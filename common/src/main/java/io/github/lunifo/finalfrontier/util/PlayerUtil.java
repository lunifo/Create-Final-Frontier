package io.github.lunifo.finalfrontier.util;

import io.github.lunifo.finalfrontier.worldgen.dimension.FinalFrontierDimensions;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerUtil {
	public static final double SPACE_TRANSITION_START = 256;
	public static final double SPACE_TRANSITION_END = 512;
	public static final double SPACE_TRANSITION_LENGTH = SPACE_TRANSITION_END - SPACE_TRANSITION_START;

	public static double getSpaceTransitionFactor(@Nullable LocalPlayer player) {
		if (player == null) {
			return 0;
		}

		ResourceKey<Level> dimensionKey = player.level().dimension();
		if (dimensionKey == FinalFrontierDimensions.DEEP_SPACE || dimensionKey == FinalFrontierDimensions.MOON) {
			return 1;
		}

		double height = player.getEyeY();
		double transitionFactor = (height - SPACE_TRANSITION_START) / SPACE_TRANSITION_LENGTH;
		return Math.clamp(transitionFactor, 0, 1);
	}
}
