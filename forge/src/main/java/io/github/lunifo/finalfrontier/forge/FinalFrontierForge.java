package io.github.lunifo.finalfrontier.forge;

import io.github.lunifo.finalfrontier.FinalFrontier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(FinalFrontier.MOD_ID)
public class FinalFrontierForge {
    public FinalFrontierForge() {
        // registrate must be given the mod event bus on forge before registration
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FinalFrontier.REGISTRATE.registerEventListeners(eventBus);
        FinalFrontier.init();

        PlatformHelperImpl.ParticleRegistrationHelperForge.register(eventBus);
        eventBus.addListener(FinalFrontierForge::onRegister);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(PlatformHelperImpl.ParticleRegistrationHelperForge::registerClient);
        });
    }

    private static void onRegister(RegisterEvent event) {
        FinalFrontier.registerCreateDependent();
    }
}
