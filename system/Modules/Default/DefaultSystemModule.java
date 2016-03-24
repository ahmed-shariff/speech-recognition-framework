package system.Modules.Default;

import java.io.PrintWriter;

import system.Modules.Core.SubModules.SystemModuleWithPrintOut;
import system.core.Response;

public class DefaultSystemModule implements SystemModuleWithPrintOut {

	private final String moduleName=DefaultSystemModule.class.getSimpleName();
	private PrintWriter out=null;
	
	public String getModuleName() {
		return moduleName;
	}

	@Override
	public void setOut(PrintWriter out){
		this.out=out;
	}
	
	public void executeSystemModule(Response response) {
		System.out.println("Default system module running");
		out.println("You said:");
		out.println(response.getValue(Response.ReponseFields.GENERATOR_RESPONSE));
		out.println("HA HA..... The jokes on you");
		out.flush();
	}

	public String getModuleSuperName() {
		return moduleSuperName;
	}
}
