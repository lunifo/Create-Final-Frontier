package io.github.lunifo.finalfrontier.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.lunifo.finalfrontier.celestial_body.CelestialBody;
import io.github.lunifo.finalfrontier.client.render.CelestialBodyRenderer;
import io.github.lunifo.finalfrontier.worldgen.dimension.FinalFrontierDimensions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow
	private @Nullable ClientLevel level;
	@Shadow
	private @Nullable VertexBuffer starBuffer;

	@WrapMethod(method = "renderClouds")
	private void noCloudsWithoutAtmosphere(PoseStack poseStack, Matrix4f matrix4f, float f, double d, double e, double g, Operation<Void> original) {
		if (level != null) {
			if (level.dimension() == FinalFrontierDimensions.DEEP_SPACE) {
				return;
			}

			CelestialBody celestialBody = CelestialBody.DIMENSION_LOOKUP.get(level.dimension());
			if (celestialBody != null) {
				if (!celestialBody.hasAtmosphere()) {
					return;
				}
			}
		}

		original.call(poseStack, matrix4f, f, d, e, g);
	}

	@Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0), cancellable = true)
	private void dontRenderMoonAndSun(PoseStack poseStack, Matrix4f matrix4f, float f, Camera camera, boolean bl, Runnable runnable, CallbackInfo ci, @SuppressWarnings("LocalMayUseName") @Local BufferBuilder bufferBuilder) {
		poseStack.popPose();

		// Cloned from renderSky with null checks
		ShaderInstance shader = GameRenderer.getPositionShader();
		if (level != null && starBuffer != null && shader != null) {
			float b = level.getStarBrightness(f);
			if (b > 0.0F) {
				RenderSystem.setShaderColor(b, b, b, b);
				FogRenderer.setupNoFog();
				starBuffer.bind();
				starBuffer.drawWithShader(poseStack.last().pose(), matrix4f, shader);
				VertexBuffer.unbind();
				runnable.run();
			}
		}

		CelestialBodyRenderer.renderCelestialBodies(poseStack, bufferBuilder);

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.depthMask(true);

		ci.cancel();
	}
}


