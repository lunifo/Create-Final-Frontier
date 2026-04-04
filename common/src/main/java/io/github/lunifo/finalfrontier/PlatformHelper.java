package io.github.lunifo.finalfrontier;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.EntityBuilder;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformHelper {
	@ExpectPlatform
	public static <T extends AbstractContraptionEntity> EntityBuilder<T, CreateRegistrate> setContraptionProperties(CreateEntityBuilder<T, CreateRegistrate> builder) {
		throw new AssertionError();
	}
}
