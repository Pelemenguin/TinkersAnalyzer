package pelemenguin.tinkersanalyzer.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;

@Mod.EventBusSubscriber(bus = Bus.MOD, modid = TinkersAnalyzer.MODID, value = Dist.CLIENT)
public final class TinkersAnalyzerButtons {

    private static final String CATEGORY = TinkersAnalyzer.makeTranslationKey("key.categories", "tinkers_analyzer");

    public static final KeyMapping SWITCH_LAYOUT_EDITING_MODE = new KeyMapping(
            TinkersAnalyzer.makeTranslationKey("key", "switch_layout_editing_mode"),
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_INSERT,
            CATEGORY
        );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SWITCH_LAYOUT_EDITING_MODE);
    }

    private TinkersAnalyzerButtons() {}

}
