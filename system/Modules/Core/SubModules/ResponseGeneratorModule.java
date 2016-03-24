package system.Modules.Core.SubModules;

import system.Modules.Core.Module;
import system.core.Response;

public interface ResponseGeneratorModule extends Module {
	public final String moduleSuperName=ResponseGeneratorModule.class.getName();
	public abstract String getModuleName();

	public Response generateResponse(Response response);
}
