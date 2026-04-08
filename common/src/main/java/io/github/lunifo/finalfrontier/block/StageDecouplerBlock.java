package io.github.lunifo.finalfrontier.block;

import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StageDecouplerBlock extends WrenchableDirectionalBlock {
	protected StageDecouplerBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
	}

	@Override
	public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext blockPlaceContext) {
		BlockState state = Objects.requireNonNull(super.getStateForPlacement(blockPlaceContext), "We're most likely dealing with some incautious modder.");
		BlockPos placementPos = blockPlaceContext.getClickedPos();
		Level level = blockPlaceContext.getLevel();

		Direction alignableDirection = null;
		for (Direction direction : Direction.values()) {
			BlockState neighbourState = level.getBlockState(placementPos.relative(direction));
			if (neighbourState.is(this) && neighbourState.getValue(FACING).getOpposite() == direction) {
				if (alignableDirection == null) {
					alignableDirection = direction;
				} else {
					return state;
				}
			}
		}

		return alignableDirection == null ? state : state.setValue(FACING, alignableDirection);
	}
}
