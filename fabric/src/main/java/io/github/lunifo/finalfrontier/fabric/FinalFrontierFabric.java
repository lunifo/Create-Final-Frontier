package io.github.lunifo.finalfrontier.fabric;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import io.github.lunifo.finalfrontier.FinalFrontier;
import net.fabricmc.api.ModInitializer;

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
    }
}
