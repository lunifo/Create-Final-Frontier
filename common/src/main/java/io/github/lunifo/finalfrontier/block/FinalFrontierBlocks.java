package io.github.lunifo.finalfrontier.block;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import io.github.lunifo.finalfrontier.BlockStateGenerators;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.behaviour.interaction.ShipControlsBehaviour;
import io.github.lunifo.finalfrontier.behaviour.movement.EngineBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GravelBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class FinalFrontierBlocks {
	public static final BlockEntry<ShipControlsBlock> SHIP_CONTROLS = FinalFrontier.REGISTRATE
			.block("ship_controls", ShipControlsBlock::new)
			.simpleItem()
			.blockstate(BlockStateGenerators::genericCubeBottomTop)
			.onRegister(MovingInteractionBehaviour.interactionBehaviour(new ShipControlsBehaviour()))
			.register();

	public static final BlockEntry<Block> ENGINE = FinalFrontier.REGISTRATE
			.block("engine", Block::new)
			.simpleItem()
			.blockstate(BlockStateGenerators::genericCubeBottomTop)
			.onRegister(MovementBehaviour.movementBehaviour(new EngineBehaviour()))
			.register();

	public static final BlockEntry<StageDecouplerBlock> STAGE_DECOUPLER = FinalFrontier.REGISTRATE
			.block("stage_decoupler", StageDecouplerBlock::new)
			.simpleItem()
			.blockstate(BlockStateGenerators::stageDecoupler)
			.register();

	public static final BlockEntry<Block> MOON_STONE = block("moon_stone").register();
	public static final BlockEntry<GravelBlock> MOON_REGOLITH = block("moon_regolith", GravelBlock::new).register();

	private static BlockBuilder<Block, ?> block(String name) {
		return block(name, Block::new);
	}

	private static <B extends Block> BlockBuilder<B, ?> block(String name, NonNullFunction<BlockBehaviour.Properties, B> blockFactory) {
		return FinalFrontier.REGISTRATE
				.block(name, blockFactory)
				.simpleItem();
	}

	public static void init() {

	}
}
