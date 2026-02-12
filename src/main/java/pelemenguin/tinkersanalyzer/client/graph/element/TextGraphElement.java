package pelemenguin.tinkersanalyzer.client.graph.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class TextGraphElement extends AnalyzerGraphElement {

    Component text;
    int color;
    int maxWidth = 0;

    public TextGraphElement(Component text, int color) {
        this.text = text;
        this.color = color;
    }

    public TextGraphElement setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        if (this.maxWidth <= 0) {
            guiGraphics.drawString(Minecraft.getInstance().font, text, 0, 0, this.color, false);
        } else {
            for (FormattedCharSequence text : Minecraft.getInstance().font.split(text, this.maxWidth)) {
                guiGraphics.drawString(Minecraft.getInstance().font, text, 0, 0, this.color, false);
            };
        }
    }

    @Override
    public int getWidth() {
        return Minecraft.getInstance().font.width(text.getVisualOrderText());
    }

    @Override
    public int getHeight() {
        return Minecraft.getInstance().font.lineHeight;
    }

}
