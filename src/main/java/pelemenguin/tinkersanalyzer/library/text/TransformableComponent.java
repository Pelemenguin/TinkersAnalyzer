package pelemenguin.tinkersanalyzer.library.text;

import net.minecraft.network.chat.Component;

public class TransformableComponent {

    private final Component raw;
    private String string = null;
    private Component transformed = null;
    private final TextTransformer transformer;

    public TransformableComponent(Component raw, TextTransformer transformer) {
        this.raw = raw;
        this.transformer = transformer;
    }

    public TransformableComponent(Component raw) {
        this(raw, TextTransformer.DEFAULT);
    }

    public Component get() {
        if (this.raw.getString() == this.string) {
            return this.transformed;
        } else {
            this.string = this.raw.getString();
            this.transformed = this.transformer.transform(this.string);
            return this.transformed;
        }
    }

}
