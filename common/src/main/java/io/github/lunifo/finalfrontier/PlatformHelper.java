package io.github.lunifo.finalfrontier;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.EntityBuilder;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Function;
import java.util.function.Supplier;

public class PlatformHelper {
	@ExpectPlatform
	public static <T extends AbstractContraptionEntity> EntityBuilder<T, CreateRegistrate> setContraptionProperties(CreateEntityBuilder<T, CreateRegistrate> builder) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static ParticleRegistrationHelper particleRegistrationHelper() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void executeOnClient(Supplier<Runnable> runnableSupplier) {
		throw new AssertionError();
	}

	public interface ParticleRegistrationHelper {
		Supplier<SimpleParticleType> register(SimpleParticleType particle, ParticleProviderWrapper particleProvider, String name);

		interface ParticleProviderWrapper {
			@Environment(EnvType.CLIENT)
			Function<SpriteSet, ParticleProvider<SimpleParticleType>> get();
		}
	}
}
