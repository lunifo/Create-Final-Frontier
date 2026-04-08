package io.github.lunifo.finalfrontier.contraption;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import io.github.lunifo.finalfrontier.entity.FinalFrontierEntityTypes;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class RocketPartContraptionEntity extends AbstractContraptionEntity {
	boolean inFlight = false;

	public Orientation orientation = new Orientation();
	private Orientation prevOrientation = new Orientation();

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
	public void tick() {
		super.tick();
		if (inFlight) {
			setContraptionMotion(getDeltaMovement().add(new Vec3(0, .0005, 0)));
			Vec3 velocity = getDeltaMovement();
			move(velocity.x, velocity.y, velocity.z);

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

		prevOrientation = orientation.copy();
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
	public Vec3 getAnchorVec() {
		return super.getAnchorVec().subtract(0.5, 0, 0.5);
	}

	@Override
	public Vec3 getPrevAnchorVec() {
		return super.getPrevAnchorVec().subtract(0.5, 0, 0.5);
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
