package system.Modules.Core.SubModules;

import system.Modules.Core.Module;
import system.Modules.Core.RecognizerModule;

public interface LanguageModule extends RecognizerModule,Module {
	public final String moduleSuperName=LanguageModule.class.getName();
	
	public abstract int build();

	public abstract String getModelPath();
	
	public abstract String getModuleName();
	
	public abstract String getModelName();
	
	public abstract boolean isGrammer();

}
