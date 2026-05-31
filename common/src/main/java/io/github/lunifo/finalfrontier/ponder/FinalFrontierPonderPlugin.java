package io.github.lunifo.finalfrontier.ponder;

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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
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

	private static void shipControls(SceneBuilder scene, SceneBuildingUtil buildingUtil) {
		scene.title("ship_controls", "Using Ship Controls");
		scene.configureBasePlate(0, 0, 5);
		scene.showBasePlate();

		scene.world().showSection(buildingUtil.select().fromTo(1, 1, 2, 3, 11, 3), Direction.NORTH);

		scene.idleSeconds(1);

		scene.addKeyframe();
		scene.overlay().showOutline(PonderPalette.GREEN, "Ship Controls Highlight", buildingUtil.select().position(2, 7, 2), 20);
		scene.overlay().showControls(new Vec3(2.5, 8, 2.5), Pointing.DOWN, 20)
				.rightClick();

		scene.idleSeconds(2);

		scene.markAsFinished();
	}
}
