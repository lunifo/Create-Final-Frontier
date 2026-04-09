package io.github.lunifo.finalfrontier;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.block.Block;

// TODO: - Fix this absolute behemoth
public class BlockStateGenerators {
	@ExpectPlatform
	public static void genericCubeBottomTop(DataGenContext<Block, ?> context, RegistrateBlockstateProvider provider) {}
	@ExpectPlatform
	public static void stageDecoupler(DataGenContext<Block, ?> context, RegistrateBlockstateProvider provider) {}
}
