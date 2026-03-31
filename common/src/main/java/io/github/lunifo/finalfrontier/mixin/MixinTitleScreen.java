package io.github.lunifo.finalfrontier.mixin;

import io.github.lunifo.finalfrontier.FinalFrontier;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {
    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        FinalFrontier.LOGGER.info("Hello from {}!", FinalFrontier.class.getName());
    }
}