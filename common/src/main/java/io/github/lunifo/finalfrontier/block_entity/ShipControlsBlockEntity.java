package io.github.lunifo.finalfrontier.block_entity;

import com.mojang.serialization.DataResult;
import io.github.lunifo.finalfrontier.FinalFrontier;
import io.github.lunifo.finalfrontier.celestial_body.CelestialBody;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShipControlsBlockEntity extends BlockEntity {
	private CelestialBody currentCelestialBody;

	public ShipControlsBlockEntity(BlockEntityType<ShipControlsBlockEntity> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag compoundTag) {
		if (getCurrentCelestialBody() != null) {
			DataResult<Tag> result = CelestialBody.CELESTIAL_BODIES.byResourceLocationCodec().encodeStart(NbtOps.INSTANCE, getCurrentCelestialBody());
			result.resultOrPartial(FinalFrontier.LOGGER::error).ifPresent(tag -> compoundTag.put("CelestialBody", tag));
		}

		super.saveAdditional(compoundTag);
	}

	@Override
	public void load(@NotNull CompoundTag compoundTag) {
		super.load(compoundTag);

		Tag serializedCelestialBody = compoundTag.get("CelestialBody");
		if (serializedCelestialBody != null) {
			DataResult<CelestialBody> result = CelestialBody.CELESTIAL_BODIES.byResourceLocationCodec().parse(NbtOps.INSTANCE, serializedCelestialBody);
			result.resultOrPartial(FinalFrontier.LOGGER::error).ifPresent(this::setCurrentCelestialBody);
		}
	}

	public void setCurrentCelestialBody(@Nullable CelestialBody body) {
		currentCelestialBody = body;
		setChanged();
	}

	public @Nullable CelestialBody getCurrentCelestialBody() {
		return currentCelestialBody;
	}

	public CelestialBody cycleCelestialBody() {
		List<CelestialBody> allCelestialBodies = List.copyOf(CelestialBody.CELESTIAL_BODIES.values());
		int index = allCelestialBodies.indexOf(getCurrentCelestialBody());
		index++;
		if (index == allCelestialBodies.size()) {
			index = 0;
		}
		CelestialBody newCelestialBody = allCelestialBodies.get(index);
		setCurrentCelestialBody(newCelestialBody);
		return newCelestialBody;
	}
}
