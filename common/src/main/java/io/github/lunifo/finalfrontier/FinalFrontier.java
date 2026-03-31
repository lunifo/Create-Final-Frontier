package io.github.lunifo.finalfrontier;

import com.simibubi.create.CreateBuildInfo;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinalFrontier {
    public static final String MOD_ID = "finalfrontier";
    public static final String NAME = "Create: Final Frontier";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);


    public static void init() {
        LOGGER.info("{} initializing! Create version: {} on platform: {}", NAME, CreateBuildInfo.VERSION, ExampleExpectPlatform.platformName());
        FinalFrontierBlocks.init(); // hold registrate in a separate class to avoid loading early on forge
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
