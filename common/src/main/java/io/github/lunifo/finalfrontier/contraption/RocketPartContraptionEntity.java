package io.github.lunifo.finalfrontier.contraption;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.block.FinalFrontierBlocks;
import io.github.lunifo.finalfrontier.block_entity.ShipControlsBlockEntity;
import io.github.lunifo.finalfrontier.celestial_body.CelestialBody;
import io.github.lunifo.finalfrontier.worldgen.dimension.FinalFrontierDimensions;
import io.github.lunifo.finalfrontier.entity.FinalFrontierEntityTypes;
import io.github.lunifo.finalfrontier.EntityCrossDimensionPassengerTeleportation;
import io.github.lunifo.finalfrontier.util.PlayerUtil;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class RocketPartContraptionEntity extends AbstractContraptionEntity {
	private static final EntityDataAccessor<Float> THROTTLE = SynchedEntityData.defineId(RocketPartContraptionEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Vector3f> ORIENTATION = SynchedEntityData.defineId(RocketPartContraptionEntity.class, EntityDataSerializers.VECTOR3);

	public Orientation orientation = new Orientation();
	private Orientation prevOrientation = new Orientation();

	private final Orientation rotationSpeed = new Orientation();

	private int despawnTicks;
	private boolean isDetached;

	public CelestialBody currentCelestialBody;

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
		this.entityData.define(ORIENTATION, new Vector3f());
	}

	@Override
	public void tick() {
		super.tick();

		if (level().isClientSide) {
			prevOrientation = orientation;
			orientation = getSyncedOrientation();
		}

		if (level().dimension() == FinalFrontierDimensions.DEEP_SPACE && !level().isClientSide) {
			if (currentCelestialBody == null) {
				disassemble();
				return;
			}

			ResourceKey<Level> dimensionKey = currentCelestialBody.dimensionKey();
			if (dimensionKey == null) {
				disassemble();
				return;
			}

			ServerLevel level = Objects.requireNonNull(level().getServer()).getLevel(dimensionKey);
			if (level == null) {
				disassemble();
				return;
			}

			((EntityCrossDimensionPassengerTeleportation)this).finalfrontier$teleportSelfAndPassengersTo(
					level,
					0.5, 511, 0.5,
					Set.of(),
					0, 0,
					null, null
			);
		}

		if (!level().isClientSide) {
			rotationSpeed.pitch *= 0.8F;
			rotationSpeed.roll *= 0.8F;
			rotationSpeed.yaw *= 0.8F;

			prevOrientation = orientation.copy();

			orientation.pitch += rotationSpeed.pitch;
			orientation.roll += rotationSpeed.roll;
			orientation.yaw += rotationSpeed.yaw;

			setSyncedOrientation(orientation);
		}

		applyForces();
		Vec3 velocity = getDeltaMovement().multiply(0.05, 0.05, 0.05);
		move(velocity.x, velocity.y, velocity.z);

		BlockPos collisionPos = terrainCollisionPos();
		if (collisionPos != null) {
			setContraptionMotion(Vec3.ZERO);
			setPos(collisionPos.getCenter().add(0, 0.5, 0));
			setOrientation(new Orientation());
		}

		if (getY() > PlayerUtil.SPACE_TRANSITION_END) {
			if (!level().isClientSide) {
				ServerLevel space = Objects.requireNonNull(level().getServer()).getLevel(FinalFrontierDimensions.DEEP_SPACE);
				assert space != null;
				RocketPartContraptionEntity newEntity = (RocketPartContraptionEntity)((EntityCrossDimensionPassengerTeleportation)this).finalfrontier$teleportSelfAndPassengersTo(
						space,
						0.5,
						100,
						0.5,
						Set.of(),
						0,
						0,
						null,
						null
				);
				newEntity.entityData.set(THROTTLE, 0.0F);
				newEntity.disassemble();

				BlockPos controlsPos = BlockPos.containing(newEntity.position());
				BlockState controlsState = space.getBlockState(controlsPos);
				if (controlsState.is(FinalFrontierBlocks.SHIP_CONTROLS.get()) && space.getBlockEntity(controlsPos) instanceof ShipControlsBlockEntity shipControls) {
					CelestialBody body = CelestialBody.DIMENSION_LOOKUP.get(level().dimension());
					FinalFrontier.LOGGER.info(body.dimensionKey().toString());
					shipControls.setCurrentCelestialBody(body);
				}
			}
		}

		if (isDetached) {
			if (despawnTicks-- == 0) {
				discard();
			}
		}
	}

	private void applyForces() {
		float throttle = getThrottle();
		Vec3 engineAcceleration = new Vec3(0, 0.7, 0).multiply(throttle, throttle, throttle);
		engineAcceleration = applyRotation(engineAcceleration, 0);

		Vec3 gravityAcceleration = new Vec3(0, (-9.81) / 20, 0);

		Vec3 totalAcceleration = engineAcceleration.add(gravityAcceleration);
		Vec3 newVelocity = getDeltaMovement().add(totalAcceleration);
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

		if (getVehicle() == null) {
			if (heldControls.contains(0))
				rotationSpeed.pitch += 1;
			if (heldControls.contains(1))
				rotationSpeed.pitch -= 1;
			if (heldControls.contains(2))
				rotationSpeed.roll -= 1;
			if (heldControls.contains(3))
				rotationSpeed.roll += 1;
		}

		return true;
	}

	@Override
	protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
		super.readAdditional(compound, spawnPacket);
		entityData.set(THROTTLE, compound.getFloat("Throttle"));

		DataResult<Orientation> result = Orientation.CODEC.parse(NbtOps.INSTANCE, compound.get("Orientation"));
		result.resultOrPartial(FinalFrontier.LOGGER::error).ifPresent(this::setOrientation);
	}

	@Override
	protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
		super.writeAdditional(compound, spawnPacket);
		compound.putFloat("InFlight", getThrottle());

		DataResult<Tag> result = Orientation.CODEC.encodeStart(NbtOps.INSTANCE, getSyncedOrientation());
		result.resultOrPartial(FinalFrontier.LOGGER::error).ifPresent(tag -> compound.put("Orientation", tag));
	}

	public Orientation getSyncedOrientation() {
		Vector3f orientationVec = entityData.get(ORIENTATION);
		return Orientation.fromVec(new Vec3(orientationVec));
	}

	public void setSyncedOrientation(Orientation orientation) {
		Vector3f orientationVec = orientation.asVec().toVector3f();
		entityData.set(ORIENTATION, orientationVec);
	}

	public void setOrientation(Orientation orientation) {
		setSyncedOrientation(orientation);
		this.prevOrientation = this.orientation.copy();
		this.orientation = orientation;
	}

	public static class Orientation {
		public static final Codec<Orientation> CODEC = Vec3.CODEC.xmap(Orientation::fromVec, Orientation::asVec);

		public float pitch;
		public float roll;
		public float yaw;

		public static Orientation fromVec(Vec3 vec) {
			Orientation orientation = new Orientation();
			orientation.pitch = (float) vec.x;
			orientation.roll = (float) vec.y;
			orientation.yaw = (float) vec.z;
			return orientation;
		}

		public Vec3 asVec() {
			return new Vec3(pitch, roll, yaw);
		}

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
