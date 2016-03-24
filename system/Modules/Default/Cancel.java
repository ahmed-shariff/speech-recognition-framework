package system.Modules.Default;

import java.util.LinkedList;
import java.util.Queue;

import system.Modules.Core.SubModules.WaitableResponseHandlerModule;
import system.core.RecognizerModulesEnum;
import system.core.Response;
import system.core.ResponseEngineFunctionsEnum;
import system.core.ResponseEngineProcess;

public class Cancel implements WaitableResponseHandlerModule {

	private final String moduleName=Cancel.class.getSimpleName();
	private final String preLoadDictionaryModule = DefaultDictionaryModule.class.getSimpleName();
	private final String preLoadLanguageMdoule= DefaultGrammerModule.class.getSimpleName();
	private Queue<ResponseEngineProcess> processQueue;
	public String getModuleName() {
		return moduleName;
	}

	public void generateProcessQueue(Response response) {
		System.out.println("IN CANCEL");
		processQueue=new LinkedList<ResponseEngineProcess>();
		if(response == null){
			cancelFinalProcessStack();
			processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.PASS_TO_SYSTEM, null));
		}
		else if(response.getRecognizerResult().getHypothesis().contains("word")){
			System.out.println("Cancelling");
			cancelFinalProcessStack();
		}else
			System.out.println("The response was: "+response.getRecognizerResult().getHypothesis());
		System.out.println("cancel setting up");
	}

	private void cancelFinalProcessStack(){
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.MOVE_REGISTER_VALUE, "2"));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SWITCH_MODULE_HANDLER, null));
	}
	public Queue<ResponseEngineProcess> getProcessQueue() {
		return processQueue;
	}

	public Queue<ResponseEngineProcess> getPreLoadingQueue() {
		processQueue= new LinkedList<ResponseEngineProcess>();
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SET_REGISTER_1, preLoadLanguageMdoule));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SWITCH_MODULE_RECOGNIZER, RecognizerModulesEnum.LANGUAGE_MODULE.value()));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SET_REGISTER_1, preLoadDictionaryModule));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SWITCH_MODULE_RECOGNIZER, RecognizerModulesEnum.DICTIONARY_MODULE.value()));
		return processQueue;
	}

	public String getModuleSuperName() {
		return moduleSuperName;
	}

}
