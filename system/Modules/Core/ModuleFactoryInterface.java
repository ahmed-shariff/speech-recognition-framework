package system.Modules.Core;

import java.util.Map;
/**
 * This interface is used to build the {@link Module}'s a given object needs. The implementation of this interface must be able to provide the necessary {@code Module}'s
 * requested. Also it is the responsibility of the class that implements this interface to ensure that the {@link Map}'s  are consistent with each other, i.e. the modules 
 * in the {@code Map} returned by the  method {@linkplain ModuleFactoryInterface#getActiveModuleMap()} are also there in the
 * the {@code Map} returned by the method {@linkplain ModuleFactoryInterface#getModuleMap()}. The implementation of this interface may or may not be able to 
 * provide {@code Module}'s dynamically. Also they may or may not ensure that each time this returns a {@code Module} it returns a new object or a give {@code Module}
 * is built only once and the same instance of the module is reused, it is preferable to reuse, and let the {@code Module}s ensure that they are instantiated once.
 * The types of {@code Module}'s needed can be provided via the constructor or a setter method.
 * @author Ahmed Shariff
 *
 */
public interface ModuleFactoryInterface {

	/**
	 * This method must return a {@link Map} containing the {@linkplain Module}'s of all types requested. The {@code Map}'s {@code Key} must contain the names of the types of
	 * {@code Module} requested. To these {@code Key}'s should be mapped, a {@code Map} containing the {@code Module} of the type with name  as in the {@code Key} it will be mapped to.
	 * This {@code Map} in which the {@code Module}'s will be stored as the {@code Value}'s, the {@code Module}'s must be mapped to the {@code Key} which is as the name
	 * of the module.
	 * @return A {@code Map} as described above
	 * @see {@linkplain ModuleFactoryInterface}, {@linkplain ModuleFactoryInterface#getActiveModuleMap()}
	 */
	public Map<String,Map<String,Module>> getModuleMap();
	
	/**
	 * This method must return a {@link Map} containing the {@link Module}'s marked as active, i.e the {@code Module}'s that object requesting the {@code Module}'s will be using when they
	 * are initiated. The {@code Key}'s of this {@code Map} must be the names of the {@code Module}'s requested, to which the {@code Module} of that type marked as active
	 * will be mapped.
	 * @return A {@code Map} as described above
	 * @see {@linkplain ModuleFactoryInterface}, {@linkplain ModuleFactoryInterface#getModuleMap()}
	 */
	public Map<String,Module> getActiveModuleMap();
}