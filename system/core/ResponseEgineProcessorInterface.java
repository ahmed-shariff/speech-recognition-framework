package system.core;

import system.Modules.Core.ModuleSet;

public interface ResponseEgineProcessorInterface {

	void execute(Response response);
	void setUpResponseEngine();
	/**
	 * The {@linkplain ModuleSet} being used in the engine will be returned.</br>
	 * @return copy of the {@linkplain ModuleSet} being used in the engine.</br>
	 * 
	 */
	public ModuleSet getModuleSet();
	
}
