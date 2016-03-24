package system.core;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;

import system.Modules.Core.Module;
import system.Modules.Core.ModuleSet;
import system.Modules.Core.SubModules.SystemModuleWithPrintOut;

abstract class SystemModuleSelectorInterface {
	protected final ModuleSet moduleSet;
	private final String compulsoryModule=Modules.SYSTEM_ENGINE_MOULE.value();

	public abstract Module selectModule(Response response);
	
	public SystemModuleSelectorInterface(ModuleSet moduleSet, PrintWriter outForModules){
		this.moduleSet=moduleSet;
		if(!checkModulesInModuleSet(compulsoryModule)){
			throw new IllegalAccessError("A Module of type "+compulsoryModule+ " does not exist in the provided ModuleSet");
		}
		System.out.println("SYSTEM_MODULE: Assigning outputs");
		Collection<Module> modules = moduleSet.getModules().get(compulsoryModule).values();
		
		for(Module m:modules){
			if(m instanceof SystemModuleWithPrintOut){
				((SystemModuleWithPrintOut)m).setOut(outForModules);
				System.out.println("SYSTEM_MODULE: out assigned for: "+ m.getModuleName());
			}
		}
	}
	
	public ModuleSet getModuleSet(){
		return (ModuleSet) moduleSet.clone();
	}
	
	public boolean checkModulesInModuleSet(String compulsaryModules){
		Map<String, Map<String, Module>> m = moduleSet.getModules();
		System.out.println(m.keySet().size());
		return m.containsKey(compulsaryModules) && !m.get(compulsaryModules).isEmpty();
	}
}
