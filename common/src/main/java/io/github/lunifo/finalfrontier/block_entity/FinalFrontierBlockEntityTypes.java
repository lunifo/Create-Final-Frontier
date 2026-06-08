package io.github.lunifo.finalfrontier.block_entity;

import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.block.FinalFrontierBlocks;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FinalFrontierBlockEntityTypes {
	public static BlockEntityEntry<ShipControlsBlockEntity> SHIP_CONTROLS = register("ship_controls", ShipControlsBlockEntity::new);

	public static <T extends BlockEntity> BlockEntityEntry<T> register(String name, BlockEntityBuilder.BlockEntityFactory<T> factory) {
		return FinalFrontier.REGISTRATE.blockEntity(name, factory)
				.validBlock(FinalFrontierBlocks.SHIP_CONTROLS)
				.register();
	}

	public static void init() {

	}
}
