package io.github.lunifo.finalfrontier.behaviour.interaction;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import io.github.lunifo.finalfrontier.contraption.RocketPartContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class ShipControlsBehaviour extends MovingInteractionBehaviour {
	@Override
	public boolean handlePlayerInteraction(Player player, InteractionHand activeHand, BlockPos localPos, AbstractContraptionEntity contraptionEntity) {
		if (contraptionEntity instanceof RocketPartContraptionEntity rocketPartEntity) {
			if (rocketPartEntity.isInFlight()) {
				rocketPartEntity.stage();
			} else {
				rocketPartEntity.startFlight();
			}
			return true;
		}
		return false;
	}
}
