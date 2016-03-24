package system.core;

import java.awt.SystemColor;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import system.GUI.SystemOutputStream;
import system.GUI.SystemOutputWindow;
import system.GUI.SystemTrayApplication;
import system.Modules.Core.Module;
import system.Modules.Core.ModuleFactory;
import system.Modules.Core.ModuleSet;
import system.Modules.Core.ModuleSetMap;
import system.Modules.Default.DefaultAcousticModule;

public class Driver {

	public static void main(String[] args) throws InterruptedException, SAXException, IOException, ParserConfigurationException, URISyntaxException {
		final String PLUGIN_FILE_NAME="Plugin files";
		final String DEFAULT_XML_FILE_NAME="DefaultConfigData.xml";
		final String CONFIG_FILE_PATH=System.getProperty("user.dir")+"\\"+"configuration.xml";
		
		//DefaultAcousticModule da=new DefaultAcousticModule();
		
		ResponseEngineInreface responseEngine= ResponseEngine.getResponseEngine();
		RecognizerEngineInterface recognizerEngine=RecognizerEngine.getRecognizerEngine();
		ResponseEgineProcessorInterface responseProcessor;
		ModuleFactory moduleFactory;
		BlockingQueue<Response> queue=new ArrayBlockingQueue<Response>(3);
		SystemEngineInterface systemEngine= SystemEngine.getSystemEngine();
		ModuleSet moduleSet;
		SystemModuleSelectorInterface moduleSelector;
		SystemOutputWindow output= new SystemOutputWindow("Output",400,SystemColor.activeCaption,SystemColor.text,true);
		final PrintWriter writer = new PrintWriter(new SystemOutputStream(output.getTextArea()));
		SystemOutputWindow console = new SystemOutputWindow("System console", 600,SystemColor.textInactiveText,SystemColor.text,false);
		PrintStream out=new PrintStream(new SystemOutputStream(console.getTextArea()));
		System.setOut(out);
		System.setErr(out);
		System.out.println("starting");
		SystemTrayApplication tray=new SystemTrayApplication("/icon.png", (RecognizerEngnePauseInterface) recognizerEngine,output, console);
		File f=new File(System.getProperty("user.dir")+"\\"+"DefaultConfigData.xml");
		System.out.println(f.exists());
		System.out.println(System.getProperty("user.dir")+"\\");
		/*
		URL x = RecognizerEngine.class.getResource("/default.config.xml");
		System.out.println(x.toString()+"\t");
		File f=new File(System.getProperty("user.dir")+"\\"+"DefaultConfigData.xml");
		System.out.println(f.exists());*/
		
		moduleFactory=new ModuleFactory(CONFIG_FILE_PATH, PLUGIN_FILE_NAME,DEFAULT_XML_FILE_NAME, Modules.DICTIONARY_MODULE.value(),Modules.ACOUSTIC_MODULE.value(),Modules.RESPONSE_HANDLER_MODULE.value(),
				Modules.LANGUGE_MODULE.value(),Modules.RESPONSE_GENARATOR_MODULE.value());
		moduleSet=new ModuleSetMap(moduleFactory);
		responseProcessor= new ResponseEgineProcessor(responseEngine, moduleSet, 5);
		responseEngine.injectRunningParameters(responseProcessor, recognizerEngine, systemEngine, queue);
		
		ModuleSet m = (ModuleSet) moduleSet.clone();
		Map<String, Map<String, Module>> moduls = m.getModules();
		System.out.println(moduls.size());
		Set<String> sss=moduls.keySet();
		Map<String, Module> ss;
		Set<String> sw;
		for(String s:sss){
			System.out.println("**************************");
			System.out.println(s);
			ss= moduls.get(s);
			System.out.println(ss.size());
			sw = ss.keySet();
			for(String sa:sw){
				System.out.println("::::" + sa +"  "+ m.getModule(s, sa).getModuleName());
			}
		}
		
		System.out.println("-------------------------------------------------------------------------");
		Map<String, Module> activeModules = m.getActiveModules();
		System.out.println(activeModules.size());
		sss=activeModules.keySet();
		for(String s:sss){
			System.out.println("**************************");
			System.out.println(s);
			System.out.println(m.getActiveModule(s).getModuleName());
		}
		
		moduleFactory=new ModuleFactory(CONFIG_FILE_PATH, PLUGIN_FILE_NAME, DEFAULT_XML_FILE_NAME, Modules.SYSTEM_ENGINE_MOULE.value());
		moduleSet=new ModuleSetMap(moduleFactory);
		moduleSelector=new SystemModuleSelectorInterface(moduleSet, writer) {
			@Override
			public Module selectModule(Response response) {
				// TODO Auto-generated method stub
				return this.moduleSet.getActiveModule(Modules.SYSTEM_ENGINE_MOULE.value());
			}
		};
		
		try {
			systemEngine.injectRunningParameters(responseEngine, queue, moduleSelector);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		recognizerEngine.injectRunningParamenters(responseEngine);
		responseProcessor.setUpResponseEngine();
		(new Thread(systemEngine,"SystemEngine")).start();
		(new Thread(recognizerEngine,"recognizerEngine")).start();
		(new Thread(responseEngine,"responseEngine")).start();
		
		/*
		System.out.println(temp.getModuleName() + " : "+ temp.getModuleSuperName() );
		tempm.put(temp.getModuleName(), temp);
		activeModules.put(temp.getModuleSuperName(),temp);
		temp= new Cancel();
		System.out.println(temp.getModuleName() + " : "+ temp.getModuleSuperName() );
		tempm.put(temp.getModuleName(), temp);
		moduls.put(temp.getModuleSuperName(), tempm);
		tempm=new HashMap<String, Module>();
		
		temp= new DefaultAcousticModule();
		System.out.println(temp.getModuleName() + " : "+ temp.getModuleSuperName() );
		tempm.put(temp.getModuleName(), temp);
		activeModules.put(temp.getModuleSuperName(),temp);
		moduls.put(temp.getModuleSuperName(), tempm);
		tempm=new HashMap<String, Module>();
		
		temp= new DefaultGrammerModule();
		System.out.println(temp.getModuleName() + " : "+ temp.getModuleSuperName() );
		tempm.put(temp.getModuleName(), temp);
		activeModules.put(temp.getModuleSuperName(),temp);
		moduls.put(temp.getModuleSuperName(), tempm);
		tempm=new HashMap<String, Module>();
		
		temp= new DefaultDictionaryModule();
		System.out.println(temp.getModuleName() + " : "+ temp.getModuleSuperName() );
		tempm.put(temp.getModuleName(), temp);
		activeModules.put(temp.getModuleSuperName(),temp);
		moduls.put(temp.getModuleSuperName(), tempm);
		tempm=new HashMap<String, Module>();
		
		temp= new DefaultGeneratorModule();
		System.out.println(temp.getModuleName() + " : "+ temp.getModuleSuperName() );
		tempm.put(temp.getModuleName(), temp);
		activeModules.put(temp.getModuleSuperName(),temp);
		moduls.put(temp.getModuleSuperName(), tempm);
		//tempm.clear();
		
		m=new ModuleSetMap(moduls, activeModules);
		
		m=(ModuleSet) m.clone();
		moduls=m.getModules();
		System.out.println(moduls.size());
		Set<String> sss=moduls.keySet();
		Map<String, Module> ss;
		Set<String> sw;
		for(String s:sss){
			System.out.println("**************************");
			System.out.println(s);
			ss= moduls.get(s);
			System.out.println(ss.size());
			sw = ss.keySet();
			for(String sa:sw){
				System.out.println("::::" + sa +"  "+ m.getModule(s, sa).getModuleName());
			}
		}
		
		System.out.println("-------------------------------------------------------------------------");
		activeModules=m.getActiveModules();
		System.out.println(activeModules.size());
		sss=activeModules.keySet();
		for(String s:sss){
			System.out.println("**************************");
			System.out.println(s);
			System.out.println(m.getActiveModule(s).getModuleName());
		}
		
		ResponseEgineProcessorInterface rp= new ResponseEgineProcessor(re, m, 5);
		
		SystemEngineInterface se= new SystemEngine();
		
		
	
		
		tempm=new HashMap<String, Module>();
		moduls.clear();
		activeModules.clear();
		temp= new DefaultSystemModule(writer);
		System.out.println("\n\t"+temp.getModuleName() + " : "+ temp.getModuleSuperName() );
		tempm.put(temp.getModuleName(), temp);
		activeModules.put(temp.getModuleSuperName(),temp);
		moduls.put(temp.getModuleSuperName(), tempm);
		
		try {
			rc=RecognizerEngine.getRecognizerEngine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m = new ModuleSetMap(moduls, activeModules);
		rc.injectRunningParamenters(re);
		re.injectRunningParameters(rp, rc, se, queue);
		try {
			se.injectRunningParameters(re, queue,new SystemModuleHandlerInterface(m){

				@Override
				public Module selectModule(Response response) {
					// TODO Auto-generated method stub
					return moduleSet.getActiveModule(SystemModule.moduleSuperName);
				}
				
			});
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		(new Thread(re)).start();
		(new Thread(rc)).start();
		(new Thread(se)).start();
		
		*/
	}

	

}
