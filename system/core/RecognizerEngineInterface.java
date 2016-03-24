package system.core;

import system.Modules.Core.SubModules.AcousticModule;
import system.Modules.Core.SubModules.DictionaryModule;
import system.Modules.Core.SubModules.LanguageModule;

interface RecognizerEngineInterface extends Runnable {
	void setLanguageModule(LanguageModule module);
	void setAcousticModule(AcousticModule aModule);
	void setDictionaryModule(DictionaryModule dModule);
	void continueRecognition();
	void stopRecognition();
	void injectRunningParamenters(ResponseEngineInreface re);
}
