package io.github.lunifo.finalfrontier.fabric;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.EntityBuilder;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.PlatformHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;

public class PlatformHelperImpl {
	public static <T extends AbstractContraptionEntity> EntityBuilder<T, CreateRegistrate> setContraptionProperties(CreateEntityBuilder<T, CreateRegistrate> builder) {
		return builder
				.visual(() -> ContraptionVisual::new)
				.properties(properties -> properties.trackRangeChunks(15).trackedUpdateRate(3).forceTrackedVelocityUpdates(true).fireImmune())
				.properties(AbstractContraptionEntity::build);
	}

	public static PlatformHelper.ParticleRegistrationHelper particleRegistrationHelper() {
		return ParticleRegistrationHelperFabric.INSTANCE;
	}

	private static class ParticleRegistrationHelperFabric implements PlatformHelper.ParticleRegistrationHelper {
		static ParticleRegistrationHelperFabric INSTANCE = new ParticleRegistrationHelperFabric();

		private ParticleRegistrationHelperFabric() {}

		public Supplier<SimpleParticleType> register(SimpleParticleType particle, Supplier<ParticleEngine.SpriteParticleRegistration<SimpleParticleType>> particleProvider, String name) {
			ParticleFactoryRegistry.getInstance().register(particle, spriteSet -> particleProvider.get().create(spriteSet));
			SimpleParticleType registeredParticle = Registry.register(BuiltInRegistries.PARTICLE_TYPE, FinalFrontier.id(name), particle);
			return () -> registeredParticle;
		}
	}
}
