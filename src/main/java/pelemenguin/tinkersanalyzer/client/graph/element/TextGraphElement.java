package pelemenguin.tinkersanalyzer.client.graph.element;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class TextGraphElement extends AnalyzerGraphElement {

    Component text;
    int color;
    int maxWidth = 0;
    float scale = 1;
    private List<FormattedCharSequence> cachedSplittedText;

    public TextGraphElement(Component text, int color) {
        this(text, color, 1.0f);
    }

    public TextGraphElement(Component text, int color, float scale) {
        this.text = text;
        this.color = color;
        this.scale = scale;
    }

    public TextGraphElement setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    private void ensureSplittedTextCached() {
        if (this.cachedSplittedText == null) {
            this.cachedSplittedText = Minecraft.getInstance().font.split(text, (int) (this.maxWidth / this.scale));
        }
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        Font font = Minecraft.getInstance().font;

        pose.scale(scale, scale, 1.0f);
        if (this.maxWidth <= 0) {
            guiGraphics.drawString(font, text, 0, 0, this.color, false);
        } else {
            int y = 0;
            this.ensureSplittedTextCached();
            for (FormattedCharSequence text : this.cachedSplittedText) {
                guiGraphics.drawString(font, text, 0, y, this.color, false);
                y += font.lineHeight;
            };
        }

        pose.popPose();
    }

    @Override
    public int getWidth() {
        int width = (int) (this.scale * Minecraft.getInstance().font.width(text.getVisualOrderText()));
        return width > this.maxWidth ? this.maxWidth : width;
    }

    @Override
    public int getHeight() {
        this.ensureSplittedTextCached();
        return (int) (Minecraft.getInstance().font.lineHeight * this.cachedSplittedText.size() * this.scale);
    }

}
