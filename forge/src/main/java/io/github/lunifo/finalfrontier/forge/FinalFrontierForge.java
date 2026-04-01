package io.github.lunifo.finalfrontier.forge;

import io.github.lunifo.finalfrontier.FinalFrontier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FinalFrontier.MOD_ID)
public class FinalFrontierForge {
    public FinalFrontierForge() {
        // registrate must be given the mod event bus on forge before registration
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FinalFrontier.REGISTRATE.registerEventListeners(eventBus);
        FinalFrontier.init();
    }
}
