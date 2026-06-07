package io.github.lunifo.finalfrontier.forge;

import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.registry.SimpleDynamicRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
        MinecraftForge.EVENT_BUS.addListener(FinalFrontierForge::addReloadListeners);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(PlatformHelperImpl.ParticleRegistrationHelperForge::registerClient);
            eventBus.addListener(FinalFrontierForge::onClientInit);
        });
    }

    private static void onClientInit(FMLClientSetupEvent event) {
        FinalFrontier.clientInit();
    }

    private static void onRegister(RegisterEvent event) {
        FinalFrontier.registerCreateDependent();
    }

    private static void addReloadListeners(AddReloadListenerEvent event) {
        SimpleDynamicRegistry.registerAll((id, registry) -> event.addListener(registry));
    }
}
