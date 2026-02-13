package pelemenguin.tinkersanalyzer.client.graph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import pelemenguin.tinkersanalyzer.client.graph.element.TextGraphElement;
import pelemenguin.tinkersanalyzer.client.graph.element.TitleGraphElement;

public class PlainTextGraph extends AnalyzerGraph {

    Component text;
    Component title;
    int maxWidth;

    public PlainTextGraph(CompoundTag tag) {
        super(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (this.text == null) {
            this.elements.clear();

            // Read text
            this.text = Component.Serializer.fromJson(tag.getString("text"));
            this.maxWidth = tag.getInt("maxWidth");
            TextGraphElement textElement = new TextGraphElement(text, this.color, 0.75f).setMaxWidth(this.maxWidth);

            // Try read title
            String stringifiedTitle = tag.getString("title");
            if (stringifiedTitle != null && !stringifiedTitle.isEmpty()) {
                this.title = Component.Serializer.fromJson(stringifiedTitle);
                TitleGraphElement titleGraphElement = new TitleGraphElement(this.title, this, this.maxWidth);
                this.addElement(titleGraphElement);
                textElement.y = titleGraphElement.getHeight();
            }

            this.addElement(textElement);
        }
        // We won't update this text since created
    }

}
