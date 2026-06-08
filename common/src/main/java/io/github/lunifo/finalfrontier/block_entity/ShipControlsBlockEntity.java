package io.github.lunifo.finalfrontier.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ShipControlsBlockEntity extends BlockEntity {
	public ShipControlsBlockEntity(BlockEntityType<ShipControlsBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}
}
