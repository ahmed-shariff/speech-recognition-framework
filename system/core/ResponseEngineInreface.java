package system.core;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

import system.Modules.Core.Module;
import system.Modules.Core.ModuleSet;
import system.Modules.Core.RecognizerModule;

interface ResponseEngineInreface extends Runnable{
	
	
	/**
	 * The dependencies of this {@code Engine} must be injected via this method.
	 * @param moduleSet - The {@linkplain ModuleSet} containing the {@linkplain Module}'s used for this {@code Engine to use}. 
	 * @param recognizer - Instance of {@linkplain RecognizerEngineInterface} to be used by this {@code Engine}.
	 * @param systemEngine - Instance of {@linkplain SystemEngineInterface} to be used by this {@code Engine}
	 * @param blockingQueue - the {@codeQueue} that will be used for the this {@code Engine} to communicate with the {@code SystemEngineInterface} instance passed by this method.
	 * @throws IllegalAccessException
	 */
	public void injectRunningParameters(ResponseEgineProcessorInterface processor,RecognizerEngineInterface recognizer,
			SystemEngineInterface systemEngine, BlockingQueue<Response> blockingQueue);
	
	
	/**
	 * Any new {@code Response} the {@code ResponseEngine} needs to handle is passed via this method 
	 * @param response
	 */
	public void newResponse(Response response);
	
	
	/**
	 * The {@code LinkedList} representations of the {@code Queue} containing the {@linkplain Response}'s scheduled to be processed by a {@linkplain SystemEngineInterface}
	 * @return {@code LinkedList<Response>}
	 */
	public LinkedList<Response> getResponseOutList();
	
	
	/**
	 * When a new {@linkplain Response} is submitted via {@linkplain #newResponse(Response)} this method must be invoked for the {@code ResponseEngine} to start processing the newly received {@code Response}</br>
	 * 
	 */
	public void continueResponseProcess();
	
	
	/**
	 * If a {@linkplain RecognizerModule} is to be built, the haltProcessForBuild must be set to {@code TRUE} through this method. Once the building process is complete
	 * the haltProcessForBuild must be set to {@code FALSE} through this method.
	 */
	public void setHaltResponseProcessForBuild(boolean haltResponseProcessForBuild);
	
	public void setRecognizerModule(RecognizerModulesEnum moduleTyoe,RecognizerModule module);
	
	public ResponseEgineProcessorInterface getProcessor();
	
	public void setOutBoundResponse(Response response);
	
	public void passOutBoundResponseToResponseOutQueue();
	
	public void wakeResponseEngineIn(long waitPeriod);
	
}
