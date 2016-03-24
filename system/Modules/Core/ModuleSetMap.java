package system.Modules.Core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>Objects of this class act as a data-structure to hold {@link Module}'s any object uses. This class provides a way to store all the {@code Module}'s 
 * as well as keep track of the {@code Modules}'s that are set as active, hence not having to store the reference of the {@code Module} currently active
 * in an object that uses {@code Module}'s.</p>
 * <p>All methods in this class are thread-safe. But non of the methods or constructors of this class checks the {@code Module}'s stored in this data-structure.
 * This only holds the reference to the {@code Module}'s mapped to the {@link String} names of them.</p>
 * 
 * @author Ahmed Shariff
 *
 */

public class ModuleSetMap implements ModuleSet{
	
	private HashMap<String, Map<String,Module>> modules;
	private HashMap<String, Module> activeModules;
	private ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
	
	/**
	 * This constructor takes in two {@link Map}'s as parameters.</br>
	 * <b>Note:</b> This constructor doesn't check if the {@code Module}'s mapped in <b>{@code modules}</b> contains all {@code Module}'s mapped in <b>{@code activeModules}</b>.
	 * Neither does it check if both the {@code Map}'s contain {@code Module}'s of all the types required by the object requesting for an instance of this data-structure.
	 * 
	 * @param modules - This is the {@linkplain Map} containing the {@link Module}'s to be included in this data-structure. The {@code Key} of this {@code Map} should
	 * be the names of the types of {@code Module}'s this instance of the {@code ModuleSetMap} will hold. To these keys will be mapped, another {@code Map} containing the
	 * {@code Module}'s of the type with name as the key to which this {@code Map} will be mapped.
	 * @param activeModules - This is the {@code Map} that contains the {@code Module}'s to be set as active when the object requesting a {@code ModuleSet} is initiated. Each type {@code Module}
	 * must have only one {@code Module} set as active at any given moment. The {@code Key} of this map is the names of the types of {@code Module}'s that will be held
	 * in this data structure. Each type of {@code Module} must have a {@code Module} of that type mapped to it.
	 * 
	 * @see {@linkplain ModuleSetMap}, {@link ModuleSetMap#ModuleSetMap(Map, Map)}
	 */
	public ModuleSetMap(Map<String, Map<String, Module>> modules,Map<String, Module> activeModules){
		this.activeModules=new HashMap<String, Module>(activeModules);
		this.modules=new HashMap<String, Map<String,Module>>(modules);
		/*for(String ma:modules.keySet()){
			System.out.println("\nModule Super Name:"+ma);
			Map<String, Module> x = modules.get(ma);
			System.out.println("number of modules:"+x.size());
			for(String mo:x.keySet()){
				System.out.println(mo+"\t"+x.get(mo).getModuleName());
			}
		}
		System.out.println("Active Modules......");
		for(String ma:activeModules.keySet()){
			System.out.println("Module Super Name:"+ma);
			System.out.println(activeModules.get(ma).getModuleName());
		}*/
	}
	
	/**
	 * This constructor takes as a parameter a factory that implements {@link ModuleFactoryInterface}.</br>
	 * <b>Note:</b> This constructor doesn't check if the {@link Map}'s returned by the method of the factory are consistent with each other, i.e. both have
	 * all the types of {@link Module}'s required by the object requesting for an instance of this data-structure is there, or if the {@code Map} returned by
	 * {@link ModuleFactoryInterface#getModuleMap()} contains all the {@code Module}'s in that are in the {@code Map} returned by the method {@link ModuleFactoryInterface#getActiveModuleMap()}.
	 * @param mFactory- The factory that implements the {@code ModuleFactoryInterface}.
	 * @see {@linkplain ModuleSetMap},  {@link ModuleSetMap#ModuleSetMap(ModuleFactoryInterface)}
	 */
	public ModuleSetMap(ModuleFactoryInterface mFactory){
		this.activeModules=new HashMap<String, Module>(mFactory.getActiveModuleMap());
		this.modules= new HashMap<String, Map<String,Module>>(mFactory.getModuleMap());
	}
	
	
	/**
	 * This method returns a {@link Map} that contains all the {@link Module}'s in this data-structure. The {@code Key} of this {@code Map} is
	 * the names of the types of {@code Module}'s this instance of the {@code ModuleSetMap} holds. To these keys is mapped, another {@code Map} containing the
	 * {@code Module}'s of the type with name as the key to which the {@code Map} is be mapped.
	 * 
	 * @return A {@code Map} containing all the {@code Module}'s.
	 * @see {@linkplain ModuleSetMap#getActiveModule(String)},{@linkplain ModuleSetMap#getActiveModules()}, {@linkplain ModuleSetMap#getModule(String, String)},{@linkplain ModuleSetMap#getModules()}
	 */
	public Map<String, Map<String, Module>> getModules(){
		HashMap<String, Map<String, Module>> m;
		readWriteLock.readLock().lock();
		m=new HashMap<String, Map<String,Module>>(modules);
		readWriteLock.readLock().unlock();
		return m;
	}
	
	/**Returns a {@code Module}
	 * @return {@link Module} with name <b>{@code moduleName}</b> of type with name  <b>{@code moduleSuperName}</b>.
	 * @see {@linkplain ModuleSetMap#getActiveModule(String)},{@linkplain ModuleSetMap#getActiveModules()}, {@linkplain ModuleSetMap#getModules()}
	 */
	public Module getModule(String moduleSuperName, String moduleName){
		Module m;
		readWriteLock.readLock().lock();
		m=modules.get(moduleSuperName).get(moduleName);
		readWriteLock.readLock().unlock();
		return m;
	}
	
	/**
	 * Returns a {@link Map} containing all {@link Module}'s marked as active. Where it's {@code key}'s are the name of the types of {@code Module}'s in this data-structure,
	 * and to them mapped are the {@code Module} of that type which is marked as active.
	 * 
	 * @return A map containing the active {@code Module}'s
	 * @see {@linkplain ModuleSetMap#getActiveModule(String)}, {@linkplain ModuleSetMap#getModule(String, String)},{@linkplain ModuleSetMap#getModules()}
	 */
	public Map<String,Module> getActiveModules(){
		HashMap<String, Module> m;
		readWriteLock.readLock().lock();
		m=new HashMap<String,Module>(activeModules);
		readWriteLock.readLock().unlock();
		return m;
	}
	
	/**
	 * Returns the active {@link Module} of the type with name passed in as a parameter.
	 * @return The module that is active of type with name specified by {@code moduleSuperName}.
	 * 
	 * @see {@linkplain ModuleSetMap#getActiveModules()}, {@linkplain ModuleSetMap#getModule(String, String)},{@linkplain ModuleSetMap#getModules()}	
	 */
	public Module getActiveModule(String moduleSuperName){
		Module m;
		readWriteLock.readLock().lock();
		m=activeModules.get(moduleSuperName);
		readWriteLock.readLock().unlock();
		return m;
	}
	
	/**
	 * Sets the {@link Module} with name specified by <b>{@code moduleName}</b> of type with name specified by <b>{@code moduleSuperName}</b> as the active {@code Module}
	 * of that type. And returns the {@code Module} that was set as active.</br>
	 * <b>Note:</b> Note that only one {@code Module} of a given type will be set as active.
	 * @return {@code Module} that was set as active.
	 * 
	 */
	public Module setActiveModule(String moduleSuperName,String moduleName){
		Module m;
		readWriteLock.writeLock().lock();	
		m=modules.get(moduleSuperName).get(moduleName);
		if(m != null){
			activeModules.remove(moduleSuperName);
			activeModules.put(moduleSuperName, m);
		}else{
			System.out.println("No module by the name " + moduleName +" of type " + moduleSuperName+ "\nLoaded module:" + getActiveModule(moduleSuperName).getModuleName());
			m=getActiveModule(moduleSuperName);
		}
		/*for(String ma:modules.keySet()){
			System.out.println("\nModule Super Name:"+ma);
			Map<String, Module> x = modules.get(ma);
			System.out.println("number of modules:"+x.size());
			for(String mo:x.keySet()){
				System.out.println(mo+"\t"+x.get(mo).getModuleName());
			}
		}
		System.out.println("Active Modules......");
		for(String ma:activeModules.keySet()){
			System.out.println("Module Super Name:"+ma);
			System.out.println(activeModules.get(ma).getModuleName());
		}*/
		readWriteLock.writeLock().unlock();
		return m;
	}
	/*
	public Set<String> getSuperNamesOfModules(){
		Set<String> keySet;
		readWriteLock.readLock().lock();
		keySet=modules.keySet();
		readWriteLock.readLock().unlock();
		return keySet;
	}*/
	
	
	/**
	 * Checks if the {@link Module} with name specified in the parameter <b>{@code moduleName}</b> of type with name specified in the parameter <b>{@code moduleSuperName}</b>
	 * exists in this data-structure.
	 * @return True if the {@code Module} specified above exists.
	 */
	public boolean hasModule(String moduleSuperName,String moduleName){
		readWriteLock.readLock().lock();
		Map<String, Module> m = modules.get(moduleSuperName);
		readWriteLock.readLock().unlock();
		return m.containsKey(moduleName);
	}
	
	
	/**
	 * Returns a clone of this data-structure.
	 * @return a clone of this data-structure.
	 */
	public Object clone(){
		ModuleSet m;
		readWriteLock.readLock().lock();
		m=new ModuleSetMap(modules,activeModules);
		readWriteLock.readLock().unlock();
		return m;
	}
	
}
