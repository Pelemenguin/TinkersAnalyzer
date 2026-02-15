package pelemenguin.tinkersanalyzer.content.network;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.content.modifier.DPSAnalyzerModifier;

public class TinkersAnalyzerNetwork {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            TinkersAnalyzer.makeResource("main"), 
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
        );

    public static void init() {
        INSTANCE.registerMessage(
                0, 
                DPSAnalyzerModifier.DPSAnalyzerReceiveDamageMessage.class,
                DPSAnalyzerModifier.DPSAnalyzerReceiveDamageMessage::encode,
                DPSAnalyzerModifier.DPSAnalyzerReceiveDamageMessage::new,
                DPSAnalyzerModifier.DPSAnalyzerReceiveDamageMessage::handle
            );
    }

}
