package system.Modules.Core.SubModules;

import system.Modules.Core.Module;
import system.core.Response;

public interface SystemModule extends Module {

	public final String moduleSuperName=SystemModule.class.getName();
	public abstract String getModuleName();
	public abstract void executeSystemModule(Response response);
}
