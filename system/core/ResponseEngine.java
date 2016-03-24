package system.core;

import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import system.Modules.Core.Module;
import system.Modules.Core.RecognizerModule;
import system.Modules.Core.SubModules.AcousticModule;
import system.Modules.Core.SubModules.DictionaryModule;
import system.Modules.Core.SubModules.LanguageModule;

/**
 * @author Ahmed Shariff
 *
 */
final class ResponseEngine implements ResponseEngineInreface {
	
	private static ResponseEngine engineInstance=null;
	
	private final Phaser barrierTocontinueResponse=new Phaser(2);
	private final Phaser barrierToHaltForBuild=new Phaser(2);
	private RecognizerEngineInterface recognizer;
	private SystemEngineInterface systemEngine;
	private ResponseEgineProcessorInterface processor;
	private BlockingQueue<Response> responseOutQueue;
	private BlockingQueue<Response> currentRersponse=new ArrayBlockingQueue<Response>(1);
	private Response tempResponse=null;
	private Response outBoundResponse;
	private boolean runningParametersSet=false;
	private volatile boolean responseEngineWaiting=false;
	private boolean haltResponseProcessForBuild=false;
	
	private ResponseEngine(){}
	/**
	 * {@inheritDoc}
	 * <p>This method can be invoked only once. Attempting to re-invoke the method will throw an {@code IllegalAccessException}.</p>
	 * The {@code ModuleSet} passed through this method must contain {@linkplain Module}'s of types:
	 * <ul>
	 * <li>{@linkplain AcousticModule}</li>
	 * <li>{@linkplain DictionaryModule}</li>
	 * <li>{@linkplain LanguageModule}</li>
	 * <li>{@linkplain ResponseHandlerModule{</li>
	 * </ul>
	 * If {@code ModuleSet} does not contain at least one {@code Mdoule} of each above {@code Module} types, a {@code IllegalArgumentException} will be thrown.</br>
	 * and also if a {@code Cancel} does not exist under the {@code Module} type {@code ResponseHandlerModule}, an {@code IllegalArgumentException} will be thrown. 
	 */
	public void injectRunningParameters(ResponseEgineProcessorInterface processor,RecognizerEngineInterface recognizer,
			SystemEngineInterface systemEngine, BlockingQueue<Response> blockingQueue){
		if(!runningParametersSet){
			this.processor=processor;
			this.recognizer=recognizer;
			this.systemEngine=systemEngine;
			this.responseOutQueue=blockingQueue;
			processor.setUpResponseEngine();
			runningParametersSet=true;
		}else{
			throw new IllegalAccessError("RESPONSE_ENGINE: Running parameters have been already injected.");
		}
	}
	
	public static synchronized ResponseEngine getResponseEngine(){
		if(engineInstance == null)
			engineInstance=new ResponseEngine();
		return engineInstance;
	}
	
	
	public void run(){
		boolean run=true;
		if(!runningParametersSet){
			System.err.println("RESPONSE_ENGINE: Running parameters not set, use method: injectRunningParameters");
			run=false;
		}
		
		while(run){
			System.out.println("RESPONSE_ENGINE: awaiting new response");
			barrierTocontinueResponse.arriveAndAwaitAdvance();
			if(haltResponseProcessForBuild){
				systemEngine.continueToBuild();
				barrierToHaltForBuild.arriveAndAwaitAdvance();
			}
			
			System.out.println("RESPONSE_ENGINE: passing to processor");
			tempResponse=currentRersponse.poll();
			System.out.println("RESPONSE_ENGINE: response is null:" + (tempResponse==null));
			if(tempResponse !=null)
				System.out.println("RESPONSE_ENGINE: Response:"+tempResponse.getRecognizerResult().getHypothesis());
			processor.execute(tempResponse);
			recognizer.continueRecognition();
		}
	}
	

	public LinkedList<Response> getResponseOutList() {
		return new LinkedList<Response>(responseOutQueue);
	}

	/**
	 * {@inheritDoc}
	 */
	public void newResponse(Response response){
		System.out.println("RESPONSE_ENGINE: new response");
		currentRersponse.offer(response);
	}
	/**
	 * {@inheritDoc}
	 * When invoked a flag is set to continue process and the {@code Engine}'s thread is notified.
	 */
	public void continueResponseProcess(){
		barrierTocontinueResponse.arrive();
	}
	
	/**
	 * {@inheritDoc}
	 * When the flag is set, the next incoming {@linkplain Response} will not be processed until the flag is set to {@code FALSE}
	 */
	public void setHaltResponseProcessForBuild(boolean haltResponseProcessForBuild) {
		if(this.haltResponseProcessForBuild==true && haltResponseProcessForBuild==false){
			this.haltResponseProcessForBuild = haltResponseProcessForBuild;
			barrierToHaltForBuild.arrive();
		}else if(this.haltResponseProcessForBuild==false && haltResponseProcessForBuild==true){
			this.haltResponseProcessForBuild=haltResponseProcessForBuild;
		}
	}
	
	
	public void setRecognizerModule(RecognizerModulesEnum moduleType,
			RecognizerModule module) {
		switch(moduleType){
		case ACOUSTIC_MODULE:
			recognizer.setAcousticModule((AcousticModule) module);
			break;
		case DICTIONARY_MODULE:
			recognizer.setDictionaryModule((DictionaryModule)module);
			break;
		case LANGUAGE_MODULE:
			recognizer.setLanguageModule((LanguageModule)module);
			break;
		default:
			throw new IllegalArgumentException("RESPONSE_ENGINE: The enum value for moduleType is not recognized");
		}
		
	}
	public ResponseEgineProcessorInterface getProcessor() {
		return processor;
	}
	
	public void setOutBoundResponse(Response response) {
		if((response.getValue(Response.ReponseFields.SYSTEM_PROCESS_KEY)) == null || response.getValue(Response.ReponseFields.GENERATOR_MODULE) == null)
			throw new IllegalArgumentException("RESPONSE_ENGINE: The response must be passed through a ResponseGeneratorModule which set the fields SYSTEM_PROCESS_KEY and GENERATOR_MODULE");
		outBoundResponse=response;
		responseEngineWaiting=false;
	}
	
	public void passOutBoundResponseToResponseOutQueue(){
		System.out.println("RESPONSE_ENGINE: passing response to system engine");
		responseOutQueue.add(outBoundResponse);
		outBoundResponse=null;
	}
	
	public void wakeResponseEngineIn(final long waitPeriod){
		System.out.println("RESPONSE_ENGINE: STARTING TO WAIT");
		responseEngineWaiting=true;
		final CyclicBarrier b=new CyclicBarrier(2);
		(new Thread(new Runnable() {
			
			public void run() {
				try {
					b.await(waitPeriod, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TimeoutException e) {
					System.out.println("RESPONSE_ENGINE: Wait timed out");
				}
				System.out.println("RESPONSE_ENGINE: WAKING RESPONSE ENGINE");
				System.out.println("RESPONSE_ENGINE: engine still waiting:"+responseEngineWaiting);
				if(responseEngineWaiting){
					recognizer.stopRecognition();
					continueResponseProcess();
					responseEngineWaiting=false;
				}
			}
		})).start();
	}
}
