package io.github.lunifo.finalfrontier.fabric;

import net.fabricmc.api.ClientModInitializer;

public class FinalFrontierClientFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		PlatformHelperImpl.ParticleRegistrationHelperFabric.registerClient();
	}
}
