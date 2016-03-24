package system.Modules.Core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import system.Modules.Default.Cancel;
import system.Modules.Default.DefaultAcousticModule;
import system.Modules.Default.DefaultDictionaryModule;
import system.Modules.Default.DefaultGeneratorModule;
import system.Modules.Default.DefaultGrammerModule;
import system.Modules.Default.DefaultHandlerModule;
import system.Modules.Default.DefaultLanguageModule;
import system.Modules.Default.DefaultSystemModule;
import system.core.Modules;

/**
 * A {@link ModuleFactory} will build and return the {@link Module}'s requested. It will return all the {@code Module}'s of all the types requested, also provide the
 * {@code Module}'s of each of those types marked as active. The {@code Map}'s returned by this implementation are consistent with each other, i.e the modules
 * in the {@code Map} returned by the  method {@linkplain ModuleFactoryInterface#getActiveModuleMap()} are also there in the
 * the {@code Map} returned by the method {@linkplain ModuleFactoryInterface#getModuleMap()}.. But, this implementation does not ensure that {@code Module}'s that are built when this class is instantiated 
 * might be built again when the another {@code ModuleFactory} is instantiated and the same {@code Module}'s are requested. The {@code Module}'s them selves must keep 
 * track of their instances.
 * @author Ahmed Shariff
 *
 */

public class ModuleFactory implements ModuleFactoryInterface {
	private class NamesFromXMLFile{
		private ArrayList<String> moduleNameList;
		private String activeModuleName;
		
		public NamesFromXMLFile(ArrayList<String> moduleNameList,
				String activeModuleName) {
			this.moduleNameList = moduleNameList;
			this.activeModuleName = activeModuleName;
		}
		ArrayList<String> getModuleNameList() {
			return moduleNameList;
		}
		String getActiveModuleName() {
			return activeModuleName;
		}
		
		boolean isEmpty(){
			return moduleNameList.isEmpty();
		}
	}
	
	private HashMap<String, Map<String, Module>> modules;
	private HashMap<String, Module> activeModules;
	private File pluginFolder;
	private LinkedList<Class<? extends Module>> classesList;
	private String tempActiveModuleName=null;
	private NodeList configNodeList=null;
	private NodeList defaultConfigNodeList=null;
	
	/**
	 * returns the {@link Map} 
	 * @param moduleSuperName
	 * @return
	 */
	private Map<String, Module> getModules(String moduleSuperName){
		tempActiveModuleName=null;
		Class<?> superClass=null;
		Map<String, Module> map=new HashMap<String, Module>();
		NamesFromXMLFile namesFromXML=null;
		
		namesFromXML=getListOfModuleNames(configNodeList, moduleSuperName);
		if(namesFromXML.isEmpty())
			namesFromXML=getListOfModuleNames(defaultConfigNodeList, moduleSuperName);
		
		for(Modules m:Modules.values()){
			if(m.value().equalsIgnoreCase(moduleSuperName))
				superClass=m.getClassInstance();
		}
		System.out.println("MODULE_FACTORY: number of total classes: "+classesList.size()+"\n\t format: ModuleSuperName  ModuleName is_a_subClass");
		for (Class<?> c : classesList) {
			System.out.println(superClass.getName()+"\n\t"+c.getName()+":implements super class:\t"+superClass.isAssignableFrom(c));
			if(superClass.isAssignableFrom(c)){
				Module m = null;
				boolean isInConfig=false;
				
				if(namesFromXML.getModuleNameList().contains(c.getSimpleName()))
					isInConfig=true;
				if(isInConfig)
					try {
						m = (Module) c.getConstructor().newInstance();
						String moduleName=m.getModuleName();
						System.out.println("MODULE_FACTORY: Loading Module:"+moduleName);
						map.put(moduleName, m);
						if(moduleName.equalsIgnoreCase(namesFromXML.getActiveModuleName()))
							tempActiveModuleName=m.getModuleName();
					} catch (InstantiationException | IllegalAccessException
							| IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
			}
		}
		if(tempActiveModuleName == null){
			JOptionPane.showMessageDialog(null,"There is no valid entry in any configuration file for an active module of type:\t"+moduleSuperName+"\nExiting", "Error in module loading...", JOptionPane.ERROR_MESSAGE );
			//System.exit(-1);
		}
		return map;
	}
	
	private Module getActiveModule(String moduleSuperName){
		System.out.println("MODULE_FACTORY: setting active module for:"+ moduleSuperName+"\n\tModule:"+modules.get(moduleSuperName).get(tempActiveModuleName).getModuleName() );
		return modules.get(moduleSuperName).get(tempActiveModuleName);
	}
	
	public ModuleFactory(String configFilePath, String pluginFolderName,String defaultConfigFileName,String... moduleSuperNames) {
		int numberOfModules=moduleSuperNames.length;
		pluginFolder=new File((System.getProperty("user.dir")+"\\"+pluginFolderName));
		try(FileInputStream fileio=new FileInputStream(new File(configFilePath));) {
			this.configNodeList=xmlLoader(fileio);
		} catch (NullPointerException|ParserConfigurationException
				| SAXException | IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"The configuration file Cannot load\nException caused: "+e.toString()+"\nDefault configuration loaded\nCheck console for details", "Error in module loading...", JOptionPane.ERROR_MESSAGE );
		}
		try(InputStream fileio=ModuleFactory.class.getResourceAsStream("/"+defaultConfigFileName);) {
			this.defaultConfigNodeList=xmlLoader(fileio);
		} catch (NullPointerException|ParserConfigurationException
				| SAXException | IOException e) {
			JOptionPane.showMessageDialog(null,"The default configuration cannot be loaded, exitting...", "Error in module loading...", JOptionPane.ERROR_MESSAGE );
			System.exit(-1);
		}
		
		if(this.defaultConfigNodeList== null && this.configNodeList==null){
			JOptionPane.showMessageDialog(null,"No configuration file detected", "Error in module loading...", JOptionPane.ERROR_MESSAGE );
			System.exit(-1);
		}
		classesList= pluginLoad(pluginFolder);
		modules=new HashMap<String, Map<String,Module>>(numberOfModules);
		activeModules=new HashMap<String, Module>(numberOfModules);
		for(String moduleSuperName:moduleSuperNames){
			modules.put(moduleSuperName, getModules(moduleSuperName));
			activeModules.put(moduleSuperName, getActiveModule(moduleSuperName));
		}
	}
	
	public Map<String,Map<String,Module>> getModuleMap(){
		return modules;
	}
	
	public Map<String,Module> getActiveModuleMap(){
		return activeModules;
	}
	private LinkedList<Class<? extends Module>> pluginLoad(File f) {
		LinkedList<Class<? extends Module>> classes=new LinkedList<Class<? extends Module>>();
		classes.add(DefaultHandlerModule.class);
		classes.add(DefaultAcousticModule.class);
		classes.add(DefaultDictionaryModule.class);
		classes.add(DefaultLanguageModule.class);
		classes.add(DefaultGeneratorModule.class);
		classes.add(DefaultSystemModule.class);
		classes.add(Cancel.class);
		classes.add(DefaultGrammerModule.class);
		if(!f.exists()){
			System.out.println("No such folder");
			return classes;
		}else{
			for(File file:f.listFiles(new FileFilter() {
				
				public boolean accept(File arg0) {
					if(arg0.getName().endsWith(".jar"))
						return true;
					return false;
				}
			})){
				try(URLClassLoader classLoader=new URLClassLoader(new URL[]{file.toURI().toURL()});){
					JarFile jarFile=null;
					try {
						jarFile=new JarFile(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Enumeration<JarEntry> en = jarFile.entries();
					while (en.hasMoreElements()) {
						JarEntry jarEntry = en.nextElement();
						if(!jarEntry.isDirectory() && jarEntry.getName().endsWith(".class")){
							String name=jarEntry.getName().split("[.]")[0];
							
							name=name.replace("/", ".");
							Class<?> tempClass=classLoader.loadClass(name);
							System.out.println(name+ "\t"+ tempClass.getName()+"\t"+Module.class.isAssignableFrom(tempClass));
							if(Module.class.isAssignableFrom(tempClass))
								classes.add((Class<Module>) tempClass);
						}
					}
				} catch (IOException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return classes;
	}
	
	
	private NodeList xmlLoader(InputStream iStreamOfXMLfile) throws ParserConfigurationException, SAXException, IOException{
		DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
		DocumentBuilder builder= factory.newDocumentBuilder();
		
		Document document= builder.parse(iStreamOfXMLfile);
		NodeList nodeList=document.getDocumentElement().getChildNodes();
		return nodeList;
	}

	private NamesFromXMLFile getListOfModuleNames(NodeList xmlNodeList,String moduleSuperName) {
		Node node = null;
		ArrayList<String> nodeNameList=new ArrayList<>();
		String activeModuleName=null;
		if(xmlNodeList != null)
			for(int i=0;i<xmlNodeList.getLength();i++){
				if(xmlNodeList.item(i).getNodeType()== Node.ELEMENT_NODE && moduleSuperName.contains(xmlNodeList.item(i).getAttributes().getNamedItem("moduleType").getNodeValue())){
					node= xmlNodeList.item(i);
					break;
				}
			}
		if(node!=null){
			for(int i=0;i<node.getChildNodes().getLength();i++){
				NodeList n = node.getChildNodes();
				Node item = n.item(i);
				if(item.getNodeType() == Node.ELEMENT_NODE){
					if(activeModuleName == null && item.hasAttributes()){
						if(item.getAttributes().getNamedItem("Active").getTextContent().equalsIgnoreCase("true")){
							activeModuleName=item.getTextContent();
							System.out.println(activeModuleName);
						}
					}
					
					nodeNameList.add(item.getTextContent());
				}
			}
			if(activeModuleName==null && !nodeNameList.isEmpty()){
				activeModuleName=nodeNameList.get(0);
			}
		}
		return new NamesFromXMLFile(nodeNameList, activeModuleName);
	}	
}
