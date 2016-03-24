package system.Modules.Core.SubModules;

import system.Modules.Core.Module;
import system.Modules.Core.RecognizerModule;

public interface DictionaryModule extends Module, RecognizerModule {
	public final String moduleSuperName=DictionaryModule.class.getName(); 

	public abstract int build();
	
	public abstract String getModelPath();

	public abstract String getModuleName();
}
