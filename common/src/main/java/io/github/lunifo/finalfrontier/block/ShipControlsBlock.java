package io.github.lunifo.finalfrontier.block;

import com.simibubi.create.content.contraptions.AssemblyException;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.block_entity.FinalFrontierBlockEntityTypes;
import io.github.lunifo.finalfrontier.block_entity.ShipControlsBlockEntity;
import io.github.lunifo.finalfrontier.celestial_body.CelestialBody;
import io.github.lunifo.finalfrontier.contraption.RocketPartContraption;
import io.github.lunifo.finalfrontier.contraption.RocketPartContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

public class ShipControlsBlock extends BaseEntityBlock {
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

			try {
				CelestialBody currentCelestialBody = null;
				if (level.getBlockEntity(blockPos) instanceof ShipControlsBlockEntity shipControlsBlockEntity) {
					currentCelestialBody = shipControlsBlockEntity.getCurrentCelestialBody();
				}

				RocketPartContraptionEntity rocketPartEntity = createRocketPart(blockPos, level);
				level.addFreshEntity(rocketPartEntity);
				rocketPartEntity.currentCelestialBody = currentCelestialBody;
			} catch (AssemblyException e) {
				return InteractionResult.PASS;
			}

			return InteractionResult.SUCCESS;
		}
		return super.use(blockState, level, blockPos, player, interactionHand, blockHitResult);
	}

	private RocketPartContraptionEntity createRocketPart(BlockPos anchor, Level level) throws AssemblyException {
		RocketPartContraption rocketPart = new RocketPartContraption();

		rocketPart.assemble(level, anchor);

		rocketPart.removeBlocksFromWorld(level, BlockPos.ZERO);
		rocketPart.startMoving(level);
		rocketPart.expandBoundsAroundAxis(Direction.Axis.Y);

		RocketPartContraptionEntity rocketPartEntity = RocketPartContraptionEntity.create(level, rocketPart);
		rocketPartEntity.setPos(Vec3.atBottomCenterOf(anchor));

		for (var decouplerPair : rocketPart.decouplerPairs) {
			BlockPos nextPartAnchor = decouplerPair.getRight();
			RocketPartContraptionEntity nextPart = createRocketPart(nextPartAnchor, level);
			((RocketPartContraption) nextPart.getContraption()).decouplerAnchor = decouplerPair.getRight().subtract(rocketPart.anchor);
			level.addFreshEntity(nextPart);
			nextPart.startRiding(rocketPartEntity);
			FinalFrontier.LOGGER.info("{}", rocketPartEntity.getPassengers());
		}

		return rocketPartEntity;
	}

	@Override
	@ParametersAreNonnullByDefault
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new ShipControlsBlockEntity(FinalFrontierBlockEntityTypes.SHIP_CONTROLS.get(), blockPos, blockState);
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState blockState) {
		return RenderShape.MODEL;
	}
}
