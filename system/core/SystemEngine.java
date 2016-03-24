package system.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;

import system.Modules.Core.Module;
import system.Modules.Core.ModuleSet;
import system.Modules.Core.SubModules.BuilderSystemModule;
import system.Modules.Core.SubModules.SystemModule;

/**
 * 
 * @author Ahmed Shariff
 * 
 * {@docRoot}
 *
 */
class SystemEngine implements SystemEngineInterface {

	private static SystemEngine engineInstance=null;
	
	private final Phaser barrier=new Phaser(2);
	
	private ResponseEngineInreface responseEngine;
	private Response responseFromInQueue;
	private SystemModuleSelectorInterface moduleSelector;
	private BlockingQueue<Response> responseInQueue;
	private boolean runningParametersSet=false;
	
	private SystemEngine(){}
	/**
	 * {@inheritDoc}
	 * <p>This method can be invoked only once. Attempting to re-invoke the method will throw an {@code IllegalAccessException}.</p>
	 * The {@linkplain ModuleSet} passed to the {@code SystemModuleHandlerInterface} instance must contain {@linkplain Module}'s of types:
	 * <ul>
	 * <li>{@linkplain SystemModule}</li>
	 * </ul>
	 * If the {@code ModuleSet} does not contain at least one {@code Mdoule} of each above types, a {@code IllegalArgumentException} will be thrown.</br> 
	 */
	public void injectRunningParameters(ResponseEngineInreface responseEngine,
										BlockingQueue<Response> blockingQueue,
										SystemModuleSelectorInterface moduleSelector) throws IllegalAccessException{
		if(!runningParametersSet){
			this.responseEngine=responseEngine;
			this.responseInQueue=blockingQueue;
			this.moduleSelector=moduleSelector;
			runningParametersSet=true;
		}else{
			throw new IllegalAccessException("Running parameters have been already injected.");
		}
	}
	
	
	public static synchronized SystemEngine getSystemEngine(){
		if(engineInstance == null)
			engineInstance=new SystemEngine();
		return engineInstance;
	}
	
	/**
	 * {@inheritDoc}
	 * When this method is invoked the {@codeEngine}'s thread will be notified to continue functions.
	 */
	public void continueToBuild(){
		barrier.arrive();
	}
	
	public void run() {
		boolean run=true;
		if(!runningParametersSet){
			System.err.println("Running parameters not set, use method: injectRunningParameters");
			run=false;
		}
		
		while(run){
			try {
				System.out.println("SYSTEM_ENGINE: getting new response");
				responseFromInQueue=responseInQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			executeSystemProcess((SystemModule)moduleSelector.selectModule(responseFromInQueue),responseFromInQueue);
		}
	}

	private void executeSystemProcess(SystemModule selectedModule,
			Response response) {
		System.out.println("SYSTEM_ENGINE: executing system function:\n\tRunning module: "+selectedModule.getModuleName());
		selectedModule.executeSystemModule(response);
		if(selectedModule instanceof BuilderSystemModule){
			System.out.println("SYSTEM_ENGINE: buildable Module");
			if(((BuilderSystemModule)selectedModule).checkBuildCommand()){
				responseEngine.setHaltResponseProcessForBuild(true);
				barrier.arriveAndAwaitAdvance();
				((BuilderSystemModule)selectedModule).continueBuild();
				responseEngine.setHaltResponseProcessForBuild(false);
			}
		}	
	}
}
