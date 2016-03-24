package system.Modules.Default;

import system.Modules.Core.SubModules.ResponseGeneratorModule;
import system.core.Response;

public class DefaultGeneratorModule implements ResponseGeneratorModule {

	private final String moduleName=DefaultGeneratorModule.class.getSimpleName();
	public String getModuleName() {
		return moduleName;
	}

	public Response generateResponse(Response response) {
		response.setNewValue(Response.ReponseFields.GENERATOR_MODULE, moduleName);
		response.setNewValue(Response.ReponseFields.SYSTEM_PROCESS_KEY, DefaultSystemModule.class.getSimpleName());
		response.setNewValue(Response.ReponseFields.GENERATOR_RESPONSE, response.getRecognizerResult().getHypothesis());
		return response;
	}

	public String getModuleSuperName() {
		return moduleSuperName;
	}

}
