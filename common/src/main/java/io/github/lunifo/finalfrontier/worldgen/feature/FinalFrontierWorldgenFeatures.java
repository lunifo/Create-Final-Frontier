package io.github.lunifo.finalfrontier.worldgen.feature;

import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import io.github.lunifo.finalfrontier.FinalFrontier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;

public class FinalFrontierWorldgenFeatures {
	public static final RegistryEntry<CraterFeature> CRATER = feature("crater", CraterFeature::new);

	public static <F extends Feature<?>> RegistryEntry<F> feature(String name, NonNullSupplier<F> featureFactory) {
		return FinalFrontier.REGISTRATE.simple(name, Registries.FEATURE, featureFactory);
	}

	public static void init() {

	}
}
