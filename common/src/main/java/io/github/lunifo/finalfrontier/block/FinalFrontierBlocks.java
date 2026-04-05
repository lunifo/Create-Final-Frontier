package io.github.lunifo.finalfrontier.block;

import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.tterrag.registrate.util.entry.BlockEntry;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.behaviour.interaction.ShipControlsBehaviour;

public class FinalFrontierBlocks {
	public static BlockEntry<ShipControlsBlock> SHIP_CONTROLS = FinalFrontier.REGISTRATE
			.block("ship_controls", ShipControlsBlock::new)
			.simpleItem()
			.onRegister(MovingInteractionBehaviour.interactionBehaviour(new ShipControlsBehaviour()))
			.register();

	public static void init() {

	}
}
