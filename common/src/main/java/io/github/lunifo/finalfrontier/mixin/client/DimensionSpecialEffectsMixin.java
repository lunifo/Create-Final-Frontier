package io.github.lunifo.finalfrontier.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.lunifo.finalfrontier.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DimensionSpecialEffects.class)
public abstract class DimensionSpecialEffectsMixin {
	@ModifyReturnValue(method = "getSunriseColor", at = @At(value = "RETURN"))
	float[] setSunriseColorToBlack(float[] original) {
		double transitionFactor = PlayerUtil.getSpaceTransitionFactor(Minecraft.getInstance().player);
		if (original == null) {
			return null;
		}

		if (transitionFactor > 0) {
			for (int i = 0; i < original.length; i++) {
				original[i] = Mth.lerp((float) transitionFactor, original[i], 0);
			}
		}
		return original;
	}
}
