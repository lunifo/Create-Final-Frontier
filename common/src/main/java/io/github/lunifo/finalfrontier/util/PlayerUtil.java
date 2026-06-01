package io.github.lunifo.finalfrontier.util;

import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

public abstract class PlayerUtil {
	public static double SPACE_TRANSITION_START = 256;
	public static double SPACE_TRANSITION_END = 2048;
	public static double SPACE_TRANSITION_LENGTH = SPACE_TRANSITION_END - SPACE_TRANSITION_START;

	public static double getSpaceTransitionFactor(@Nullable LocalPlayer player) {
		if (player == null) {
			return 0;
		}

		double height = player.getEyeY();
		double transitionFactor = (height - SPACE_TRANSITION_START) / SPACE_TRANSITION_LENGTH;
		return Math.clamp(transitionFactor, 0, 1);
	}
}
