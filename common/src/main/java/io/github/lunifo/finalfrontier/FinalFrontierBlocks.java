package io.github.lunifo.finalfrontier;

import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Block;

public class FinalFrontierBlocks {
	public static final BlockEntry<Block> EXAMPLE_BLOCK = FinalFrontier.REGISTRATE.block("example_block", Block::new).register();

	public static void init() {
		// load the class and register everything
		FinalFrontier.LOGGER.info("Registering blocks for " + FinalFrontier.NAME);
	}
}
