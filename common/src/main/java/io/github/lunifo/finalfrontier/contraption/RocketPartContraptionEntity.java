package io.github.lunifo.finalfrontier.contraption;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import io.github.lunifo.finalfrontier.entity.FinalFrontierEntityTypes;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class RocketPartContraptionEntity extends OrientedContraptionEntity {
	public RocketPartContraptionEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	public static RocketPartContraptionEntity create(Level level, Contraption contraption) {
		RocketPartContraptionEntity rocketPartEntity = new RocketPartContraptionEntity(FinalFrontierEntityTypes.ROCKET_PART.get(), level);
		rocketPartEntity.setContraption(contraption);
		rocketPartEntity.setInitialOrientation(Direction.UP);
		rocketPartEntity.startAtInitialYaw();
		return rocketPartEntity;
	}
}
