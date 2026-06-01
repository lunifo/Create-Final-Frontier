package io.github.lunifo.finalfrontier.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.lunifo.finalfrontier.util.PlayerUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
	@Shadow private static float fogRed;
	@Shadow private static float fogBlue;
	@Shadow private static float fogGreen;

	@Inject(method = "levelFogColor", at = @At("HEAD"), cancellable = true)
	private static void setFogColorToBlack(CallbackInfo ci) {
		float transitionFactor = (float) PlayerUtil.getSpaceTransitionFactor(Minecraft.getInstance().player);
		if (transitionFactor > 0) {
			RenderSystem.setShaderFogColor(
					Mth.lerp(transitionFactor, fogRed, 0),
					Mth.lerp(transitionFactor, fogGreen, 0),
					Mth.lerp(transitionFactor, fogBlue, 0)
			);
			ci.cancel();
		}
	}

	@Inject(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V", ordinal = 1), cancellable = true)
	private static void setClearColorToBlack(Camera camera, float f, ClientLevel clientLevel, int i, float g, CallbackInfo ci) {
		float transitionFactor = (float) PlayerUtil.getSpaceTransitionFactor(Minecraft.getInstance().player);
		if (transitionFactor > 0) {
			RenderSystem.clearColor(
					Mth.lerp(transitionFactor, fogRed, 0),
					Mth.lerp(transitionFactor, fogGreen, 0),
					Mth.lerp(transitionFactor, fogBlue, 0),
					0
			);
			ci.cancel();
		}
	}
}
