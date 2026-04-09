package io.github.lunifo.finalfrontier.forge;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;

// We don't use Forge datagen so these don't need to do anything
public class BlockStateGeneratorsImpl {
	public static void genericCubeBottomTop(DataGenContext<Block, ?> context, RegistrateBlockstateProvider provider) {}
	public static void stageDecoupler(DataGenContext<Block, ?> context, RegistrateBlockstateProvider provider) {}
}
