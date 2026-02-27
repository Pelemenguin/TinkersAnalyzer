package pelemenguin.tinkersanalyzer.client.util.render;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;

public class TriangleFanHelper {

    private static Tesselator tesselator;
    private static BufferBuilder builder;
    private static boolean isStarted = false;

    public static void prepareDrawTriangleFan(float x1, float y1, float x2, float y2, Matrix4f matrix, int color) {
        assertStatus(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        tesselator = Tesselator.getInstance();
        builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        isStarted = true;

        builder.vertex(matrix, x1, y1, 0).color(color).endVertex();
        builder.vertex(matrix, x2, y2, 0).color(color).endVertex();
    }

    public static void addVertex(float x, float y, Matrix4f matrix, int color) {
        assertStatus(true);
        builder.vertex(matrix, x, y, 0).color(color).endVertex();
    }

    public static void finishDrawTriangleFan() {
        assertStatus(true);
        tesselator.end();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        tesselator = null;
        builder = null;
        isStarted = false;
    }

    private static void assertStatus(boolean shouldStarted) {
        if (shouldStarted != isStarted) {
            throw new IllegalStateException("Wrong state! It %s but actually it %s".formatted(
                    shouldStarted ? "should start already" : "shouldn't start yet",
                    isStarted ? "did" : "didn't"
                ));
        }
    }

}
