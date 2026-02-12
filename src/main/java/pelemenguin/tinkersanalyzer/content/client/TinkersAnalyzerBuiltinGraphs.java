package pelemenguin.tinkersanalyzer.content.client;

import pelemenguin.tinkersanalyzer.client.AnalyzerOverlay;
import pelemenguin.tinkersanalyzer.client.graph.PlainTextGraph;
import pelemenguin.tinkersanalyzer.library.TinkersAnalyzerGraphs;

public final class TinkersAnalyzerBuiltinGraphs {

    public static void init() {
        AnalyzerOverlay.registerGraph(TinkersAnalyzerGraphs.PLAIN_TEXT, PlainTextGraph::new);
    }

}
