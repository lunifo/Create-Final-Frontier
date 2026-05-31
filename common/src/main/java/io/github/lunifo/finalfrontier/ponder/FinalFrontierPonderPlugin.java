package io.github.lunifo.finalfrontier.ponder;

import com.simibubi.create.AllItems;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.block.FinalFrontierBlocks;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class FinalFrontierPonderPlugin implements PonderPlugin {
	@Override
	public @NotNull String getModId() {
		return FinalFrontier.MOD_ID;
	}

	@Override
	public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> rawHelper) {
		PonderSceneRegistrationHelper<ItemProviderEntry<?>> helper = rawHelper.withKeyFunction(RegistryEntry::getId);

		helper.forComponents(FinalFrontierBlocks.SHIP_CONTROLS)
				.addStoryBoard("ship_controls", FinalFrontierPonderPlugin::shipControls);
	}

	private static void shipControls(SceneBuilder scene, SceneBuildingUtil util) {
		scene.title("ship_controls", "Using Ship Controls");
		scene.configureBasePlate(0, 0, 5);
		scene.showBasePlate();
		scene.setSceneOffsetY(-2);

		scene.idle(10);
		scene.world().showSection(util.select().fromTo(1, 1, 2, 3, 11, 3), Direction.NORTH);
		scene.idleSeconds(1);

		// Glue rocket
		scene.addKeyframe();

		scene.overlay().showControls(util.vector().blockSurface(new BlockPos(1, 1, 2), Direction.WEST), Pointing.LEFT, 10)
				.rightClick()
				.withItem(AllItems.SUPER_GLUE.asStack());
		scene.idle(5);
		scene.effects().indicateSuccess(new BlockPos(1, 1, 2));
		scene.idleSeconds(1);

		AABB glueBox = new AABB(new BlockPos(1, 1, 2));
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, "Rocket Glue", glueBox, 1);
		scene.overlay().chaseBoundingBoxOutline(PonderPalette.GREEN, "Rocket Glue", glueBox.expandTowards(2, 10, 1), seconds(2));

		scene.idle(10);

		scene.overlay().showControls(util.vector().blockSurface(new BlockPos(3, 11, 3), Direction.EAST), Pointing.RIGHT, 10)
				.rightClick()
				.withItem(AllItems.SUPER_GLUE.asStack());
		scene.idle(5);
		scene.effects().indicateSuccess(new BlockPos(3, 11, 3));
		scene.idleSeconds(1);

		scene.overlay().showText(seconds(2.5))
				.text("First glue your rocket together")
				.colored(PonderPalette.GREEN)
				.pointAt(util.vector().blockSurface(new BlockPos(1, 1, 2), Direction.WEST))
				.placeNearTarget();

		scene.idleSeconds(3);

		// Assemble rocket
		scene.addKeyframe();
		scene.overlay().showOutline(PonderPalette.BLUE, "Ship Controls Highlight", util.select().position(2, 7, 2), seconds(5));
		scene.overlay().showControls(util.vector().topOf(2, 7, 2), Pointing.DOWN, seconds(1))
				.rightClick();
		scene.idleSeconds(1);
		scene.overlay().showText(seconds(4))
				.text("Assemble the rocket by clicking the ship controls")
				.colored(PonderPalette.BLUE)
				.pointAt(util.vector().blockSurface(new BlockPos(2, 7, 2), Direction.NORTH))
				.placeNearTarget();

		scene.idleSeconds(5);

		scene.markAsFinished();
	}

	private static int seconds(double ticks) {
		return (int)(ticks * 20);
	}
}
