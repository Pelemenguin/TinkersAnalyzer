package pelemenguin.tinkersanalyzer.client.graph.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph;

/**
 * @deprecated This element is so ugly
 * 
 * <p>
 * An {@link AnalyzerGraphElement} displaying a title.
 * A horizontal line is drawn below the title.
 * 
 * <h1>Size</h1>
 * The element's width is equal to the {@link #minWidth} given.
 * By default, {@code minWidth} is equal to the length of the {@link text} to display.
 * However, the horizontal line's length directly depends on the parent {@link AnalyzerGraph}'s width,
 * which is usually greater that the {@code minWidth} itself.
 */
@Deprecated(since = "0.1-alpha.1")
public class TitleGraphElement extends AnalyzerGraphElement {

    @Deprecated
    AnalyzerGraph parent;
    @Deprecated
    Component text;
    @Deprecated
    int minWidth;

    /**
     * Creates a {@link TitleGraphElement}.
     * @param text     The text to be displayed as title
     * @param parent   The parent {@link AnalyzerGraph}
     * @param minWidth The minium width
     */
    @Deprecated
    public TitleGraphElement(Component text, AnalyzerGraph parent, int minWidth) {
        this.text = text;
        this.parent = parent;
        this.minWidth = minWidth;
    }

    /**
     * Creates a {@link TitleGraphElement} with {@link #minWidth} that equals to {@link #text}'s width.
     * @param text
     * @param parent
     */
    @Deprecated
    public TitleGraphElement(Component text, AnalyzerGraph parent) {
        this(text, parent, Minecraft.getInstance().font.width(text));
    }

    @Override
    @Deprecated
    public void draw(GuiGraphics guiGraphics) {
        int color = this.parent.getColor();

        guiGraphics.drawString(Minecraft.getInstance().font, this.text, 0, 0, color, false);
        guiGraphics.hLine(- this.x - 2, this.parent.getWidth() - this.x - 3, this.getHeight() - 2, (0xFF000000) | color);
    }

    /**
     * Returns the {@link #minWidth}.
     * @return {@link #minWidth}
     */
    @Override
    @Deprecated
    public int getWidth() {
        return minWidth;
    }

    /**
     * Returns the {@link TitleGraphElement}'s height.
     * The result will be 2 + font's lineheight.
     * @return The element's height
     */
    @Override
    @Deprecated
    public int getHeight() {
        return (int) (2 + Minecraft.getInstance().font.lineHeight);
    }

}
