package io.github.lunifo.finalfrontier.fabric;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.EntityBuilder;

public class PlatformHelperImpl {
	public static <T extends AbstractContraptionEntity> EntityBuilder<T, CreateRegistrate> setContraptionProperties(CreateEntityBuilder<T, CreateRegistrate> builder) {
		return builder
				.visual(() -> ContraptionVisual::new)
				.properties(properties -> properties.trackRangeChunks(15).trackedUpdateRate(3).forceTrackedVelocityUpdates(true).fireImmune())
				.properties(AbstractContraptionEntity::build);
	}
}
