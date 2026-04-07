package io.github.lunifo.finalfrontier.block;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.behaviour.movement.MovementBehaviour;
import com.tterrag.registrate.util.entry.BlockEntry;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.behaviour.interaction.ShipControlsBehaviour;
import io.github.lunifo.finalfrontier.behaviour.movement.EngineBehaviour;
import net.minecraft.world.level.block.Block;

public class FinalFrontierBlocks {
	public static final BlockEntry<ShipControlsBlock> SHIP_CONTROLS = FinalFrontier.REGISTRATE
			.block("ship_controls", ShipControlsBlock::new)
			.simpleItem()
			.onRegister(MovingInteractionBehaviour.interactionBehaviour(new ShipControlsBehaviour()))
			.register();

	public static final BlockEntry<Block> ENGINE = FinalFrontier.REGISTRATE
			.block("engine", Block::new)
			.simpleItem()
			.onRegister(MovementBehaviour.movementBehaviour(new EngineBehaviour()))
			.register();

	public static void init() {

	}
}
