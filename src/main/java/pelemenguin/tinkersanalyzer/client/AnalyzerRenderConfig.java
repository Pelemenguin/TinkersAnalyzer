package pelemenguin.tinkersanalyzer.client;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class AnalyzerRenderConfig {

    private static ForgeConfigSpec.BooleanValue ENABLE_AA;
    private static ForgeConfigSpec.IntValue TARGET_MSAA_SAMPLES;
    private static boolean needUpdate = false;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("renderConfig");

        ENABLE_AA = builder.comment(
                "Enables antialias"
            ).define("enableAA", true);

        TARGET_MSAA_SAMPLES = builder.comment(
                "Determins the target MSAA samples",
                "It should be 2, 4, 8, 16, etc."
            ).defineInRange("targetMSAASamples", 8, 1, 2147483647);

        builder.pop();
    }

    public static boolean isAAEnabled() {
        return ENABLE_AA.get();
    }

    public static int getTargetMSAASamples() {
        return TARGET_MSAA_SAMPLES.get();
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        needUpdate = true;
    }

    public static void onConfigReload(ModConfigEvent.Reloading event) {
        needUpdate = true;
    }

    public static void checkUpdate() {
        if (needUpdate) {
            AnalyzerOverlay.ENABLE_AA = isAAEnabled();
            AnalyzerOverlay.TARGET_MSAA_SAMPLES = getTargetMSAASamples();
        }
    }

}
