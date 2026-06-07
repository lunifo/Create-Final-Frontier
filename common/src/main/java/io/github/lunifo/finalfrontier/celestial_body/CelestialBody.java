package io.github.lunifo.finalfrontier.celestial_body;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.lunifo.finalfrontier.registry.SimpleDynamicRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public record CelestialBody(ResourceKey<Level> dimensionKey, boolean hasAtmosphere) {
	public static final Codec<CelestialBody> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(CelestialBody::dimensionKey),
					Codec.BOOL.fieldOf("has_atmosphere").forGetter(CelestialBody::hasAtmosphere)
			).apply(instance, CelestialBody::new)
	);

	public static final SimpleDynamicRegistry<CelestialBody> CELESTIAL_BODIES = new SimpleDynamicRegistry<>("celestial_body", CODEC);
	public static final Map<ResourceKey<Level>, CelestialBody> DIMENSION_LOOKUP = new HashMap<>();

	public static void init() {
		CELESTIAL_BODIES.addListener(registry -> {
			DIMENSION_LOOKUP.clear();
			registry.entries().forEach((location, body) -> DIMENSION_LOOKUP.put(body.dimensionKey, body));
		});
	}
}
