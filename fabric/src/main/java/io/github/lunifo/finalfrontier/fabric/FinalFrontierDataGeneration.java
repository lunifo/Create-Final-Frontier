package io.github.lunifo.finalfrontier.fabric;

import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.lunifo.finalfrontier.FinalFrontier;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class FinalFrontierDataGeneration implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(RecipeProvider::new);
		FinalFrontier.REGISTRATE.setupDatagen(pack, ExistingFileHelper.withResourcesFromArg());
	}

	private static class RecipeProvider extends FabricRecipeProvider {
		public RecipeProvider(FabricDataOutput output) {
			super(output);
		}

		@Override
		public void buildRecipes(Consumer<FinishedRecipe> consumer) {

		}
	}
}
