package io.github.lunifo.finalfrontier;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.RelativeMovement;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface EntityCrossDimensionPassengerTeleportation {
	Entity finalfrontier$teleportSelfAndPassengersTo(ServerLevel level, double x, double y, double z, Set<RelativeMovement> relativeMovements, float g, float h, @Nullable Entity newVehicle, @Nullable Integer seatIndex);
}
