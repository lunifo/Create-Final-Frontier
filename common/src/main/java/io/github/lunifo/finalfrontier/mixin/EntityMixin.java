package io.github.lunifo.finalfrontier.mixin;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import io.github.lunifo.finalfrontier.EntityCrossDimensionPassengerTeleportation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityCrossDimensionPassengerTeleportation {
	@Shadow public abstract Level level();
	@Shadow public abstract EntityType<?> getType();
	@Shadow public abstract void setRemoved(Entity.RemovalReason removalReason);
	@Shadow public abstract List<Entity> getPassengers();

	@Shadow private Vec3 position;

	@Unique
	public Entity finalfrontier$teleportSelfAndPassengersTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float g, float h, @Nullable Entity newVehicle, @Nullable Integer seatIndex) {
		Entity newEntity = this.getType().create(level);
		if (newEntity == null) {
			return null;
		}

		// Passengers get dismounted automatically when setRemoved is called
		List<Entity> passengers = getPassengers();
		Map<UUID, Integer> seatMapping = Map.of();
		if ((Object)this instanceof AbstractContraptionEntity contraptionEntity) {
			seatMapping = new HashMap<>(contraptionEntity.getContraption().getSeatMapping());
		}

		newEntity.restoreFrom((Entity)(Object)this);
		newEntity.moveTo(x, y, z, g, Mth.clamp(h, -90.0F, 90.0F));
		newEntity.setYHeadRot(g);
		this.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
		level.addDuringTeleport(newEntity);

		if (newVehicle != null) {
			if (newVehicle instanceof AbstractContraptionEntity contraptionVehicle && seatIndex != null) {
				contraptionVehicle.addSittingPassenger(newEntity, seatIndex);
			} else {
				newEntity.startRiding(newVehicle);
			}
			newVehicle.positionRider(newEntity);
		}

		for (var passenger: passengers) {
			// Players need to be handled differently
			if (passenger instanceof ServerPlayer playerPassenger) {
				playerPassenger.teleportTo(level, x, y, z, relativeMovements, g, h);
				if (newEntity instanceof AbstractContraptionEntity contraptionEntity && seatMapping != null) {
					int playerSeatIndex = seatMapping.get(playerPassenger.getUUID());
					contraptionEntity.addSittingPassenger(playerPassenger, playerSeatIndex);
				}
				continue;
			}
			Vec3 passengerOffset = position.subtract(passenger.position());

			Integer passengerSeatIndex = null;
			if (seatMapping != null) {
				passengerSeatIndex = seatMapping.get(passenger.getUUID());
			}

			((EntityCrossDimensionPassengerTeleportation) passenger)
					.finalfrontier$teleportSelfAndPassengersTo(
							level,
							x + passengerOffset.x,
							y + passengerOffset.y,
							z + passengerOffset.z,
							relativeMovements,
							passenger.getYRot(),
							passenger.getXRot(),
							newEntity,
							passengerSeatIndex
					);
		}

		return newEntity;
	}
}
