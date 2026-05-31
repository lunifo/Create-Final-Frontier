package io.github.lunifo.finalfrontier.fabric;

import io.github.lunifo.finalfrontier.FinalFrontier;
import net.fabricmc.api.ClientModInitializer;

public class FinalFrontierClientFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		PlatformHelperImpl.ParticleRegistrationHelperFabric.registerClient();
		FinalFrontier.clientInit();
	}
}
