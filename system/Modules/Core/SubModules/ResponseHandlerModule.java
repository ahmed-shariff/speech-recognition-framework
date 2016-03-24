package system.Modules.Core.SubModules;

import java.util.Queue;

import system.Modules.Core.Module;
import system.core.Response;
import system.core.ResponseEngineProcess;

public interface ResponseHandlerModule extends Module {
	public final String moduleSuperName=ResponseHandlerModule.class.getName();
	public abstract String getModuleName();
	
	public abstract void generateProcessQueue(Response response);
	
	public abstract Queue<ResponseEngineProcess> getPreLoadingQueue();

	public abstract Queue<ResponseEngineProcess> getProcessQueue();
}
