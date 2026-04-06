package io.github.lunifo.finalfrontier;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.EntityBuilder;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.SimpleParticleType;

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

	public interface ParticleRegistrationHelper {
		Supplier<SimpleParticleType> register(SimpleParticleType particle, Supplier<ParticleEngine.SpriteParticleRegistration<SimpleParticleType>> particleProvider, String name);
	}
}
