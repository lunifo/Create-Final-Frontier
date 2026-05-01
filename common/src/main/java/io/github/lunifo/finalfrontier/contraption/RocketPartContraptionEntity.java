package io.github.lunifo.finalfrontier.contraption;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import io.github.lunifo.finalfrontier.entity.FinalFrontierEntityTypes;
import io.github.lunifo.finalfrontier.EntityCrossDimensionPassengerTeleportation;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class RocketPartContraptionEntity extends AbstractContraptionEntity {
	private static final EntityDataAccessor<Boolean> IN_FLIGHT = SynchedEntityData.defineId(RocketPartContraptionEntity.class, EntityDataSerializers.BOOLEAN);

	public Orientation orientation = new Orientation();
	private Orientation prevOrientation = new Orientation();

	private int despawnTicks;
	private boolean isDetached;

	// Temporary variable for testing
	BlockPos initialPos;

	public RocketPartContraptionEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	public static RocketPartContraptionEntity create(Level level, Contraption contraption) {
		RocketPartContraptionEntity rocketPartEntity = new RocketPartContraptionEntity(FinalFrontierEntityTypes.ROCKET_PART.get(), level);
		rocketPartEntity.setContraption(contraption);

		// Temporary variable for testing
		rocketPartEntity.initialPos = contraption.anchor;
		return rocketPartEntity;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IN_FLIGHT, false);
	}

	@Override
	public void tick() {
		super.tick();
		if (isInFlight()) {
			setContraptionMotion(getDeltaMovement().add(new Vec3(0, .0005, 0)));
			Vec3 velocity = getDeltaMovement();
			move(velocity.x, velocity.y, velocity.z);

			// Temporary test flight
			if (getY() > 256) {
				if (!level().isClientSide) {
					ServerLevel nether = Objects.requireNonNull(level().getServer()).getLevel(Level.NETHER);
					assert nether != null;
					RocketPartContraptionEntity newEntity = (RocketPartContraptionEntity)((EntityCrossDimensionPassengerTeleportation)this).finalfrontier$teleportSelfAndPassengersTo(nether, 0, 200, 0, Set.of(), 0, 0, null);
					newEntity.entityData.set(IN_FLIGHT, false);
					newEntity.disassemble();
				}
			}
		}

		if (isDetached) {
			if (despawnTicks-- == 0) {
				discard();
			}
		}

		if (getVehicle() == null) {
			prevOrientation = orientation.copy();
			// Change orientation here
		}
	}

	public void detach() {
		stopRiding();
		isDetached = true;
		despawnTicks = 200;
	}

	public void stage() {
		boolean shouldDetach = true;
		for (var passenger : getPassengers()) {
			if (passenger instanceof RocketPartContraptionEntity rocketPartEntity) {
				shouldDetach = false;
				rocketPartEntity.stage();
			}
		}

		if (shouldDetach && getVehicle() != null) {
			detach();
		}
	}

	public boolean isInFlight() {
		return entityData.get(IN_FLIGHT);
	}

	@Override
	public void disassemble() {
		for (var passenger : getPassengers()) {
			if (passenger instanceof RocketPartContraptionEntity rocketPartEntity) {
				rocketPartEntity.disassemble();
			}
		}
		super.disassemble();
	}

	@Override
	public void load(@NotNull CompoundTag compoundTag) {
		super.load(compoundTag);
		ListTag motionList = compoundTag.getList("Motion", 6);
		setContraptionMotion(new Vec3(motionList.getDouble(0), motionList.getDouble(1), motionList.getDouble(2)));
	}

	@Override
	protected void tickContraption() {
		tickActors();
	}

	@Override
	public Vec3 applyRotation(Vec3 localPos, float partialTicks) {
		Orientation lerpedOrientation = Orientation.lerp(prevOrientation, orientation, partialTicks);
		return VecHelper.rotate(localPos, lerpedOrientation.pitch, lerpedOrientation.yaw, lerpedOrientation.roll);
	}

	@Override
	public Vec3 reverseRotation(Vec3 localPos, float partialTicks) {
		Orientation lerpedOrientation = Orientation.lerp(prevOrientation, orientation, partialTicks);
		Vec3 result = VecHelper.rotate(localPos, -lerpedOrientation.roll, Direction.Axis.Z);
		result = VecHelper.rotate(result, -lerpedOrientation.yaw, Direction.Axis.Y);
		result = VecHelper.rotate(result, -lerpedOrientation.pitch, Direction.Axis.X);
		return result;
	}

	@Override
	protected StructureTransform makeStructureTransform() {
		return new StructureTransform(BlockPos.containing(getAnchorVec().add(.5, .5, .5)), orientation.pitch, orientation.yaw, orientation.roll);
	}

	@Override
	protected float getStalledAngle() {
		return orientation.yaw;
	}

	@Override
	protected void handleStallInformation(double x, double y, double z, float angle) {
		orientation.yaw = angle;
	}

	@Override
	public ContraptionRotationState getRotationState() {
		return orientation.toRotationState();
	}

	@Override
	public void applyLocalTransforms(PoseStack matrixStack, float partialTicks) {
		Orientation lerpedOrientation = Orientation.lerp(prevOrientation, orientation, partialTicks);

		matrixStack.translate(-0.5, 0, -0.5);

		TransformStack.of(matrixStack)
				.rotateXCenteredDegrees(lerpedOrientation.pitch)
				.rotateYCenteredDegrees(lerpedOrientation.yaw)
				.rotateZCenteredDegrees(lerpedOrientation.roll);
	}

	@Override
	public Vec3 getPassengerPosition(Entity passenger, float partialTicks) {
		if (passenger instanceof RocketPartContraptionEntity rocketPartEntity) {
			BlockPos localAnchorPos = ((RocketPartContraption) rocketPartEntity.contraption).decouplerAnchor;
			if (localAnchorPos == null) {
				return null;
			}
			Vec3 localAnchorVec = localAnchorPos.getCenter();
			Vec3 globalAnchorVec = toGlobalVector(localAnchorVec, 1);
			return globalAnchorVec.subtract(0, 0.5, 0);
		} else {
			return super.getPassengerPosition(passenger, partialTicks);
		}
	}

	@Override
	public void positionRider(Entity passenger, MoveFunction callback) {
		super.positionRider(passenger, callback);
		if (passenger instanceof RocketPartContraptionEntity rocketPart) {
			rocketPart.orientation = orientation;
			rocketPart.prevOrientation = prevOrientation;
		}
	}

	@Override
	public Vec3 getAnchorVec() {
		return super.getAnchorVec().subtract(0.5, 0, 0.5);
	}

	@Override
	public Vec3 getPrevAnchorVec() {
		return super.getPrevAnchorVec().subtract(0.5, 0, 0.5);
	}

	@Override
	protected boolean canAddPassenger(Entity entity) {
		if (entity instanceof AbstractContraptionEntity) {
			return entity instanceof RocketPartContraptionEntity;
		}
		return super.canAddPassenger(entity);
	}

	@Override
	public boolean startControlling(BlockPos controlsLocalPos, Player player) {
		return player != null && !player.isSpectator();
	}

	boolean staging;
	@Override
	public boolean control(BlockPos controlsLocalPos, Collection<Integer> heldControls, Player player) {
		if (level().isClientSide)
			return true;
		if (heldControls.contains(5))
			return false;

		if (heldControls.contains(4)) {
			if (isInFlight()) {
				if (!staging) {
					staging = true;
					stage();
				}
			} else {
				staging = true;
				startFlight();
			}
		} else if (staging) {
			staging = false;
		}

		return true;
	}

	@Override
	protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
		super.readAdditional(compound, spawnPacket);
		entityData.set(IN_FLIGHT, compound.getBoolean("InFlight"));

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
		compound.putBoolean("InFlight", isInFlight());

		// Temporary variable for testing
		if (initialPos != null) {
			int[] initialPosArray = {initialPos.getX(), initialPos.getY(), initialPos.getZ()};
			compound.putIntArray("InitialPos", initialPosArray);
		}
	}

	public void startFlight() {
		entityData.set(IN_FLIGHT, true);
	}

	public static class Orientation {
		public float pitch;
		public float roll;
		public float yaw;

		public ContraptionRotationState toRotationState() {
			ContraptionRotationState rotationState = new ContraptionRotationState();

			rotationState.xRotation = pitch;
			rotationState.yRotation = yaw;
			rotationState.zRotation = roll;

			return rotationState;
		}

		public Orientation copy() {
			Orientation orientation = new Orientation();
			orientation.pitch = pitch;
			orientation.roll = roll;
			orientation.yaw = yaw;
			return orientation;
		}

		public static Orientation lerp(Orientation from, Orientation to, float progress) {
			Orientation orientation = new Orientation();

			orientation.pitch = AngleHelper.angleLerp(progress, from.pitch, to.pitch);
			orientation.roll = AngleHelper.angleLerp(progress, from.roll, to.roll);
			orientation.yaw = AngleHelper.angleLerp(progress, from.yaw, to.yaw);

			return orientation;
		}
	}
}
