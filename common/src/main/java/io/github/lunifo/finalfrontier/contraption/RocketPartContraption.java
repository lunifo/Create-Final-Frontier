package io.github.lunifo.finalfrontier.contraption;

import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;

public class RocketPartContraption extends Contraption {
	@Override
	public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
		searchMovedStructure(world, pos, null);

		addBlock(world, pos, Pair.of(new StructureTemplate.StructureBlockInfo(pos, world.getBlockState(pos), null), null));

		return false;
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
