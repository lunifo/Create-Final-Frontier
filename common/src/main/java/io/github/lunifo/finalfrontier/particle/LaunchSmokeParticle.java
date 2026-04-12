package io.github.lunifo.finalfrontier.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@Environment(EnvType.CLIENT)
public class LaunchSmokeParticle extends TextureSheetParticle {
	private final SpriteSet spriteSet;

	protected LaunchSmokeParticle(ClientLevel clientLevel, double d, double e, double f, double g, double h, double i, SpriteSet spriteSet) {
		super(clientLevel, d, e, f, g, h, i);
		this.spriteSet = spriteSet;
		this.quadSize *= 25;
		this.xd *= 3;
		this.yd *= 0;
		this.zd *= 3;
		this.lifetime = 100;
		this.setSpriteFromAge(spriteSet);
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(spriteSet);
	}

	@Override
	public @NotNull ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Environment(EnvType.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public Provider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		@ParametersAreNonnullByDefault
		@Override
		public @Nullable Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
			return new LaunchSmokeParticle(clientLevel, d, e, f, g, h, i, this.spriteSet);
		}
	}
}
