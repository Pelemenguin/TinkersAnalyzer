package pelemenguin.tinkersanalyzer.client.graph;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import pelemenguin.tinkersanalyzer.client.graph.element.TextGraphElement;

public class PlainTextGraph extends AnalyzerGraph {

    Component text;
    int maxWidth;

    public PlainTextGraph(CompoundTag tag) {
        super(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (this.text == null) {
            this.elements.clear();
            this.text = Component.Serializer.fromJson(tag.getString("text"));
            this.maxWidth = tag.getInt("maxWidth");
            this.addElement(new TextGraphElement(text, this.color).setMaxWidth(this.maxWidth));
        }
        // We won't update this text since created
    }

}
