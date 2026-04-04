package io.github.lunifo.finalfrontier.contraption;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import io.github.lunifo.finalfrontier.FinalFrontier;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class FinalFrontierContraptionTypes {
	public static Holder.Reference<ContraptionType> ROCKET_PART = Registry.registerForHolder(
			CreateBuiltInRegistries.CONTRAPTION_TYPE,
			new ResourceLocation(FinalFrontier.MOD_ID, "rocket_part"),
			new ContraptionType(RocketPartContraption::new)
	);

	public static void init() {

	}
}
