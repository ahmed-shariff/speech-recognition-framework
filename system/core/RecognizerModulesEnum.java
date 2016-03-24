package system.core;

import system.Modules.Core.SubModules.AcousticModule;
import system.Modules.Core.SubModules.DictionaryModule;
import system.Modules.Core.SubModules.LanguageModule;

public enum RecognizerModulesEnum{
	ACOUSTIC_MODULE(AcousticModule.class.getName()),
	LANGUAGE_MODULE(LanguageModule.class.getName()),
	DICTIONARY_MODULE(DictionaryModule.class.getName());

	final String name;
	RecognizerModulesEnum(String name){
		this.name=name;
	}
	
	public String value(){
		return name;
	}
	public static String getAllValueNames() {
		return ACOUSTIC_MODULE.value()+"\n"+LANGUAGE_MODULE.value()+"\n"+DICTIONARY_MODULE.value();
	}
}