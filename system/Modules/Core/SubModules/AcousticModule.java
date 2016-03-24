package system.Modules.Core.SubModules;

import system.Modules.Core.Module;
import system.Modules.Core.RecognizerModule;

public interface AcousticModule extends RecognizerModule, Module {
	public final String moduleSuperName= AcousticModule.class.getName();
	
	public abstract int build();

	public abstract String getModelPath();
	
	public abstract String getModuleName();

}
