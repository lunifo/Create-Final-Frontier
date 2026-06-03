package io.github.lunifo.finalfrontier.contraption;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import io.github.lunifo.finalfrontier.dimension.FinalFrontierDimensions;
import io.github.lunifo.finalfrontier.entity.FinalFrontierEntityTypes;
import io.github.lunifo.finalfrontier.EntityCrossDimensionPassengerTeleportation;
import io.github.lunifo.finalfrontier.util.PlayerUtil;
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
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class RocketPartContraptionEntity extends AbstractContraptionEntity {
	private static final EntityDataAccessor<Float> THROTTLE = SynchedEntityData.defineId(RocketPartContraptionEntity.class, EntityDataSerializers.FLOAT);

	public Orientation orientation = new Orientation();
	private Orientation prevOrientation = new Orientation();

	private int despawnTicks;
	private boolean isDetached;

	public RocketPartContraptionEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	public static RocketPartContraptionEntity create(Level level, Contraption contraption) {
		RocketPartContraptionEntity rocketPartEntity = new RocketPartContraptionEntity(FinalFrontierEntityTypes.ROCKET_PART.get(), level);
		rocketPartEntity.setContraption(contraption);

		return rocketPartEntity;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(THROTTLE, 0.0F);
	}

	@Override
	public void tick() {
		super.tick();

		applyForces();
		Vec3 velocity = getDeltaMovement().multiply(0.05, 0.05, 0.05);
		move(velocity.x, velocity.y, velocity.z);

		BlockPos collisionPos = terrainCollisionPos();
		if (collisionPos != null) {
			setContraptionMotion(Vec3.ZERO);
			setPos(collisionPos.getCenter().add(0, 0.5, 0));
		}

		if (getY() > PlayerUtil.SPACE_TRANSITION_END) {
			if (!level().isClientSide) {
				ServerLevel nether = Objects.requireNonNull(level().getServer()).getLevel(FinalFrontierDimensions.DEEP_SPACE);
				assert nether != null;
				RocketPartContraptionEntity newEntity = (RocketPartContraptionEntity)((EntityCrossDimensionPassengerTeleportation)this).finalfrontier$teleportSelfAndPassengersTo(
						nether,
						0,
						100,
						0,
						Set.of(),
						0,
						0,
						null,
						null
				);
				newEntity.entityData.set(THROTTLE, 0.0F);
				newEntity.disassemble();
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

	private void applyForces() {
		float throttle = getThrottle();
		Vec3 engineAcceleration = new Vec3(0, 0.7, 0).multiply(throttle, throttle, throttle);
		Vec3 gravityAcceleration = new Vec3(0, (-9.81) / 20, 0);
		Vec3 acceleration = engineAcceleration.add(gravityAcceleration);
		Vec3 newVelocity = getDeltaMovement().add(acceleration);
		setContraptionMotion(newVelocity);
	}

	@Nullable
	private BlockPos terrainCollisionPos() {
		BlockPos pos = BlockPos.containing(position().add(0, contraption.bounds.minY, 0));
		if (level().getBlockState(pos).isAir()) {
			return null;
		} else {
			return BlockPos.containing(position());
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

	public float getThrottle() {
		return entityData.get(THROTTLE);
	}

	public void setThrottle(float throttle) {
		entityData.set(THROTTLE, throttle);
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

		if (heldControls.contains(5)) {
			staging = true;
			stage();
		} else if (staging) {
			staging = false;
		}

		if (heldControls.contains(4)) {
			setThrottle(1);
		} else {
			setThrottle(0);
		}

		return true;
	}

	@Override
	protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
		super.readAdditional(compound, spawnPacket);
		entityData.set(THROTTLE, compound.getFloat("Throttle"));
	}

	@Override
	protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
		super.writeAdditional(compound, spawnPacket);
		compound.putFloat("InFlight", getThrottle());
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
