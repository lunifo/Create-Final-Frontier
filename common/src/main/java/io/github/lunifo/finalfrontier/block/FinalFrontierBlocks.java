package io.github.lunifo.finalfrontier.block;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.util.entry.BlockEntry;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import io.github.fabricators_of_create.porting_lib.models.generators.block.BlockStateProvider;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.behaviour.interaction.ShipControlsBehaviour;
import io.github.lunifo.finalfrontier.behaviour.movement.EngineBehaviour;
import net.minecraft.world.level.block.Block;

public class FinalFrontierBlocks {
	public static final BlockEntry<ShipControlsBlock> SHIP_CONTROLS = FinalFrontier.REGISTRATE
			.block("ship_controls", ShipControlsBlock::new)
			.simpleItem()
			.blockstate((ctx, provider) -> provider.simpleBlock(ctx.get(), cubeBottomTop(ctx, provider)))
			.onRegister(MovingInteractionBehaviour.interactionBehaviour(new ShipControlsBehaviour()))
			.register();

	public static final BlockEntry<Block> ENGINE = FinalFrontier.REGISTRATE
			.block("engine", Block::new)
			.simpleItem()
			.blockstate((ctx, provider) -> provider.simpleBlock(ctx.get(), cubeBottomTop(ctx, provider)))
			.onRegister(MovementBehaviour.movementBehaviour(new EngineBehaviour()))
			.register();

	private static ModelFile cubeBottomTop(DataGenContext<?, ?> ctx, BlockStateProvider provider) {
		String name = ctx.getName();
		return provider.models().cubeBottomTop(
				name,
				FinalFrontier.id("block/" + name + "_side"),
				FinalFrontier.id("block/" + name + "_bottom"),
				FinalFrontier.id("block/" + name + "_top")
		);
	}

	public static void init() {

	}
}
