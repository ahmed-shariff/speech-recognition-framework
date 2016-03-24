package system.Modules.Default;

import system.Modules.Core.SubModules.AcousticModule;

public class DefaultAcousticModule implements AcousticModule {

	private final String modelPath="resource:/models/en-us/en-us";
	
	public DefaultAcousticModule(){
	}
	
	public int build() {
		return 0;
	}

	public String getModelPath() {
		return modelPath;
	}

	public String getModuleName() {
		return DefaultAcousticModule.class.getSimpleName();
	}

	public String getModuleSuperName() {
		return moduleSuperName;
	}

}
