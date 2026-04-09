package io.github.lunifo.finalfrontier.contraption;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import io.github.lunifo.finalfrontier.block.FinalFrontierBlocks;
import io.github.lunifo.finalfrontier.block.StageDecouplerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;

public class RocketPartContraption extends Contraption {
	public Set<Pair<BlockPos, BlockPos>> decouplerPairs = new HashSet<>();
	public BlockPos decouplerAnchor;

	@Override
	public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
		boolean success = searchMovedStructure(world, pos, null);

		addBlock(world, pos, Pair.of(new StructureTemplate.StructureBlockInfo(pos, world.getBlockState(pos), null), null));

		return success;
	}

	@Override
	protected void addBlock(Level level, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair) {
		super.addBlock(level, pos, pair);

		BlockState blockState = level.getBlockState(pos);
		if (blockState.is(FinalFrontierBlocks.STAGE_DECOUPLER.get())) {
			Direction decouplerDirection = blockState.getValue(StageDecouplerBlock.FACING);
			BlockPos connectedPos = pos.relative(decouplerDirection);
			BlockState connectedState = level.getBlockState(connectedPos);
			if (connectedState.is(FinalFrontierBlocks.STAGE_DECOUPLER.get())) {
				Direction connectedDecouplerDirection = connectedState.getValue(StageDecouplerBlock.FACING);
				if (decouplerDirection == connectedDecouplerDirection.getOpposite()) {
					Pair<BlockPos, BlockPos> reverseDecouplerPair = Pair.of(connectedPos, pos);
					if (decouplerPairs.contains(reverseDecouplerPair)) {
						decouplerPairs.remove(reverseDecouplerPair);
					} else {
						decouplerPairs.add(Pair.of(pos, connectedPos));
					}
				}
			}
		}
	}

	@Override
	public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
		super.readNBT(world, nbt, spawnData);
		if (nbt.contains("DecouplerAnchor")) {
			decouplerAnchor = NbtUtils.readBlockPos(nbt.getCompound("DecouplerAnchor"));
		}
		if (nbt.contains("DecouplerPairs")) {
			ListTag decouplerTags = nbt.getList("DecouplerPairs", 10);
			for (var tag : decouplerTags) {
				ListTag pair = (ListTag) tag;
				BlockPos pos1 = NbtUtils.readBlockPos(pair.getCompound(0));
				BlockPos pos2 = NbtUtils.readBlockPos(pair.getCompound(1));
				decouplerPairs.add(Pair.of(pos1, pos2));
			}
		}
	}

	@Override
	public CompoundTag writeNBT(boolean spawnPacket) {
		CompoundTag nbt = super.writeNBT(spawnPacket);
		if (decouplerAnchor != null) {
			nbt.put("DecouplerAnchor", NbtUtils.writeBlockPos(decouplerAnchor));
		}

		ListTag decouplerTags = new ListTag();
		for (var pair : decouplerPairs) {
			ListTag pairTag = new ListTag();
			pairTag.add(NbtUtils.writeBlockPos(pair.getLeft()));
			pairTag.add(NbtUtils.writeBlockPos(pair.getRight()));
			decouplerTags.add(pairTag);
		}
		nbt.put("DecouplerPairs", decouplerTags);
		return nbt;
	}

	@Override
	protected boolean isAnchoringBlockAt(BlockPos pos) {
		return false;
	}

	@Override
	public boolean canBeStabilized(Direction facing, BlockPos localPos) {
		return false;
	}

	@Override
	public ContraptionType getType() {
		return FinalFrontierContraptionTypes.ROCKET_PART.value();
	}
}
