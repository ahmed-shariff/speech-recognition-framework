package system.core;

import edu.cmu.sphinx.api.SpeechResult;

public class Response {
	public static enum ReponseFields{
		GENERATOR_RESPONSE,
		GENERATOR_MODULE,
		HANDLER_MODULE,
		LANGUAGE_MODULE,
		SYSTEM_PROCESS_KEY
	}
	
	private SpeechResult recognizerResult=null;
	private String systemProcessKey=null;
	private String generatorResponse=null;
	private String generatorModule=null;
	private String handlerModule=null;
	private String languageModule=null;
	
	public Response(SpeechResult result){
		this.recognizerResult=result;
	}
	
	public void setNewValue(ReponseFields field, String value){
		switch(field){
		case GENERATOR_RESPONSE:
			if(generatorResponse!= null)
				throwIllegalArgumentException("Generator response");
			else
				generatorResponse=value;
			break;
		case GENERATOR_MODULE:
			if(generatorModule != null)
				throwIllegalArgumentException("Generator Module name");
			else
				generatorModule=value;
			break;
		case HANDLER_MODULE:
			if(handlerModule != null)
				throwIllegalArgumentException("Handler Module name");
			else
				handlerModule=value;
			break;
		case LANGUAGE_MODULE:
			if(languageModule != null)
				throwIllegalArgumentException("Language Module name");
			else
				languageModule=value;
			break;
		case SYSTEM_PROCESS_KEY:
			if(systemProcessKey != null)
				throwIllegalArgumentException("Syetem process key");
			else
				systemProcessKey=value;
		}
	}
	
	public SpeechResult getRecognizerResult(){
		return recognizerResult;
	}
	
	public String getValue(ReponseFields field){
		switch(field){
		case GENERATOR_RESPONSE:
			if(generatorResponse== null)
				throwNullPointerException("Generator response");
			else
				return generatorResponse;
			break;
		case GENERATOR_MODULE:
			if(generatorModule == null)
				throwNullPointerException("Generator Module name");
			else
				return generatorModule;
			break;
		case HANDLER_MODULE:
			if(handlerModule == null)
				throwNullPointerException("Handler Module name");
			else
				return handlerModule;
			break;
		case LANGUAGE_MODULE:
			if(languageModule == null)
				throwNullPointerException("Language Module name");
			else
				return languageModule;
			break;
		case SYSTEM_PROCESS_KEY:
			if(systemProcessKey == null)
				throwNullPointerException("System process key");
			else
				return systemProcessKey;
			break;
		}
		return null;
	}
	
	private void throwIllegalArgumentException(String str) throws IllegalArgumentException{
		throw new IllegalArgumentException("Cannot set "+str+"\nValue cannot be set twice");
	}
	
	private void throwNullPointerException(String str)throws NullPointerException{
		throw new NullPointerException(str+" has not been set.");
	}
}
