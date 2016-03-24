package system.Modules.Default;

import system.Modules.Core.SubModules.DictionaryModule;

public class DefaultDictionaryModule implements DictionaryModule {

	private final String moduleName=DefaultDictionaryModule.class.getSimpleName();
	private final String modelPath="resource:/models/en-us/cmudict-en-us.dict";
	public int build() {
		return 0;
	}

	public String getModelPath() {
		return modelPath;
	}

	public String getModuleName() {
		return moduleName;
	}

	public String getModuleSuperName() {
		return moduleSuperName;
	}

}
