package io.github.lunifo.finalfrontier.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class CelestialBodyRenderer {
	public static void renderCelestialBodies(PoseStack poseStack, BufferBuilder bufferBuilder) {
		RenderSystem.disableBlend();

		RenderSystem.setShaderColor(1, 1, 1, 1);

		poseStack.pushPose();
		poseStack.mulPose(Axis.XP.rotationDegrees(45));
		Matrix4f matrix = poseStack.last().pose();
		RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/block/pearlescent_froglight_top.png"));

		float size = 60;
		bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferBuilder.vertex(matrix, -size, 100, -size).uv(0, 0).endVertex();
		bufferBuilder.vertex(matrix, size, 100, -size).uv(1, 0).endVertex();
		bufferBuilder.vertex(matrix, size, 100, size).uv(1, 1).endVertex();
		bufferBuilder.vertex(matrix, -size, 100, size).uv(0, 1).endVertex();
		BufferUploader.drawWithShader(bufferBuilder.end());
		poseStack.popPose();

		RenderSystem.enableBlend();
	}
}
