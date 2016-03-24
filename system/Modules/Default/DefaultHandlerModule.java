package system.Modules.Default;

import java.util.LinkedList;
import java.util.Queue;

import system.Modules.Core.SubModules.ResponseHandlerModule;
import system.core.RecognizerModulesEnum;
import system.core.Response;
import system.core.ResponseEngineFunctionsEnum;
import system.core.ResponseEngineProcess;

public class DefaultHandlerModule implements ResponseHandlerModule {

	private final String moduleName=DefaultHandlerModule.class.getSimpleName();
	private Queue<ResponseEngineProcess> processQueue;
	private final String preProcessAcousticModule=DefaultAcousticModule.class.getSimpleName();
	private final String preProcessDictionaryModule=DefaultDictionaryModule.class.getSimpleName();
	private final String preProcessLanguageModule=DefaultGrammerModule.class.getSimpleName();
	private final String preProcessGenaratorModule=DefaultGeneratorModule.class.getSimpleName();
	public String getModuleName() {
		return moduleName;
	}

	public void generateProcessQueue(Response response) {
		System.out.println("Default handler running");
		processQueue=new LinkedList<ResponseEngineProcess>();
		response.setNewValue(Response.ReponseFields.HANDLER_MODULE, getModuleName());
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.PASS_TO_GENERATOR, null));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SET_REGISTER_2, getModuleName()));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SET_REGISTER_1, Cancel.class.getSimpleName()));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SWITCH_MODULE_HANDLER, null));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.WAIT, null));
		//processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.PASS_TO_SYSTEM, null));
	}

	public Queue<ResponseEngineProcess> getProcessQueue() { 
		return processQueue;
	}

	public Queue<ResponseEngineProcess> getPreLoadingQueue() {
		processQueue= new LinkedList<ResponseEngineProcess>();
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SET_REGISTER_1, preProcessAcousticModule));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SWITCH_MODULE_RECOGNIZER, RecognizerModulesEnum.ACOUSTIC_MODULE.value()));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SET_REGISTER_1, preProcessDictionaryModule));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SWITCH_MODULE_RECOGNIZER, RecognizerModulesEnum.DICTIONARY_MODULE.value()));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SET_REGISTER_1, preProcessLanguageModule));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SWITCH_MODULE_RECOGNIZER, RecognizerModulesEnum.LANGUAGE_MODULE.value()));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SET_REGISTER_1, preProcessGenaratorModule));
		processQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.SWITCH_MODULE_GENERATOR,null));
		return processQueue;
	}

	public String getModuleSuperName() {
		return moduleSuperName;
	}

}
