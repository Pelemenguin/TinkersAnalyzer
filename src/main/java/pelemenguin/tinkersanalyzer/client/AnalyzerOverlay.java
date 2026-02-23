package pelemenguin.tinkersanalyzer.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.joml.Matrix4f;
import org.slf4j.Logger;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import pelemenguin.tinkersanalyzer.client.graph.AnalyzerGraph;
import pelemenguin.tinkersanalyzer.client.graph.IAnalyzerGraphCreator;
import pelemenguin.tinkersanalyzer.library.Analyzer;
import pelemenguin.tinkersanalyzer.library.hook.DisplayAnalyzerGraphModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

public class AnalyzerOverlay implements IGuiOverlay {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final AnalyzerOverlay INSTANCE = new AnalyzerOverlay();

    private static final HashMap<ResourceLocation, IAnalyzerGraphCreator> ALL_GRAPHS = new HashMap<>();
    private boolean needUpdate = true;

    protected Map<UUID, AnalyzerGraph> graphs = new HashMap<>();

    public static void registerGraph(ResourceLocation id, IAnalyzerGraphCreator graph) {
        IAnalyzerGraphCreator original = ALL_GRAPHS.putIfAbsent(id, graph);
        if (original != null) {
            throw new IllegalArgumentException("Graph for ID " + id + " already registered!");
        }
    }

    private final Analyzer analyzer = new Analyzer();
    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (Minecraft.getInstance().options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        if (this.needUpdate) {
            ItemStack mainHandItemStack = player.getMainHandItem();
            Item mainHandItem = mainHandItemStack.getItem();
            this.analyzer.clear();
            if (mainHandItem instanceof IModifiable) {
                ToolStack toolStack = ToolStack.from(mainHandItemStack);
                for (ModifierEntry entry : toolStack.getModifierList()) {
                    DisplayAnalyzerGraphModifierHook hook = entry.getHook(DisplayAnalyzerGraphModifierHook.INSTANCE);
                    if (hook != null) {
                        hook.addGraph(toolStack, entry, this.analyzer);
                    }
                }
            }

            if (this.analyzer.isEmpty()) {
                this.graphs.clear();
                AnalyzerLayout.INSTANCE.forceDisableEditingMode();
            } else {
                this.loadAnalyzer(this.analyzer);
            }
            this.needUpdate = false;
        }
        AnalyzerLayout.INSTANCE.load(this.analyzer);

        if (!this.analyzer.isEmpty()) {
            PoseStack pose = guiGraphics.pose();
            pose.pushPose();
            pose.setIdentity();

            Matrix4f old = RenderSystem.getProjectionMatrix();
            Window window = Minecraft.getInstance().getWindow();
            Matrix4f matrix = new Matrix4f().setPerspective((float) Math.toRadians(Minecraft.getInstance().options.fov().get()), (float) window.getWidth() / window.getHeight(), 0.05f, 11000.0f);

            RenderSystem.setProjectionMatrix(matrix, VertexSorting.DISTANCE_TO_ORIGIN);

            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushPose();
            modelViewStack.setIdentity();
            modelViewStack.scale(0.03125f, -0.03125f, 0.03125f);
            RenderSystem.applyModelViewMatrix();

            AnalyzerLayout.INSTANCE.pushTransformation(pose);

            for (var graphEntry : this.graphs.entrySet()) {
                UUID graphKey = graphEntry.getKey();
                AnalyzerGraph graph = graphEntry.getValue();
                pose.pushPose();
                AnalyzerLayout.INSTANCE.transformGraph(pose, graphKey, graph);
                graph.render(guiGraphics, AnalyzerLayout.INSTANCE.isSelected(graphKey));
                pose.popPose();
            }

            RenderSystem.setProjectionMatrix(old, VertexSorting.ORTHOGRAPHIC_Z);
            pose.popPose();

            modelViewStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        AnalyzerLayout.INSTANCE.drawEditingModeOverlay(guiGraphics, screenWidth, screenHeight);
    }

    public void loadAnalyzer(Analyzer analyzer) {
        this.graphs.clear();
        Map<UUID, Pair<ResourceLocation, CompoundTag>> loaded = analyzer.getAllGraphs();
        for (UUID uuid : loaded.keySet()) {
            Pair<ResourceLocation, CompoundTag> pair = loaded.get(uuid);
            if (this.graphs.containsKey(uuid)) {
                this.graphs.get(uuid).load(pair.getSecond());
            } else {
                if (!ALL_GRAPHS.containsKey(pair.getFirst())) {
                    LOGGER.error("Graph for ID " + pair.getFirst() + " does not exist!");
                    continue;
                }
                try {
                    this.graphs.put(uuid, ALL_GRAPHS.get(pair.getFirst()).createGraph(pair.getSecond()));
                } catch (Exception e) {
                    LOGGER.error("Error initializing graph " + pair.getFirst() + " with data " + pair.getSecond());
                }
            }
            AnalyzerLayout.DEFAULT_LAYOUTS.putIfAbsent(uuid, analyzer.getDefaultLayoutFor(uuid));
        }
    }

    public void needUpdate() {
        this.needUpdate = true;
    }

    private AnalyzerOverlay() {}

}
