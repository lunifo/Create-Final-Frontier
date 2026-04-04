package io.github.lunifo.finalfrontier.entity;

import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.tterrag.registrate.util.entry.EntityEntry;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.PlatformHelper;
import io.github.lunifo.finalfrontier.contraption.RocketPartContraptionEntity;
import net.minecraft.world.entity.MobCategory;

public class FinalFrontierEntityTypes {
	public static EntityEntry<RocketPartContraptionEntity> ROCKET_PART =
			PlatformHelper.setContraptionProperties(FinalFrontier.REGISTRATE.entity("rocket_part", RocketPartContraptionEntity::new, MobCategory.MISC))
					.renderer(() -> ContraptionEntityRenderer::new)
					.register();

	public static void init() {

	}
}
