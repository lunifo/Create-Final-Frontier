package io.github.lunifo.finalfrontier.dimension;

import io.github.lunifo.finalfrontier.FinalFrontier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class FinalFrontierDimensions {
	public static final ResourceKey<Level> DEEP_SPACE = create("deep_space");
	public static final ResourceKey<Level> MOON = create("moon");

	private static ResourceKey<Level> create(String name) {
		return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(FinalFrontier.MOD_ID, name));
	}
}
