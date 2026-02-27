package pelemenguin.tinkersanalyzer.library.text;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;

/**
 * A util class to transform texts into formatted {@link Component}.
 * 
 * <h2>Supported Formats</h2>
 * 
 * <ul>
 *     <li> {@code *}, {@code **} and {@code ***} are for <i>italic</i>, <b>bold</b>, and <b><i>bold italic</i></b> style respectively.
 *     <ul>
 *         <li> {@code *} and {@code **} can be combined as {@code ***}. For example:
 *  
 * <pre><code>
 * ... outer text * italics here ** now bold italics inside ** then italics * outer ...
 * </code></pre>
 * 
 *              will be transformed into:
 * <pre>
 * ... outer text <i> italics here <b> now bold italics inside </b> then italics </i> outer ...
 * </pre>
 * 
 *              {@code **} and {@code *} are allowed to be closed in the order different from the order they start with:
 * 
 * <pre><code>
 * ... a * b ** c * d ** e...
 * </code></pre>
 * 
 *              will be transformed into:
 * 
 * <pre>
 * ... a <i> b <b> c </b></i><b> d </b> e...
 * </pre>
 * 
 *     </li>
 *     </ul>
 * </ul>
 * 
 * <h3>Customization</h3>
 * 
 * <p>
 * The styles may be customized via methods {@link #withItalicStyle(Style)}, {@link #withBoldStyle(Style)},
 * {@link #withBoldItalicStyle(Style)}, {@link #withEmptyStyle(Style)}, {@link #withErrorStyle(Style)} etc.
 * 
 * <p>
 * There are also serveral pre-defined {@code TextTransformer} presets for use.
 */
public class TextTransformer {

    public static final Style DEFAULT_ITALIC_STYLE = Style.EMPTY.withItalic(true);
    public static final Style DEFAULT_BOLD_STYLE = Style.EMPTY.withBold(true);
    public static final Style DEFAULT_EMPTY_STYLE = Style.EMPTY;
    public static final Style DEFAULT_ERROR_STYLE = Style.EMPTY.withColor(0xFF0000);

    public static final Style TOOLTIP_ITALIC_STYLE = Style.EMPTY.withColor(0x25E2CD);
    public static final Style TOOLTIP_BOLD_STYLE = Style.EMPTY.withColor(0x94FFDE).withBold(true);
    public static final Style TOOLTIP_EMPTY_STYLE = Style.EMPTY.withColor(0x00A0DD);
    private static final Style TOOLTIP_BOLD_ITALIC_STYLE = Style.EMPTY.withColor(0xD3FCE6).withBold(true).withItalic(true);

    /**
     * The {@link TextTransformer} using default styles.
     * 
     * <h4>Styles</h4>
     * <ul>
     *     <li> {@code *italic*} - <i>italic</i>
     *     <li> {@code **bold**} - <b>bold</b>
     *     <li> {@code ***bold italic***} - <b><i>bold italic</i></b>
     * </ul>
     */
    public static final TextTransformer defaultInstance = new TextTransformer();
    /**
     * The {@link TextTransformer} used in tooltips.
     * 
     * <h4>Styles</h4>
     * For better visibility, the background color has been set to {@code #000000}.
     * <div style="background-color:#000000;border-radius:15px;margin:10px;color:#FFFFFF;">
     * <ul>
     *     <li> {@code *italic*} - <a style="color:#00A0DD;">italic</a>
     *     <li> {@code **bold**} - <a style="color:#25E2CD;"><b>bold</b></a>
     *     <li> {@code ***bold italic***} - <a style="color:#D3FCE6;"><b><i>bold italic</i></b></a>
     * </ul>
     * </div>
     */
    public static final TextTransformer tooltipsInstance = new TextTransformer()
            .withEmptyStyle(TOOLTIP_EMPTY_STYLE)
            .withItalicStyle(TOOLTIP_ITALIC_STYLE)
            .withBoldStyle(TOOLTIP_BOLD_STYLE)
            .withBoldItalicStyle(TOOLTIP_BOLD_ITALIC_STYLE);

    private Style italicStyle = DEFAULT_ITALIC_STYLE;
    private Style boldStyle = DEFAULT_BOLD_STYLE;
    private Style boldItalicStyle = null; // Bold apply to Italic by default
    private Style emptyStyle = DEFAULT_EMPTY_STYLE;
    private Style errorStyle = DEFAULT_ERROR_STYLE;

    /**
     * Sets the italic style for the {@link TextTransformer}.
     * @param style The style to set
     * @return      The {@code TextTransformer} itself
     */
    public TextTransformer withItalicStyle(Style style) {
        this.italicStyle = style;
        return this;
    }
    /**
     * Sets the italic style for the {@link TextTransformer}.
     * @param style The style to set
     * @return      The {@code TextTransformer} itself
     */
    public TextTransformer withBoldStyle(Style style) {
        this.boldStyle = style;
        return this;
    }
    /**
     * Sets the bold style for the {@link TextTransformer}.
     * @param style The style to set
     * @return      The {@code TextTransformer} itself
     */
    public TextTransformer withBoldItalicStyle(Style style) {
        this.boldItalicStyle = style;
        return this;
    }
    /**
     * Sets the default style for the {@link TextTransformer}.
     * @param style The style to set
     * @return      The {@code TextTransformer} itself
     */
    public TextTransformer withEmptyStyle(Style style) {
        this.emptyStyle = style;
        return this;
    }
    /**
     * Sets the error style for the {@link TextTransformer}.
     * @param style The style to set
     * @return      The {@code TextTransformer} itself
     */
    public TextTransformer withErrorStyle(Style style) {
        this.errorStyle = style;
        return this;
    }

    /**
     * Transforms a {@link Component}.
     * @param text The {@code Component} to transform
     * @return     The transformed {@code Component}
     */
    public Component transform(String text) {
        try {
            this.ensureBoldItalicStyleInitialized();
            MutableComponent result = Component.empty();
            ArrayDeque<Style> styleStack = new ArrayDeque<>();
            styleStack.addLast(this.emptyStyle);
            int lastNode = 0; // Represents the start of each segment
            final int length = text.length();

            StringBuilder sb = new StringBuilder(length);
            int i = 0;
            while (i < text.length()) {
                char c = text.charAt(i);
                if (c == '\\') {
                    if (i < length - 1) {
                        sb.append(text.charAt(i + 1));
                        i += 2;
                        continue;
                    }
                }
                if (c == '*') {
                    if (i < length - 1) {
                        char next = text.charAt(i + 1);
                        if (c == next) {
                            handleBold(result, styleStack, lastNode, sb.toString(), i - 1);
                            lastNode = i + 2;
                            sb.delete(0, length);
                            i += 2;
                            continue;
                        }
                    }

                    handleItalics(result, styleStack, lastNode, sb.toString(), i - 1);
                    lastNode = i + 1;
                    sb.delete(0, length);
                } else {
                    sb.append(c);
                }
                i += 1;
            }
            if (!sb.isEmpty()) {
                result.append(Component.literal(sb.toString()).withStyle(this.getCurrentStyle(styleStack)));
            }
            return result;
        } catch (Throwable t) {
            return Component.translatable(TinkersAnalyzer.makeTranslationKey("special", "text_transformation_failed"),
                    Component.literal(text),
                    Component.literal(Arrays.toString(t.getStackTrace())).withStyle(this.errorStyle)
                )
                .withStyle(this.errorStyle);
        }
    }

    protected Style getCurrentStyle(ArrayDeque<Style> styleStack) {
        Iterator<Style> iterator = styleStack.iterator();
        Style start = iterator.next();
        while (iterator.hasNext()) {
            start = iterator.next().applyTo(start);
        }
        return start;
    }

    protected void ensureBoldItalicStyleInitialized() {
        if (this.boldItalicStyle == null) {
            this.boldItalicStyle = this.boldStyle.applyTo(this.italicStyle);
        }
    }

    protected void handleItalics(MutableComponent result, ArrayDeque<Style> styleStack, int lastNode, String toPut, int index) {
        result.append(Component.literal(toPut).withStyle(this.getCurrentStyle(styleStack)));
        Style last = styleStack.peekLast();
        // Use == to prevent the same italic style and bold style
        if (this.italicStyle == last) {
            // Close last italics style
            styleStack.pollLast();
        } else if (this.boldItalicStyle == last) {
            styleStack.pollLast();
            styleStack.addLast(this.boldStyle);
        } else if (this.boldStyle == last) {
            styleStack.pollLast();
            styleStack.addLast(this.boldItalicStyle);
        } else {
            styleStack.addLast(this.italicStyle);
        }
    }

    protected void handleBold(MutableComponent result, ArrayDeque<Style> styleStack, int lastNode, String toPut, int index) {
        result.append(Component.literal(toPut).withStyle(this.getCurrentStyle(styleStack)));
        Style last = styleStack.peekLast();
        // Use == to prevent the same italic style and bold style
        if (this.boldStyle == last) {
            styleStack.pollLast();
        } else if (this.boldItalicStyle == last) {
            styleStack.pollLast();
            styleStack.addLast(this.italicStyle);
        } else if (this.italicStyle == last) {
            styleStack.pollLast();
            styleStack.addLast(this.boldItalicStyle);
        } else {
            styleStack.addLast(this.boldStyle);
        }
    }

}
