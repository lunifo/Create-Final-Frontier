package io.github.lunifo.finalfrontier.particle;

import io.github.lunifo.finalfrontier.PlatformHelper;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.function.Supplier;

public class FinalFrontierParticleTypes {
	private static final PlatformHelper.ParticleRegistrationHelper PARTICLE_REGISTRY = PlatformHelper.particleRegistrationHelper();
	public static final Supplier<SimpleParticleType> LAUNCH_SMOKE_PARTICLE = PARTICLE_REGISTRY.register(new SimpleParticleType(false) {}, () -> LaunchSmokeParticle.Provider::new, "launch_smoke");

	public static void init() {

	}
}
