package pelemenguin.tinkersanalyzer.content.client;

import pelemenguin.tinkersanalyzer.client.AnalyzerOverlay;
import pelemenguin.tinkersanalyzer.client.graph.DPSGraph;
import pelemenguin.tinkersanalyzer.client.graph.EntityRadarGraph;
import pelemenguin.tinkersanalyzer.client.graph.FluidGaugeGraph;
import pelemenguin.tinkersanalyzer.client.graph.PlainTextGraph;
import pelemenguin.tinkersanalyzer.content.TinkersAnalyzerGraphs;

public final class TinkersAnalyzerBuiltinGraphs {

    public static void init() {
        AnalyzerOverlay.registerGraph(TinkersAnalyzerGraphs.PLAIN_TEXT, PlainTextGraph::new);
        AnalyzerOverlay.registerGraph(TinkersAnalyzerGraphs.DPS, (tag) -> DPSGraph.getInstance());
        AnalyzerOverlay.registerGraph(TinkersAnalyzerGraphs.FLUID_GAUGE, FluidGaugeGraph::new);
        AnalyzerOverlay.registerGraph(TinkersAnalyzerGraphs.ENTITY_RADAR, (tag) -> EntityRadarGraph.getInstance());
    }

}
