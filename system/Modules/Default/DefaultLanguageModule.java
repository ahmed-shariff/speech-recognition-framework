package system.Modules.Default;

import system.Modules.Core.SubModules.LanguageModule;

public class DefaultLanguageModule implements LanguageModule {

	private final String modelPath="resource:/models/en-us/en-us.lm.dmp";
	private final String moduleName=DefaultLanguageModule.class.getSimpleName();
	public int build() {
		return 0;
	}

	public String getModelPath() {
		return modelPath;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getModelName() {
		return null;
	}

	public boolean isGrammer() {
		return false;
	}

	public String getModuleSuperName() {
		return moduleSuperName;
	}

}
