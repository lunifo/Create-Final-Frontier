package io.github.lunifo.finalfrontier.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import io.github.lunifo.finalfrontier.FinalFrontier;

public class FinalFrontierBlocks {
	public static BlockEntry<ShipControlsBlock> SHIP_CONTROLS = FinalFrontier.REGISTRATE
			.block("ship_controls", ShipControlsBlock::new)
			.simpleItem()
			.register();

	public static void init() {

	}
}
