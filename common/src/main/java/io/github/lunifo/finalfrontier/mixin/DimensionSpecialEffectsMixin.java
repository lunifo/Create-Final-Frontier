package io.github.lunifo.finalfrontier.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionSpecialEffects.class)
public class DimensionSpecialEffectsMixin {
	@Inject(method = "getSunriseColor", at = @At(value = "HEAD"), cancellable = true)
	void removeSunriseColor(float f, float g, CallbackInfoReturnable<float[]> cir) {
		if (Minecraft.getInstance().player.getEyeY() > 2048) {
			cir.setReturnValue(null);
			cir.cancel();
		}
	}
}
