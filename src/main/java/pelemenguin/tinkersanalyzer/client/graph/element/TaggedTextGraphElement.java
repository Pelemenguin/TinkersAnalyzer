package pelemenguin.tinkersanalyzer.client.graph.element;

import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TaggedTextGraphElement extends AnalyzerGraphElement {

    private Supplier<Component> supplier;
    private int color = -1;
    private int tagColor = -1;
    private Component tag = null;
    private boolean tagAbove = true;

    public TaggedTextGraphElement(Supplier<Component> source) {
        this.supplier = source;
    }

    public TaggedTextGraphElement colored(int color) {
        return this.colored(color, color);
    }

    public TaggedTextGraphElement colored(int color, int tagColor) {
        this.color = color;
        this.tagColor = tagColor;
        return this;
    }

    public TaggedTextGraphElement tagAbove(Component tag) {
        this.tag = tag;
        this.tagAbove = true;
        return this;
    }

    public TaggedTextGraphElement tagBelow(Component tag) {
        this.tag = tag;
        this.tagAbove = false;
        return this;
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();
        Font font = Minecraft.getInstance().font;

        if (this.tag != null) {
            pose.pushPose();
            pose.translate(4, this.tagAbove ? 2 : 6, 0);
            int tagWidth = font.width(this.tag);
            float scale = 0.25f;
            if (tagWidth > 32) {
                scale = 8.0f / tagWidth;
            }

            pose.scale(scale, scale, 1);
            guiGraphics.drawString(font, this.tag, -tagWidth / 2, -font.lineHeight / 2, this.tagColor >= 0 ? this.tagColor :  this.color, false);
            pose.popPose();
        }

        pose.pushPose();
        Component value = this.supplier.get();
        int width = font.width(value);
        float scale = 0.5f;
        if (width > 16) {
            scale = 8.0f / width;
        }

        pose.translate(4, this.tag == null ? 4 : this.tagAbove ? 5 : 3, 0);
        pose.scale(scale, scale, 0);
        guiGraphics.drawString(font, value, -width / 2, -font.lineHeight / 2, this.color, false);
        pose.popPose();
    }

    @Override
    public int getWidth() {
        return 8;
    }

    @Override
    public int getHeight() {
        return 8;
    }

}
