package io.github.lunifo.finalfrontier.fabric;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.EntityBuilder;
import com.tterrag.registrate.fabric.EnvExecutor;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.PlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.HashMap;
import java.util.Map;
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

	public static void executeOnClient(Supplier<Runnable> runnableSupplier) {
		EnvExecutor.runWhenOn(EnvType.CLIENT, runnableSupplier);
	}

	public static class ParticleRegistrationHelperFabric implements PlatformHelper.ParticleRegistrationHelper {
		static ParticleRegistrationHelperFabric INSTANCE = new ParticleRegistrationHelperFabric();

		private ParticleRegistrationHelperFabric() {}

		private static final Map<SimpleParticleType, ParticleProviderWrapper> PARTICLE_PROVIDERS = new HashMap<>();

		@Environment(EnvType.CLIENT)
		public static void registerClient() {
			for (var entry : PARTICLE_PROVIDERS.entrySet()) {
				ParticleFactoryRegistry.getInstance().register(entry.getKey(), entry.getValue().get()::apply);
			}
		}

		public Supplier<SimpleParticleType> register(SimpleParticleType particle, ParticleProviderWrapper particleProviderWrapper, String name) {
			PARTICLE_PROVIDERS.put(particle, particleProviderWrapper);
			SimpleParticleType registeredParticle = Registry.register(BuiltInRegistries.PARTICLE_TYPE, FinalFrontier.id(name), particle);
			return () -> registeredParticle;
		}
	}
}
