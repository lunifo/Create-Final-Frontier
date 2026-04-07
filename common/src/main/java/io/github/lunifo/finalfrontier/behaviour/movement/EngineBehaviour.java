package io.github.lunifo.finalfrontier.behaviour.movement;

import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import io.github.lunifo.finalfrontier.particle.FinalFrontierParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class EngineBehaviour implements MovementBehaviour {
	@Override
	public void tick(MovementContext context) {
		Level level = context.world;
		BlockPos actorPosition = BlockPos.containing(context.position);
		if (!level.getBlockState(actorPosition.below()).isAir()) {
			level.addAlwaysVisibleParticle(FinalFrontierParticleTypes.LAUNCH_SMOKE_PARTICLE.get(), true, actorPosition.getCenter().x, actorPosition.getCenter().y, actorPosition.getCenter().z, 0, 0.07, 0);
		}
	}
}
