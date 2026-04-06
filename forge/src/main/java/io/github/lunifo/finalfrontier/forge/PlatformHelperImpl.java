package io.github.lunifo.finalfrontier.forge;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.EntityBuilder;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.PlatformHelper;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PlatformHelperImpl {
	public static <T extends AbstractContraptionEntity> EntityBuilder<T, CreateRegistrate> setContraptionProperties(CreateEntityBuilder<T, CreateRegistrate> builder) {
		return builder
				.visual(() -> ContraptionVisual::new)
				.properties(properties -> properties.setTrackingRange(15).setUpdateInterval(3).setShouldReceiveVelocityUpdates(true).fireImmune())
				.properties(AbstractContraptionEntity::build);
	}

	public static PlatformHelper.ParticleRegistrationHelper particleRegistrationHelper() {
		return ParticleRegistrationHelperForge.INSTANCE;
	}

	public static class ParticleRegistrationHelperForge implements PlatformHelper.ParticleRegistrationHelper {
		static ParticleRegistrationHelperForge INSTANCE = new ParticleRegistrationHelperForge();

		private ParticleRegistrationHelperForge() {}

		private static final DeferredRegister<ParticleType<?>> PARTICLE_REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, FinalFrontier.MOD_ID);
		private static final Map<SimpleParticleType, Supplier<ParticleEngine.SpriteParticleRegistration<SimpleParticleType>>> PARTICLE_PROVIDERS = new HashMap<>();
		public static void register(IEventBus eventBus) {
			PARTICLE_REGISTER.register(eventBus);
		}

		@OnlyIn(Dist.CLIENT)
		public static void registerClient(RegisterParticleProvidersEvent event) {
			for (var entry : PARTICLE_PROVIDERS.entrySet()) {
				event.registerSpriteSet(entry.getKey(), entry.getValue().get());
			}
		}

		public Supplier<SimpleParticleType> register(SimpleParticleType particle, Supplier<ParticleEngine.SpriteParticleRegistration<SimpleParticleType>> particleProvider, String name) {
			PARTICLE_PROVIDERS.put(particle, particleProvider);
			return PARTICLE_REGISTER.register(name, () -> particle);
		}
	}
}
