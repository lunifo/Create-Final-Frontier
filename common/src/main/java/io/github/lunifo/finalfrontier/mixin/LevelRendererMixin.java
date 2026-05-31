package io.github.lunifo.finalfrontier.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
	@Shadow @Final private Minecraft minecraft;

	@Redirect(
			method = "renderSky",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/FogRenderer;levelFogColor()V"
			)
	)
	void setFogColorToBlack() {
		if (minecraft.player.getEyeY() > 2048) {
			RenderSystem.setShaderFogColor(0, 0, 0);
		} else {
			FogRenderer.levelFogColor();
		}
	}

	@Redirect(
			method = "renderSky",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"
			)
	)
	float setFullStarBrightness(ClientLevel instance, float f) {
		if (minecraft.player.getEyeY() > 2048) {
			return 1;
		} else {
			return instance.getStarBrightness(f);
		}
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;setupColor(Lnet/minecraft/client/Camera;FLnet/minecraft/client/multiplayer/ClientLevel;IF)V", shift = At.Shift.AFTER))
	void setClearColorToBlack(PoseStack poseStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f matrix4f, CallbackInfo ci) {
		if (minecraft.player.getEyeY() > 2048) {
			RenderSystem.clearColor(0, 0, 0, 1);
		}
	}
}
