package pelemenguin.tinkersanalyzer.client;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.joml.Matrix4f;
import org.slf4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import pelemenguin.tinkersanalyzer.TinkersAnalyzer;
import pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph;
import pelemenguin.tinkersanalyzer.client.util.render.RotationHelper;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.AnalyzerLayoutEntry;
import slimeknights.mantle.client.ResourceColorManager;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = TinkersAnalyzer.MODID, value = Dist.CLIENT)
public class AnalyzerLayout {

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final AnalyzerLayout INSTANCE = new AnalyzerLayout();
    private HashMap<UUID, AnalyzerLayoutEntry> entries = new HashMap<>();
    protected static final HashMap<UUID, AnalyzerLayoutEntry> DEFAULT_LAYOUTS = new HashMap<>();
    private final HashMap<UUID, Matrix4f> cachedMatrices = new HashMap<>();
    private UUID[] availableGraphUUIDs = null;

    private EditingMode editingMode = EditingMode.OFF;
    private float initYaw = 0.0f;
    private float initPitch = 0.0f;
    private static enum EditingMode {
        OFF, ANGLE, DISTANCE
    }

    private Matrix4f editingModeMatrixToOrigin = new Matrix4f();
    private Matrix4f editingModeMatrixToPreviousView = new Matrix4f();

    public AnalyzerLayout() {}

    public void load(Analyzer analyzer) {
        LocalPlayer player = Minecraft.getInstance().player;
        Set<UUID> keySet = analyzer.getAllGraphs().keySet();
        if (editingMode != EditingMode.OFF && player != null) {
            this.editingModeMatrixToOrigin = RotationHelper.transposedRotationMatrix(player.getYRot(), player.getXRot(), this.editingModeMatrixToOrigin);
        }
        for (UUID uuid : keySet) {
            Matrix4f layoutMatrix = this.getLayoutFor(uuid).getTransformationMatrix();
            this.cachedMatrices.put(uuid, this.cachedMatrices.getOrDefault(uuid, new Matrix4f()).set(layoutMatrix));
        }
    }

    public void loadFromConfig() {
        AnalyzerLayoutConfig.loadConfig(entries);
    }

    public AnalyzerLayoutEntry getLayoutFor(UUID uuid) {
        AnalyzerLayoutEntry result = this.entries.get(uuid);
        if (result == null) {
            result = DEFAULT_LAYOUTS.get(uuid);
            if (result == null) {
                result = AnalyzerLayoutEntry.DEFAULT_LAYOUT.get();
                DEFAULT_LAYOUTS.put(uuid, result);
            }
            this.entries.put(uuid, result);
        }
        return result;
    }

    public void transformGraph(PoseStack pose, UUID uuid, AnalyzerGraph graph) {
        pose.mulPoseMatrix(this.cachedMatrices.get(uuid));
        pose.translate(-0.5f * graph.getWidth(), -0.5f * graph.getHeight(), 0);
    }

    // ===== Editing Mode ===== //

    private boolean enableEditingMode() {
        if (AnalyzerOverlay.INSTANCE.graphs.isEmpty()) return false;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;
        this.initYaw = player.getYRot();
        this.initPitch = player.getXRot();
        this.editingModeMatrixToPreviousView = RotationHelper.rotationMatrix(
                this.initYaw,
                this.initPitch,
                this.editingModeMatrixToPreviousView
            );
        this.availableGraphUUIDs = AnalyzerOverlay.INSTANCE.graphs.keySet().toArray(new UUID[AnalyzerOverlay.INSTANCE.graphs.size()]);
        this.selectedGraphIndex = 0;
        this.selectedGraph = this.availableGraphUUIDs[0];
        return true;
    }

    private void disableEditingMode() {
        this.initPitch = 0.0f;
        this.initYaw = 0.0f;
        this.availableGraphUUIDs = null;
        this.selectedGraph = null;

        AnalyzerLayoutConfig.saveConfig(this.entries);
    }

    public void cycleEditingMode() {
        switch (this.editingMode) {
            case OFF: {
                if (!this.enableEditingMode()) return;
                this.editingMode = EditingMode.ANGLE;
                break;
            }
            case ANGLE: {
                this.editingMode = EditingMode.DISTANCE;
                break;
            }
            case DISTANCE: {
                this.disableEditingMode();
                this.editingMode = EditingMode.OFF;
                break;
            }
        }
    }

    public void cycleEditingModeReversed() {
        switch (this.editingMode) {
            case OFF: {
                if (!this.enableEditingMode()) return;
                this.editingMode = EditingMode.DISTANCE;
                break;
            }
            case DISTANCE: {
                this.editingMode = EditingMode.ANGLE;
                break;
            }
            case ANGLE: {
                this.disableEditingMode();
                this.editingMode = EditingMode.OFF;
            }
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (INSTANCE.editingMode == EditingMode.OFF) return;
        event.setCanceled(true);

        switch (INSTANCE.editingMode) {
            case ANGLE: {
                INSTANCE.handleAngleEdit((float) event.getScrollDelta());
                break;
            }
            case DISTANCE: {
                INSTANCE.handleDistanceEdit((float) event.getScrollDelta());
                break;
            }
            default: {
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (TinkersAnalyzerButtons.SWITCH_LAYOUT_EDITING_MODE.consumeClick()) {
            if (Minecraft.getInstance().options.keyShift.isDown()) {
                INSTANCE.cycleEditingModeReversed();
            } else {
                INSTANCE.cycleEditingMode();
            }
        }
        if (INSTANCE.editingMode != EditingMode.OFF && Minecraft.getInstance().options.keyPlayerList.consumeClick()) {
            if (Minecraft.getInstance().options.keyShift.isDown()) {
                INSTANCE.cycleSelectionReversed();
            } else {
                INSTANCE.cycleSelection();
            }
        }
    }

    private int selectedGraphIndex = 0;
    @Nullable
    private UUID selectedGraph = null;

    public void cycleSelection() {
        this.selectedGraphIndex ++;
        this.selectedGraphIndex %= this.availableGraphUUIDs.length;
        this.selectedGraph = this.availableGraphUUIDs[this.selectedGraphIndex];
    }

    public void cycleSelectionReversed() {
        this.selectedGraphIndex --;
        if (this.selectedGraphIndex < 0) this.selectedGraphIndex = this.availableGraphUUIDs.length - 1;
        this.selectedGraph = this.availableGraphUUIDs[this.selectedGraphIndex];
    }

    private static final float ANGLE_EDITING_SCROLL_MULTIPLIER = 5.0f;
    private void handleAngleEdit(float scrollDelta) {
        if (Minecraft.getInstance().options.keyShift.isDown()) {
            this.getLayoutFor(this.selectedGraph).addYaw(-ANGLE_EDITING_SCROLL_MULTIPLIER * scrollDelta);
        } else {
            this.getLayoutFor(this.selectedGraph).addPitch(-ANGLE_EDITING_SCROLL_MULTIPLIER * scrollDelta);
        }
    }
    private static final float DISTANCE_EDITING_SCROLL_MULTIPLIER = 4.0f;
    private void handleDistanceEdit(float scrollDelta) {
        this.getLayoutFor(this.selectedGraph).addR(DISTANCE_EDITING_SCROLL_MULTIPLIER * scrollDelta);
    }

    public boolean isSelected(UUID uuid) {
        return uuid.equals(this.selectedGraph);
    }

    public void pushTransformation(PoseStack pose) {
        if (this.editingMode != EditingMode.OFF) {
            pose.mulPoseMatrix(this.editingModeMatrixToOrigin);
            pose.mulPoseMatrix(this.editingModeMatrixToPreviousView);
        }
    }

    private static final String EDITING_MODE = TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode");
    private static final Component EDITING_MODE_TITLE = Component.translatable(TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.title"));
    private static final Component EDITING_MODE_DESCRIPTION = Component.translatable(TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.description"),
            Component.keybind("key.tinkers_analyzer.toggle_layout_editing_mode"),
            Component.keybind("key.playerlist")
        );
    private static final String EDITING_MODE_ANGLE = TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.angle");
    private static final Component EDITING_MODE_ANGLE_TITLE = Component.translatable(TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.angle.title"));
    private static final Component EDITING_MODE_ANGLE_DESCRIPTION = Component.translatable(TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.angle.description"));
    private static final String EDITING_MODE_DISTANCE = TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.distance");
    private static final Component EDITING_MODE_DISTANCE_TITLE = Component.translatable(TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.distance.title"));
    private static final Component EDITING_MODE_DISTANCE_DESCRIPTION = Component.translatable(TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.distance.description"));
    private static final String EDITING_MODE_OFF = TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.off");
    private static final Component EDITING_MODE_OFF_TITLE = Component.translatable(TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.off.title"));
    private static final Component EDITING_MODE_OFF_DESCRIPTION = Component.translatable(TinkersAnalyzer.makeAnalyzerTranslationKey("editing_mode.off.description"));
    private float editingModeEnableProgress = 0.0f;
    public void drawEditingModeOverlay(GuiGraphics guiGraphics, int width, int height) {
        if (this.editingMode == EditingMode.OFF) {
            editingModeEnableProgress -= 0.1f * Minecraft.getInstance().getDeltaFrameTime();
            if (this.editingModeEnableProgress < 0) this.editingModeEnableProgress = 0.0f;
        } else {
            editingModeEnableProgress += 0.1f * Minecraft.getInstance().getDeltaFrameTime();
            if (this.editingModeEnableProgress > 1.0f) {
                this.editingModeEnableProgress = 1.0f;
            }
        }

        if (this.editingModeEnableProgress > 0.0f) {
            Font font = Minecraft.getInstance().font;

            int alpha = Mth.ceil(255 * this.editingModeEnableProgress) << 24;
            int editingModeColor = alpha | ResourceColorManager.getColor(EDITING_MODE);
            @SuppressWarnings("null") // ChatFormatting.GRAY always has color
            int textColor = alpha | ChatFormatting.WHITE.getColor().intValue();

            float displacement = font.lineHeight * (3 * this.editingModeEnableProgress - 2);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, displacement, 0);
            guiGraphics.drawString(font, EDITING_MODE_TITLE, (width - font.width(EDITING_MODE_TITLE)) / 2, 0, editingModeColor);
            guiGraphics.drawString(font, EDITING_MODE_DESCRIPTION, (width - font.width(EDITING_MODE_DESCRIPTION)) / 2, (int) font.lineHeight, textColor);
            guiGraphics.pose().popPose();

            displacement = height / 2 + font.lineHeight * (-3 * this.editingModeEnableProgress + 5);
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, displacement, 0);
            Component title;
            Component desc;
            int modeColor;
            if (this.editingMode == EditingMode.ANGLE) {
                title = EDITING_MODE_ANGLE_TITLE;
                desc = EDITING_MODE_ANGLE_DESCRIPTION;
                modeColor = alpha | ResourceColorManager.getColor(EDITING_MODE_ANGLE);
            } else if (this.editingMode == EditingMode.DISTANCE) {
                title = EDITING_MODE_DISTANCE_TITLE;
                desc = EDITING_MODE_DISTANCE_DESCRIPTION;
                modeColor = alpha | ResourceColorManager.getColor(EDITING_MODE_DISTANCE);
            } else if (this.editingMode == EditingMode.OFF) {
                title = EDITING_MODE_OFF_TITLE;
                desc = EDITING_MODE_OFF_DESCRIPTION;
                modeColor = alpha | ResourceColorManager.getColor(EDITING_MODE_OFF);
            } else {
                title = Component.empty();
                desc = Component.empty();
                modeColor = 0;
            }

            guiGraphics.drawString(font, title, (width - font.width(title)) / 2, 0, modeColor);
            guiGraphics.drawString(font, desc, (width - font.width(desc)) / 2, font.lineHeight, textColor);
            guiGraphics.pose().popPose();
        }
    }

}
