/**
 * 
 */
package system.core;

import java.io.IOException;
import java.util.concurrent.Phaser;

import system.Modules.Core.SubModules.AcousticModule;
import system.Modules.Core.SubModules.DictionaryModule;
import system.Modules.Core.SubModules.LanguageModule;
import system.core.RecognizerEngineComponents.LiveSpeechRecognizerExtention;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;

/*
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.recognizer.Recognizer;
*/
/**
 * @author Ahmed Shariff
 *
 */
final class RecognizerEngine implements RecognizerEngineInterface,RecognizerEngnePauseInterface{
	private static RecognizerEngine engineInstance=null;
	
	private final Phaser barrier=new Phaser(2);
	private final Phaser pauseBarrier=new Phaser(1);
	
	private ResponseEngineInreface responseEngine=null;
	private volatile Configuration recognizerConfiguration=null;
	private LiveSpeechRecognizerExtention recognizer=null;
	private Response response;
	private SpeechResult result;
	private volatile boolean resetRecognizer=false;
	private volatile boolean recognitionInurrupted=false;
	private boolean runningParametersSet=false;
	private boolean pauseState=false;
	
	
	
	private RecognizerEngine(){		
		recognizerConfiguration=new Configuration();
	}
	
	public static synchronized RecognizerEngine getRecognizerEngine() throws IOException{
		if(engineInstance==null){
			engineInstance=new RecognizerEngine();
		}
		return engineInstance;
	}
	
	
	public void run() {
		boolean run=true;
		if(!runningParametersSet){
			System.err.println("RECOGNIER_ENGINE: Running Parameters not set, use method: injectRunningParameters");
			run=false;
		}else{
			try {
				System.out.println("INITIAL ACOUSTIC MODULE: "+recognizerConfiguration.getAcousticModelPath());
				System.out.println("INITIAL DICTIONARY MODULE: "+ recognizerConfiguration.getDictionaryPath());
				System.out.println("INITIAL GRAMMER USE: "+recognizerConfiguration.getUseGrammar());
				if(recognizerConfiguration.getUseGrammar())
					System.out.println(recognizerConfiguration.getGrammarName());
				else
					System.out.println(recognizerConfiguration.getLanguageModelPath());
					recognizer=new LiveSpeechRecognizerExtention(recognizerConfiguration);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(run){			
			pauseBarrier.arriveAndAwaitAdvance();
			resetRecognizer(resetRecognizer);
			System.out.println("RECOGNIZER_ENGINE: running");
			System.out.println("GRAMMER USE: "+recognizerConfiguration.getUseGrammar());
			if(recognizerConfiguration.getUseGrammar())
				System.out.println("GRAMMAR: "+recognizerConfiguration.getGrammarName());
			else
				System.out.println("LANGUAGE MODEL: "+recognizerConfiguration.getLanguageModelPath());
			recognizer.startRecognition(true);
			result=recognizer.getResult();
			recognizer.stopRecognition();
			response=new Response(result);
			if(!recognitionInurrupted){
				System.out.println("RECOGNIZER_ENGINE: passing new response");
				responseEngine.newResponse(response);
				responseEngine.continueResponseProcess();
			}
			else
				recognitionInurrupted=false;
			barrier.arriveAndAwaitAdvance();
		}
	}
	
	public void setLanguageModule(LanguageModule module){
		System.out.println("RECOGNIER_ENGINE: Setting model:"+ module.getModuleName());
		if(module.isGrammer()){
			System.out.println(module.getModelName());
			recognizerConfiguration.setGrammarPath(module.getModelPath());
			recognizerConfiguration.setGrammarName(module.getModelName());
			recognizerConfiguration.setUseGrammar(true);
		}else{
			recognizerConfiguration.setLanguageModelPath(module.getModelPath());
			recognizerConfiguration.setUseGrammar(false);
		}
		resetRecognizer=true;
	}
	
	public void setAcousticModule(AcousticModule aModule){
		System.out.println("RECOGNIER_ENGINE: Setting model:"+ aModule.getModuleName());
		recognizerConfiguration.setAcousticModelPath(aModule.getModelPath());
		
		resetRecognizer=true;
	}
	
	public void setDictionaryModule(DictionaryModule dModule){
		recognizerConfiguration.setDictionaryPath(dModule.getModelPath());
		System.out.println("RECOGNIER_ENGINE: Setting model:"+ dModule.getModuleName() );
		
		resetRecognizer=true;
	}
	
	private void resetRecognizer(boolean resetRecognizer){
		if(!resetRecognizer)
			return;
		System.out.println("RECOGNIER_ENGINE: Resetting recognizer");
		recognizer.closeRecognitionLine();
		try {
			recognizer=new LiveSpeechRecognizerExtention(recognizerConfiguration);
			resetRecognizer=false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void continueRecognition(){
		recognitionInurrupted=false;
		barrier.arrive();
	}
	
	public void stopRecognition(){
		System.out.println("RECOGNIZER_ENGINE: setting INTURRUPT.......");
		recognitionInurrupted=true;
	}
	
	public void setPauseRecognitionEngine(boolean state){
		if(pauseState != state){
			pauseState=state;
			if(state ==true){
				System.out.println("RECOGNIZER_ENGINE: paused");
				pauseBarrier.register();
			}else if (state == false){
				System.out.println("RECOGNIZER_ENGINE: resumed");
				pauseBarrier.arriveAndDeregister();
			}
		}
	}
	
	
	public void injectRunningParamenters(ResponseEngineInreface re){
		if(!runningParametersSet){
			responseEngine=re;/*
			recognizerConfiguration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
			
			recognizerConfiguration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
			recognizerConfiguration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.dmp");*/
			//recognizerConfiguration.setGrammarPath("res");
			//recognizerConfiguration.setGrammarName("grammar");
			//recognizerConfiguration.setUseGrammar(false);
			
			
			runningParametersSet=true;
		}else
			throw new IllegalAccessError("RECOGNIER_ENGINE: Running parameters have been already injected.");
	}

}
