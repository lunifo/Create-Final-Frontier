package io.github.lunifo.finalfrontier.fabric;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelFile;
import io.github.lunifo.finalfrontier.FinalFrontier;
import net.minecraft.world.level.block.Block;

public class BlockStateGeneratorsImpl {
	public static void genericCubeBottomTop(DataGenContext<Block, ?> context, RegistrateBlockstateProvider provider) {
		provider.simpleBlock(context.get(), cubeBottomTop(context, provider));
	}

	public static void stageDecoupler(DataGenContext<Block, ?> context, RegistrateBlockstateProvider provider) {
		provider.directionalBlock(context.get(), cubeBottomTop(context, provider));
	}

	private static ModelFile cubeBottomTop(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider provider) {
		String name = ctx.getName();
		return provider.models().cubeBottomTop(
				name,
				FinalFrontier.id("block/" + name + "_side"),
				FinalFrontier.id("block/" + name + "_bottom"),
				FinalFrontier.id("block/" + name + "_top")
		);
	}
}
