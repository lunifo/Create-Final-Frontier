package io.github.lunifo.finalfrontier.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class ExampleExpectPlatformImpl {
	public static String platformName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "Quilt" : "Fabric";
	}
}
