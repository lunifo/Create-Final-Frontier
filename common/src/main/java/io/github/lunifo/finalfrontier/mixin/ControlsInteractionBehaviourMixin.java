package io.github.lunifo.finalfrontier.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsInteractionBehaviour;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ControlsInteractionBehaviour.class)
public class ControlsInteractionBehaviourMixin {
	@Inject(method = "handlePlayerInteraction", at = @At("HEAD"), cancellable = true)
	private void cancelIfNotTrain(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity, CallbackInfoReturnable<Boolean> cir) {
		if (!(contraptionEntity instanceof CarriageContraptionEntity)) {
			cir.setReturnValue(false);
		}
	}
}
