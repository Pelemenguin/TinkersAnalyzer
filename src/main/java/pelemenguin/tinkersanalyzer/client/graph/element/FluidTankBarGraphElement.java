package pelemenguin.tinkersanalyzer.client.graph.element;

import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph;
import pelemenguin.tinkersanalyzer.client.util.render.QuadHelper;
import pelemenguin.tinkersanalyzer.client.util.render.TexQuadHelper;

public class FluidTankBarGraphElement extends ProgressBarGraphElement {

    public FluidTankBarGraphElement(AnalyzerGraph parent, int barLength, boolean vertical, boolean reversed) {
        super(parent, barLength, vertical, reversed);
    }

    public FluidTankBarGraphElement(AnalyzerGraph parent, int barLength, boolean vertical) {
        super(parent, barLength, vertical, false);
    }

    public FluidTankBarGraphElement(AnalyzerGraph parent, int barLength) {
        super(parent, barLength, false, false);
    }

    private FluidStack fluid;
    protected boolean ignoreDensity = false;

    public FluidTankBarGraphElement fluid(FluidStack fluid) {
        this.fluid = fluid;
        return this;
    }

    public FluidTankBarGraphElement ignoreDensiy() {
        this.ignoreDensity = true;
        return this;
    }

    protected boolean atBottom() {
        return this.ignoreDensity ? !this.reversed : (
                (!this.fluid.getFluid().getFluidType().isLighterThanAir()) ^ this.reversed
            );
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();
        Matrix4f matrix = pose.last().pose();

        QuadHelper.prepareDrawQuads();

        float x1;
        float y1;
        float x2;
        float y2;
        if (this.vertical) {
            QuadHelper.drawAxisAlignedBorderedQuad(0, 0, BAR_WIDTH + 2, this.barLength + 2, 1, matrix, 0, 0xFF000000 | this.color);
            if (this.atBottom()) {
                x1 = 1;
                y1 = this.barLength - this.progressBar.getProgress(this) + 1;
                x2 = BAR_WIDTH + 1;
                y2 = this.barLength + 1;
            } else {
                x1 = 1;
                y1 = 1;
                x2 = BAR_WIDTH + 1;
                y2 = this.progressBar.getProgress(this) + 1;
            }
        } else {
            QuadHelper.drawAxisAlignedBorderedQuad(0, 0, this.barLength + 2, BAR_WIDTH + 2, 1, matrix, 0, 0xFF000000 | this.color);
            if (this.reversed) {
                x1 = this.barLength - this.progressBar.getProgress(this) + 1;
                y1 = 1;
                x2 = this.barLength + 1;
                y2 = BAR_WIDTH + 1;
            } else {
                x1 = 1;
                y1 = 1;
                x2 = this.progressBar.getProgress(this) + 1;
                y2 = BAR_WIDTH + 1;
            }
        }

        QuadHelper.finishDrawQuads();

        this.drawFluid(x1, y1, x2, y2, matrix);
    }

    private void drawFluid(float x1, float y1, float x2, float y2, Matrix4f matrix) {
        if (this.fluid == null) return;

        IClientFluidTypeExtensions f = IClientFluidTypeExtensions.of(this.fluid.getFluid());
        ResourceLocation tex = f.getStillTexture(this.fluid);
        if (tex == null) return;
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(tex);
        int color = f.getTintColor(this.fluid);

        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();

        TexQuadHelper.prepareDrawTexQuad(InventoryMenu.BLOCK_ATLAS);
        TexQuadHelper.drawAxisAlignedTexQuadRepeat(x1, y1, x2, y2, u0, v0, u1, v1, BAR_WIDTH, BAR_WIDTH, matrix, color);
        TexQuadHelper.finishDrawTexQuad();
    }

}
