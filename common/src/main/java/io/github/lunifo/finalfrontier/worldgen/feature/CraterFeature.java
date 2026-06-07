package io.github.lunifo.finalfrontier.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.Vec3;

public class CraterFeature extends Feature<CraterFeature.Config> {
	public CraterFeature() {
		super(Config.CODEC);
	}

	@Override
	public boolean place(FeaturePlaceContext<Config> ctx) {
		Config config = ctx.config();
		BlockPos origin = ctx.origin();
		Vec3 originVec = origin.getCenter();
		WorldGenLevel level = ctx.level();
		float radius = config.radius + 0.01F; // Nudge the radius to prevent floating point precision issues
		int maxHeight = 10;

		for (int y = 0; y < maxHeight; y++) {
			for (int x = -config.radius; x <= config.radius; x++) {
				for (int z = -config.radius; z <= config.radius; z++) {
					BlockPos currentPos = origin.offset(x, y - config.depth, z);
					Vec3 diff = currentPos.atY(origin.getY()).getCenter().subtract(originVec);
					if (diff.lengthSqr() <= radius * radius) {
						level.setBlock(currentPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
					}
				}
			}
		}

		return true;
	}

	public record Config(int radius, int depth) implements FeatureConfiguration {
		public static final Codec<Config> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
					Codec.INT.fieldOf("radius").forGetter(Config::radius),
					Codec.INT.fieldOf("depth").forGetter(Config::depth)
				).apply(instance, Config::new)
		);
	}
}
