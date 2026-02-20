package pelemenguin.tinkersanalyzer.client;

import java.util.HashMap;
import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph;
import pelemenguin.tinkersanalyzer.library.AnalyzerLayoutEntry;

public class AnalyzerLayout {

    public static final AnalyzerLayout INSTANCE = new AnalyzerLayout();
    private HashMap<UUID, AnalyzerLayoutEntry> entries = new HashMap<>();

    public AnalyzerLayout() {}

    private AnalyzerLayoutEntry getLayoutFor(UUID uuid, AnalyzerLayoutEntry defaultLayout) {
        this.entries.putIfAbsent(uuid, defaultLayout);
        AnalyzerLayoutEntry result = this.entries.get(uuid);
        return result;
    }

    public void transformGraph(PoseStack pose, UUID uuid, AnalyzerGraph graph, AnalyzerLayoutEntry defaultLayout) {
        AnalyzerLayoutEntry entry = this.getLayoutFor(uuid, defaultLayout);
        pose.translate(-0.5f * graph.getWidth(), -0.5f * graph.getHeight(), 0);
        pose.mulPose(Axis.YP.rotationDegrees(entry.yaw()));
        pose.mulPose(Axis.XN.rotationDegrees(entry.pitch()));
        pose.translate(0.0f, 0.0f, -entry.r());
    }

}
