package io.github.lunifo.finalfrontier.contraption;

import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.OrientedContraptionEntity;
import io.github.lunifo.finalfrontier.entity.FinalFrontierEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class RocketPartContraptionEntity extends OrientedContraptionEntity {
	boolean inFlight = false;
	// Temporary variable for testing
	BlockPos initialPos;

	public RocketPartContraptionEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	public static RocketPartContraptionEntity create(Level level, Contraption contraption) {
		RocketPartContraptionEntity rocketPartEntity = new RocketPartContraptionEntity(FinalFrontierEntityTypes.ROCKET_PART.get(), level);
		rocketPartEntity.setContraption(contraption);
		rocketPartEntity.setInitialOrientation(Direction.UP);
		rocketPartEntity.startAtInitialYaw();

		// Temporary variable for testing
		rocketPartEntity.initialPos = contraption.anchor;
		return rocketPartEntity;
	}

	@Override
	public void tick() {
		super.tick();
		if (inFlight) {
			move(0, 0.5, 0);

			// Temporary test flight
			if (getY() > 256) {
				if (initialPos != null) {
					setPos(Vec3.atBottomCenterOf(initialPos));
					inFlight = false;
					if (!level().isClientSide()) {
						disassemble();
					}
				}
			}
		}
	}

	@Override
	protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
		super.readAdditional(compound, spawnPacket);
		inFlight = compound.getBoolean("InFlight");

		// Temporary variable for testing
		int[] initialPosArray = compound.getIntArray("InitialPos");
		if (initialPosArray.length != 0) {
			initialPos = new BlockPos(
					initialPosArray[0],
					initialPosArray[1],
					initialPosArray[2]
			);
		}
	}

	@Override
	protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
		super.writeAdditional(compound, spawnPacket);
		compound.putBoolean("InFlight", inFlight);

		// Temporary variable for testing
		if (initialPos != null) {
			int[] initialPosArray = {initialPos.getX(), initialPos.getY(), initialPos.getZ()};
			compound.putIntArray("InitialPos", initialPosArray);
		}
	}

	public void startFlight() {
		inFlight = true;
	}
}
