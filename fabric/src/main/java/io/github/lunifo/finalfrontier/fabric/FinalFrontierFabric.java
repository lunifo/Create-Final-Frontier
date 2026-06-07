package io.github.lunifo.finalfrontier.fabric;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.registry.SimpleDynamicRegistry;
import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

public class FinalFrontierFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		FinalFrontier.init();
		FinalFrontier.LOGGER.info(EnvExecutor.unsafeRunForDist(
				() -> () -> "{} is accessing Porting Lib on a Fabric client!",
				() -> () -> "{} is accessing Porting Lib on a Fabric server!"
		), FinalFrontier.NAME);
		// on fabric, Registrates must be explicitly finalized and registered.
		FinalFrontier.REGISTRATE.register();

		FinalFrontier.REGISTRATE.addDataGenerator(ProviderType.LANG, FinalFrontierFabric::addPonderLang);

		FinalFrontier.registerCreateDependent();

		SimpleDynamicRegistry.registerAll((id, registry) -> ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public ResourceLocation getFabricId() {
						return new ResourceLocation(FinalFrontier.MOD_ID, id);
					}

					@Override
					public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
						registry.onResourceManagerReload(resourceManager);
					}
				})
		);
	}

	private static void addPonderLang(RegistrateLangProvider provider) {
		PonderIndex.getLangAccess().provideLang(FinalFrontier.MOD_ID, provider::add);
	}
}
