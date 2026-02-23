package pelemenguin.tinkersanalyzer.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.library.AnalyzerLayoutEntry;

@Mod.EventBusSubscriber(modid = TinkersAnalyzer.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class AnalyzerLayoutConfig {

    public static final Logger LOGGER = LogUtils.getLogger();

    private static ForgeConfigSpec SPEC;
    private static ForgeConfigSpec.ConfigValue<List<? extends String>> LAYOUTS;

    public static final String LAYOUT_CONFIG_PATH = "layoutConfig";
    public static final String LAYOUT_LIST_PATH = "layoutEntries";

    public static void init() {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("layoutConfig");

        LAYOUTS = builder.comment(
                "Config the Analyzer graphs' layout",
                "Format: <uuid>:<yaw>:<pitch>:<distance>"
            ).defineListAllowEmpty("layouts", List.of(), (element) -> {
                if (element instanceof String s) {
                    String[] parts = s.split(":");
                    if (parts.length != 4) return false;
                    try {
                        UUID.fromString(parts[0]);
                        Float.parseFloat(parts[1]);
                        Float.parseFloat(parts[2]);
                        Float.parseFloat(parts[3]);
                        return true;
                    } catch (Throwable e) {
                        return false;
                    }
                }
                return false;
            });

        builder.pop();

        SPEC = builder.build();
    }

    public static ForgeConfigSpec spec() {
        return SPEC;
    }

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> layouts() {
        return LAYOUTS;
    }

    public static void loadConfig(HashMap<UUID, AnalyzerLayoutEntry> entries) {
        for (String s : LAYOUTS.get()) {
            try {
                parseConfigEntry(entries, s);
            } catch (Throwable e) {
                LOGGER.error("Error loading layout config entry: " + s, e);
            }
        }
    }

    private static AnalyzerLayoutEntry parseConfigEntry(HashMap<UUID, AnalyzerLayoutEntry> entries, String s) throws IndexOutOfBoundsException, IllegalArgumentException {
        String[] parts = s.split(":");
        UUID uuid = UUID.fromString(parts[0]);
        float yaw = Float.parseFloat(parts[1]);
        float pitch = Float.parseFloat(parts[2]);
        float r = Float.parseFloat(parts[3]);

        AnalyzerLayoutEntry result = new AnalyzerLayoutEntry(yaw, pitch, r);
        entries.put(uuid, result);
        return result;
    }

    public static void saveConfig(HashMap<UUID, AnalyzerLayoutEntry> entries) {
        ArrayList<String> list = new ArrayList<>();

        for (var entry : entries.entrySet()) {
            AnalyzerLayoutEntry layoutEntry = entry.getValue();
            list.add("%s:%s:%s:%s".formatted(
                    entry.getKey().toString(), layoutEntry.yaw(), layoutEntry.pitch(), layoutEntry.r()
                ));
        }

        LAYOUTS.set(List.copyOf(list));
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        AnalyzerLayout.INSTANCE.loadFromConfig();
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        AnalyzerLayout.INSTANCE.loadFromConfig();
    }

}
