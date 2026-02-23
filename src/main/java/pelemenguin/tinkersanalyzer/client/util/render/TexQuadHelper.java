package pelemenguin.tinkersanalyzer.client.util.render;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public final class TexQuadHelper {

    private static Tesselator tesselator;
    private static BufferBuilder builder;
    private static boolean isStarted;

    public static void prepareDrawTexQuad(ResourceLocation shaderTexture) {
        assertStatus(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, shaderTexture);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();

        tesselator = Tesselator.getInstance();
        builder = tesselator.getBuilder();
        builder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        isStarted = true;
    }

    public static void drawAxisAlignedTexQuad(float x1, float y1, float x2, float y2, float u0, float v0, float u1, float v1, Matrix4f matrix, int color) {
        assertStatus(true);
        builder.vertex(matrix, x1, y1, 0).uv(u0, v0).color(color).endVertex();
        builder.vertex(matrix, x1, y2, 0).uv(u0, v1).color(color).endVertex();
        builder.vertex(matrix, x2, y2, 0).uv(u1, v1).color(color).endVertex();
        builder.vertex(matrix, x2, y1, 0).uv(u1, v0).color(color).endVertex();
    }

    public static void drawAxisAlignedTexQuadHorizontalRepeat(float x1, float y1, float x2, float y2, float u0, float v0, float u1, float v1, float unitX, Matrix4f matrix, int color) {
        assertStatus(true);
        float curX = x1 + unitX;
        if (curX < x2) {
            for (; curX < x2; curX += unitX) {
                drawAxisAlignedTexQuad(curX - unitX, y1, curX, y2, u0, v0, u1, v1, matrix, color);
            }
        }
        drawAxisAlignedTexQuad(curX - unitX, y1, x2, y2, u0, v0, u1 - (u1 - u0) * (curX - x2) / unitX, v1, matrix, color);
    }

    public static void drawAxisAlignedTexQuadVerticalRepeat(float x1, float y1, float x2, float y2, float u0, float v0, float u1, float v1, float unitY, Matrix4f matrix, int color) {
        assertStatus(true);
        float curY = y1 + unitY;
        if (curY < y2) {
            for (; curY < y2; curY += unitY) {
                drawAxisAlignedTexQuad(x1, curY - unitY, x2, curY, u0, v0, u1, v1, matrix, color);
            }
        }
        drawAxisAlignedTexQuad(x1, curY - unitY, x2, y2, u0, v0, u1, v1 - (v1 - v0) * (curY - y2) / unitY, matrix, color);
    }

    public static void drawAxisAlignedTexQuadRepeat(float x1, float y1, float x2, float y2, float u0, float v0, float u1, float v1, float unitX, float unitY, Matrix4f matrix, int color) {
        assertStatus(true);
        float curY = y1 + unitY;
        if (curY < y2) {
            for (; curY < y2; curY += unitY) {
                drawAxisAlignedTexQuadHorizontalRepeat(x1, curY - unitY, x2, curY, u0, v0, u1, v1, unitX, matrix, color);
            }
        }
        drawAxisAlignedTexQuadHorizontalRepeat(x1, curY - unitY, x2, y2, u0, v0, u1, v1 - (v1 - v0) * (curY - y2) / unitY, unitX, matrix, color);
    }

    public static void finishDrawTexQuad() {
        assertStatus(true);
        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
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

    private TexQuadHelper() {}

}
