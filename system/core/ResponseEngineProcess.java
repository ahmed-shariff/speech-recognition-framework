package system.core;

public class ResponseEngineProcess{
	private ResponseEngineFunctionsEnum processFunction;
	private String additionalParameter;
	public ResponseEngineProcess(ResponseEngineFunctionsEnum processFunction,
			String additionalParameters) {
		this.processFunction = processFunction;
		this.additionalParameter = additionalParameters;
	}
	public ResponseEngineFunctionsEnum getProcessFunction() {
		return processFunction;
	}
	public String getAdditionalParameter() {
		return additionalParameter;
	}
}