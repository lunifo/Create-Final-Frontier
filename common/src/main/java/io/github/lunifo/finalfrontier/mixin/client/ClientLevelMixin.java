package io.github.lunifo.finalfrontier.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.lunifo.finalfrontier.util.PlayerUtil;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin {
	@Shadow @Final private Minecraft minecraft;

	@ModifyReturnValue(method = "getSkyColor", at = @At("RETURN"))
	Vec3 setSkyColorToBlack(Vec3 original) {
		double transitionFactor = PlayerUtil.getSpaceTransitionFactor(minecraft.player);
		if (transitionFactor > 0) {
			return VecHelper.lerp((float) transitionFactor, original, Vec3.ZERO);
		} else {
			return original;
		}
	}

	@ModifyReturnValue(method = "getStarBrightness", at = @At("RETURN"))
	float setFullStarBrightness(float original) {
		double transitionFactor = PlayerUtil.getSpaceTransitionFactor(minecraft.player);
		if (transitionFactor > 0) {
			return Mth.lerp((float) transitionFactor, original, 1);
		} else {
			return original;
		}
	}
}
