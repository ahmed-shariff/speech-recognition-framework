package system.Modules.Default;

import system.Modules.Core.SubModules.LanguageModule;

public class DefaultGrammerModule implements LanguageModule {

	private final String modelPath="resource:/models";
	private final String modelName="grammar";
	private final String moduleName=DefaultGrammerModule.class.getSimpleName();
	public DefaultGrammerModule(){
		//modelPath=System.getProperty("user.dir");
	}
	
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
		return modelName;
	}

	public boolean isGrammer() {
		return true;
	}

	public String getModuleSuperName() {
		return moduleSuperName;
	}

}
