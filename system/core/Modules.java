package system.core;

import system.Modules.Core.SubModules.AcousticModule;
import system.Modules.Core.SubModules.DictionaryModule;
import system.Modules.Core.SubModules.LanguageModule;
import system.Modules.Core.SubModules.ResponseGeneratorModule;
import system.Modules.Core.SubModules.ResponseHandlerModule;
import system.Modules.Core.SubModules.SystemModule;

public enum Modules {
	RESPONSE_HANDLER_MODULE(ResponseHandlerModule.class),
	RESPONSE_GENARATOR_MODULE(ResponseGeneratorModule.class),
	SYSTEM_ENGINE_MOULE(SystemModule.class),
	ACOUSTIC_MODULE(AcousticModule.class),
	LANGUGE_MODULE(LanguageModule.class),
	DICTIONARY_MODULE(DictionaryModule.class);
	
	String name;
	Class<?> clas;
	Modules(Class<?> name){
		this.clas=name;
		this.name=name.getName();
	}
	
	public String value(){
		return name;
	}
	
	public Class<?> getClassInstance(){
		return clas;
	}
}
