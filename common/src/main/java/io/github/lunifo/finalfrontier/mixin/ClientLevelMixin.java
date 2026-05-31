package io.github.lunifo.finalfrontier.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
	@Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
	void setSkyColorToBlack(Vec3 vec3, float f, CallbackInfoReturnable<Vec3> cir) {
		if (Minecraft.getInstance().player.getEyeY() > 2048) {
			cir.setReturnValue(Vec3.ZERO);
			cir.cancel();
		}
	}
}
