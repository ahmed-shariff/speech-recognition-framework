package system.core;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import system.Modules.Core.Module;
import system.Modules.Core.ModuleSet;
import system.Modules.Core.RecognizerModule;
import system.Modules.Core.SubModules.AcousticModule;
import system.Modules.Core.SubModules.DictionaryModule;
import system.Modules.Core.SubModules.LanguageModule;
import system.Modules.Core.SubModules.ResponseGeneratorModule;
import system.Modules.Core.SubModules.ResponseHandlerModule;
import system.Modules.Core.SubModules.WaitableResponseHandlerModule;
/**
 * 
 * @author Ahmed Shariff
 *
 */
public class ResponseEgineProcessor implements ResponseEgineProcessorInterface {

	private final String[] compulsaryModules={Modules.ACOUSTIC_MODULE.value(),Modules.DICTIONARY_MODULE.value(),Modules.LANGUGE_MODULE.value(),
			Modules.RESPONSE_GENARATOR_MODULE.value(),Modules.RESPONSE_HANDLER_MODULE.value()};
	private final ResponseEngineInreface responseEngine;
	private final ModuleSet moduleset;
	private final ConcurrentLinkedQueue<ResponseEngineProcess> responseProcessQueue;
	private final long waitPeriod;
	private Response currentResponse;
	private String RESPONS_ENGINE_REGISTER_1;
	private String RESPONS_ENGINE_REGISTER_2;
	private ResponseHandlerModule activeResponseHandlerModule;
	private ResponseGeneratorModule activeResponseGeneratorModule;
	private int numberOfExtraProcessesAdded;
	
	/**
	 * @param responseEngine - The reference to the {@linkplain ResponseEngineInreface} being used in the system. 
	 * <b>Note:</b> The reference that is passed here doesn't have to have it's dependencies injected
	 * @param moduleSet - The {@linkplain ModuleSet} to be used in this system. The module must have the following types:
	 * <ul>
	 * <li>{@linkplain AcousticModule}</li>
	 * <li>{@linkplain DictionaryModule}</li>
	 * <li>{@linkplain LanguageModule}</li>
	 * <li>{@linkplain ResponseGeneratorModule}</li>
	 * <li>{@linkplain ResponseHandlerModule}</li>
	 * </ul>
	 * @param waitPeriod - The length of the period the response engine has to wake up again in when process {@linkplain ResponseEngineFunctionsEnum#WAIT} is initiated.
	 * The Period is in milli-seconds.
	 */
	public ResponseEgineProcessor(ResponseEngineInreface responseEngine,ModuleSet moduleSet,long waitPeriod) {
		this.moduleset=moduleSet;
		this.responseEngine=responseEngine;
		this.waitPeriod=waitPeriod;
		if(!checkModulesInModuleSet(moduleset)){
			StringBuilder str=new StringBuilder("RESPONSE_PROCESSOR: The module set must contain following Modules:");
			for(String name:compulsaryModules)
				str.append("\n\t"+name);
			throw new IllegalArgumentException(str.toString());
		}
		this.responseProcessQueue=new ConcurrentLinkedQueue<ResponseEngineProcess>();
		activeResponseHandlerModule=(ResponseHandlerModule) moduleSet.getActiveModule(Modules.RESPONSE_HANDLER_MODULE.value());
		System.out.println("RESPONSE_PROCESSOR: Active Handler:"+ activeResponseHandlerModule.getModuleName());
		activeResponseGeneratorModule=(ResponseGeneratorModule) moduleSet.getActiveModule(Modules.RESPONSE_GENARATOR_MODULE.value());
		System.out.println("RESPONSE_PROCESSOR: Active generator:"+ activeResponseGeneratorModule.getModuleName());
		numberOfExtraProcessesAdded=0;
	}
	
	private boolean checkModulesInModuleSet(ModuleSet moduleset){
		boolean flag=true;
		Map<String, Map<String, Module>> m = moduleset.getModules();
		for(String str:compulsaryModules)
			flag = flag && m.containsKey(str) && !m.get(str).isEmpty();
		return flag;
	}
	
	public void execute(Response response) {
		ResponseEngineProcess process;
		if(response!=null || (response==null && activeResponseHandlerModule instanceof WaitableResponseHandlerModule)){
			this.currentResponse=response;
			activeResponseHandlerModule.generateProcessQueue(response);
			if(activeResponseHandlerModule.getProcessQueue() != null)
				responseProcessQueue.addAll(activeResponseHandlerModule.getProcessQueue());
			while(!responseProcessQueue.isEmpty()){
				process=responseProcessQueue.poll();
				System.out.println("RESPONSE_PROCESSOR: Processing:"+process.getProcessFunction().name()+"\t left:"+responseProcessQueue.size());
				executeProcess(process);
			}
		}
		System.out.println("RESPONSE_PROCESSOR: Process end");
		numberOfExtraProcessesAdded=0;
	}
	
	public void setUpResponseEngine(){
		responseEngine.setRecognizerModule(RecognizerModulesEnum.ACOUSTIC_MODULE, (RecognizerModule) moduleset.getActiveModule(Modules.ACOUSTIC_MODULE.value()));
		responseEngine.setRecognizerModule(RecognizerModulesEnum.DICTIONARY_MODULE, (RecognizerModule)moduleset.getActiveModule(Modules.DICTIONARY_MODULE.value()));
		responseEngine.setRecognizerModule(RecognizerModulesEnum.LANGUAGE_MODULE, (RecognizerModule)moduleset.getActiveModule(Modules.LANGUGE_MODULE.value()));
		
	}
	
	private void executeProcess(ResponseEngineProcess currentProcess) //throws IOException
	{
		switch(currentProcess.getProcessFunction()){
		case PASS_TO_GENERATOR:
			responseEngine.setOutBoundResponse(activeResponseGeneratorModule.generateResponse(currentResponse));
			break;
		case PASS_TO_SYSTEM:
			if(!responseProcessQueue.isEmpty())
				if(numberOfExtraProcessesAdded>0)
					responseProcessQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.PASS_TO_SYSTEM, null));
				else
					throw new IllegalArgumentException("RESPONSE_PROCESSOR: PASS_TO_SYSTEM must be the last call in a process stack produced by the handler");
			else
				responseEngine.passOutBoundResponseToResponseOutQueue();
			break;
		case SWITCH_MODULE_GENERATOR:
			System.out.println("RESPONSE_PROCESSOR: Switching generator "+RESPONS_ENGINE_REGISTER_1+"\t"+activeResponseGeneratorModule.getModuleName());
			if(!activeResponseGeneratorModule.getModuleName().equalsIgnoreCase(RESPONS_ENGINE_REGISTER_1))
				activeResponseGeneratorModule=(ResponseGeneratorModule) moduleset.setActiveModule(Modules.RESPONSE_GENARATOR_MODULE.value()
					, RESPONS_ENGINE_REGISTER_1);
			break;
		case SWITCH_MODULE_HANDLER:
			System.out.println("RESPONSE_PROCESSOR: Switching handler: "+RESPONS_ENGINE_REGISTER_1+"\t"+activeResponseHandlerModule.getModuleName());
			if(!activeResponseHandlerModule.getModuleName().equalsIgnoreCase(RESPONS_ENGINE_REGISTER_1)){
				activeResponseHandlerModule=(ResponseHandlerModule) moduleset.setActiveModule(Modules.RESPONSE_HANDLER_MODULE.value()
						,RESPONS_ENGINE_REGISTER_1);
				Queue<ResponseEngineProcess> q=activeResponseHandlerModule.getPreLoadingQueue();
				numberOfExtraProcessesAdded+=q.size();
				while(!q.isEmpty()){
					responseProcessQueue.add(q.poll());
				}
			}
			break;
		case SWITCH_MODULE_RECOGNIZER:
			if(currentProcess.getAdditionalParameter() == null)
				throw new IllegalArgumentException("RESPONSE_PROCESSOR: The additionaParameter for function SWITCH_MODULE_RECOGNIZER cannot be null");
			boolean matched=false;
			for(RecognizerModulesEnum e:RecognizerModulesEnum.values()){
				if(currentProcess.getAdditionalParameter().equalsIgnoreCase(e.value())){
					System.out.println(e.value());
					System.out.println(moduleset.getActiveModule(e.value()).getModuleName());
					System.out.println(RESPONS_ENGINE_REGISTER_1);
					if(!moduleset.getActiveModule(e.value()).getModuleName().equalsIgnoreCase(RESPONS_ENGINE_REGISTER_1)){
						try {
							System.out.println("RESPONSE_PROCESSOR: Switching  module :"+RESPONS_ENGINE_REGISTER_1);
							switchModelOfRecognizer(e, RESPONS_ENGINE_REGISTER_1);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							System.exit(-1);
						}
					}
					matched=true;
				}
			}
			if(!matched)
				throw new IllegalArgumentException("RESPONSE_PROCESSOR: The parameter for SWITCH_MODULE_RECOGNIZER process does not match allowed values. "
						+ "Vlaues must be:\n"+RecognizerModulesEnum.getAllValueNames());
			break;
		case SET_REGISTER_1:
			RESPONS_ENGINE_REGISTER_1=currentProcess.getAdditionalParameter();
			break;
		case SET_REGISTER_2:
			RESPONS_ENGINE_REGISTER_2=currentProcess.getAdditionalParameter();
			break;
		case MOVE_REGISTER_VALUE:
			if(currentProcess.getAdditionalParameter().equalsIgnoreCase("2"))
				RESPONS_ENGINE_REGISTER_1=RESPONS_ENGINE_REGISTER_2;
			else if(currentProcess.getAdditionalParameter().equalsIgnoreCase("1"))
				RESPONS_ENGINE_REGISTER_2=RESPONS_ENGINE_REGISTER_1;
			else
				throw new IllegalArgumentException("RESPONSE_PROCESSOR: The parameter for MOVE_REGISTER_VALUE process does not match allowed values. "
						+"Values must be: \n\t1\n\t2");
			break;
		case WAIT:
			if(!responseProcessQueue.isEmpty())
				if(numberOfExtraProcessesAdded>0)
					responseProcessQueue.add(new ResponseEngineProcess(ResponseEngineFunctionsEnum.WAIT, null));
				else
					throw new IllegalArgumentException("RESPONSE_PROCESSOR: WAIT must be the last call in a process stack produced by the handler");
			else{
				System.out.println("RESPONSE_PROCESSOR: calling wait");
				responseEngine.wakeResponseEngineIn(waitPeriod);
			}
			break;
		default:
			throw new IllegalArgumentException("RESPONSE_PROCESSOR: The enum value is not recognized by the response engine processor");
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <b>Note:</b> 
	 * <ul>
	 * <li>Making changes to the {@linkplain ModuleSet} returned by this method will not be reflected in the {@linkplain ModuleSet} of the engine.</li>
	 * <li>Any changes made to the {@linkplain ModuleSet} used in the engine after the invoking this method will not be reflected in the returned {@linkplain ModuleSet}</li>
	 * </ul>
	 */
	public ModuleSet getModuleSet(){
		return (ModuleSet) moduleset.clone();
	}
	
	
	private void switchModelOfRecognizer(RecognizerModulesEnum module,String moduleName) throws IOException{
		Module m;
		if(module == RecognizerModulesEnum.ACOUSTIC_MODULE){
			if(!moduleset.hasModule(Modules.ACOUSTIC_MODULE.value(), moduleName))
				throw new IllegalArgumentException("RESPONSE_PROCESSOR: There is no module with name \""+moduleName+"\" in registered set of ACOUSTIC_MODULE's");
			m=moduleset.setActiveModule(Modules.ACOUSTIC_MODULE.value(),moduleName);
			responseEngine.setRecognizerModule(RecognizerModulesEnum.ACOUSTIC_MODULE, (AcousticModule) m);
		}
		else if( module == RecognizerModulesEnum.DICTIONARY_MODULE){
			if(!moduleset.hasModule(Modules.DICTIONARY_MODULE.value(), moduleName))
				throw new IllegalArgumentException("RESPONSE_PROCESSOR: There is no module with name \""+moduleName+"\" in registered set of DICTIONARY_MODULE's");
			m=moduleset.setActiveModule(Modules.DICTIONARY_MODULE.value(), moduleName);
			responseEngine.setRecognizerModule(RecognizerModulesEnum.DICTIONARY_MODULE, (DictionaryModule) m);
		}else if (module == RecognizerModulesEnum.LANGUAGE_MODULE) {
			if(!moduleset.hasModule(Modules.LANGUGE_MODULE.value(), moduleName))
				throw new IllegalArgumentException("RESPONSE_PROCESSOR: There is no module with name \""+moduleName+"\" in registered set of LANGUAGE_MODULE's");
			m=moduleset.setActiveModule(Modules.LANGUGE_MODULE.value(), moduleName);
			responseEngine.setRecognizerModule(RecognizerModulesEnum.LANGUAGE_MODULE, (LanguageModule) m);
		}
	}

}
