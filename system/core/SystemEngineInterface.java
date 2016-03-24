package system.core;

import java.util.concurrent.BlockingQueue;


interface SystemEngineInterface extends Runnable {
	
	/**
	 * /**
	 * The dependencies of this {@code Engine} must be injected via this method.
	 * @param responseEngine - Instance of {@linkplain ResponseEngineInreface} to be used by this {@code Engine}.
	 * @param blockingQueue - The {@codeQueue} that will be used for the this {@code Engine} to communicate with the {@code ResponseEngineInreface} instance passed by this method.
	 * @param moduleSelector - The {@linkplain SystemModuleHandlerInterface} instance to be used by this engine.
	 * @throws IllegalAccessException
	 */
	public void injectRunningParameters(ResponseEngineInreface responseEngine,
			BlockingQueue<Response> blockingQueue,
			SystemModuleSelectorInterface moduleSelector) throws IllegalAccessException;
	
	/**
	 * When a {@linkplain system.Modules.Core.SubModules.SystemModule} pauses the {@code SystemEngine}, to ensure the rest of the system is paused
	 * before initiating building procedure, this method must be invoked for the {@code SystemEngine} to continue processing. When this method is invoked, 
	 * a {@linkplain system.Modules.Core.RecognizerModule}'s resource will be changed, hence this method must be invoked only when it is confirmed that the resource to be built
	 * is not being used, that is, the system is paused.
	 */
	public void continueToBuild();
}
