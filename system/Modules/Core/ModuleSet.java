package system.Modules.Core;

import java.util.Map;

import system.core.Modules;
/**
 * This interface is of a data-structure that holds the {@link Module}'s used by a given object. Classes implementing this interface should ensure not only to store all 
 * {@code Module}'s an object uses, but also keep track of the {@code Module}'s that are set as active, eliminating the need to store a local reference of the
 * {@code Module} active.
 * @author Ahmed Shariff
 *
 */
public interface ModuleSet extends Cloneable {

	/**This method must return all {@linkplain Module}'s in this {@code ModuleSet}
	 * @see {@linkplain ModuleSet#getModule(Modules, String)},{@linkplain ModuleSet#getActiveModule(Modules)}, {@linkplain ModuleSet#getActiveModules()}
	 * @return Map<{@linkplain Modules},Map<{@linkplain String},{@linkplain Modules}>>
	 * </br>The @{code key} of the map should contain the {@linkplain String} names of types of the {@linkplain Module}'s</br>
	 *  The @{code Value} of the map contains a map containing the {@code Module}'s that implements the {@code Module} interface with the name in the {@code key}
	 *  this map is mapped to. The Key values of this map is the {@linkplain String} name of the {@code Module}
	 */
	public Map<String, Map<String, Module>> getModules();
	
	/**
	 * This method must return the {@linkplain Module} with name <b>{@code moduleName}</b> of type with name <b>{@code moduleSuperName}</b>
	 * @param moduleSuperName - The name of the type of {@code Module}
	 * @param moduleName - The name of the expected {@link Module}
	 * @return The {@link Module} described above.
	 * @see {@linkplain ModuleSet#getModules()},{@linkplain ModuleSet#getActiveModule(Modules)}, {@linkplain ModuleSet#getActiveModules()}
	 */
	public Module getModule(String moduleSuperName, String moduleName);
	
	/**
	 * This method must return the {@link Module}'s that are set as active in this {@code ModuleSet}.</br>
	 * <b>Note:</b> only one {@code Module} of any given type should be active at a time.
	 * @return A map containing the {@link Module}'s marked as active, mapped to the {@link String} name of the type of {@link Module}
	 * @see {@linkplain ModuleSet#getModule(Modules, String)},{@linkplain ModuleSet#getActiveModule(Modules)}, {@linkplain ModuleSet#getModules()}
	 */
	public Map<String,Module> getActiveModules();
	
	/**
	 * This method must return the {@link Module} of type with the name <b>{@code moduleName}</b> that is marked as active in this {@link ModuleSet}</br>
	 * <b>Note:</b> only one {@code Module} of any given type should be active at a time.
	 * @param moduleName - name of the type of {@code Module} that is set as active
	 * @return
	 */
	public Module getActiveModule(String moduleSuperName);
	
	/**
	 * Sets the active {@link Module} of a given type of {@code Module}, and returns the {@link Module} which was set as active. It must be ensured that the 
	 * null is not returned. if requested {@code Module} is not available, an appropriate alternate module must be loaded, while informing the user.
	 * @param moduleSuperName - The type name of the {@link Module} to be set as active.
	 * @param moduleName - The name of the {@link Module} to be set as active
	 * @return The {@link Module} that was set as active.
	 */
	public Module setActiveModule(String moduleSuperName,String moduleName);
	
	/**
	 * Checks if a {@link Module} with name <b>{@code moduleName}</b> of type with name <b>{@code moduleSuperName}</b> 
	 * @param moduleSuperName - The {@link String} name of the type of {@link Module}
	 * @param moduleName - The {@code String} name of the 
	 * @return {@code True} if the {@code Module} exists in a given {@code Module} type
	 */
	public boolean hasModule(String moduleSuperName,String moduleName);
	
	/**
	 * This method must return a clone of the {@link ModuleSet}, so that any changes made to it will not alter the properties of {@code ModuleSet} in the system.
	 * @return
	 * A copy of this {@code ModuleSet}
	 */
	public Object clone();
}
