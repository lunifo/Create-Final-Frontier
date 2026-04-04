package io.github.lunifo.finalfrontier.block;

import com.simibubi.create.content.contraptions.AssemblyException;
import io.github.lunifo.finalfrontier.contraption.RocketPartContraption;
import io.github.lunifo.finalfrontier.contraption.RocketPartContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class ShipControlsBlock extends Block {
	public ShipControlsBlock(Properties properties) {
		super(properties);
	}

	@ParametersAreNonnullByDefault
	@Override
	public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
		if (player.getItemInHand(interactionHand).isEmpty()) {
			if (level.isClientSide()) {
				return InteractionResult.SUCCESS;
			}

			RocketPartContraption rocketPart = new RocketPartContraption();
			try {
				rocketPart.assemble(level, blockPos);
			} catch (AssemblyException e) {
				return InteractionResult.PASS;
			}

			rocketPart.removeBlocksFromWorld(level, BlockPos.ZERO);
			rocketPart.startMoving(level);
			rocketPart.expandBoundsAroundAxis(Direction.Axis.Y);

			RocketPartContraptionEntity rocketPartEntity = RocketPartContraptionEntity.create(level, rocketPart);
			rocketPartEntity.setPos(Vec3.atBottomCenterOf(blockPos));
			level.addFreshEntity(rocketPartEntity);
			return InteractionResult.SUCCESS;
		}
		return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
	}
}
