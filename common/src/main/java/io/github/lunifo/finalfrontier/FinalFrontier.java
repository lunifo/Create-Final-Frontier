package io.github.lunifo.finalfrontier;

import com.simibubi.create.CreateBuildInfo;
import com.simibubi.create.foundation.data.CreateRegistrate;
import io.github.lunifo.finalfrontier.contraption.FinalFrontierContraptionTypes;
import io.github.lunifo.finalfrontier.entity.FinalFrontierEntityTypes;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinalFrontier {
    public static final String MOD_ID = "finalfrontier";
    public static final String NAME = "Create: Final Frontier";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

    public static void init() {
        LOGGER.info("{} initializing! Create version: {}", NAME, CreateBuildInfo.VERSION);
        FinalFrontierBlocks.init();
        FinalFrontierEntityTypes.init();
    }

    public static void registerCreateDependent() {
        FinalFrontierContraptionTypes.init();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
