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

    private static final HashMap<ResourceLocation, IAnalyzerGraphCreator> ALL_GRAPHS = new HashMap<>();

    private Map<UUID, AnalyzerGraph> graphs = new HashMap<>();

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        ItemStack mainHandItemStack = player.getMainHandItem();
        Item mainHandItem = mainHandItemStack.getItem();
        Analyzer analyzer = new Analyzer();
        if (mainHandItem instanceof IModifiable) {
            ToolStack toolStack = ToolStack.from(mainHandItemStack);
            for (ModifierEntry entry : toolStack.getModifierList()) {
                DisplayAnalyzerGraphModifierHook hook = entry.getHook(DisplayAnalyzerGraphModifierHook.INSTANCE);
                if (hook != null) {
                    hook.addGraph(toolStack, entry, analyzer);
                }
            }
        }

        if (analyzer.isEmpty()) return;
        this.loadAnalyzer(analyzer);

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.setIdentity();

        Matrix4f old = RenderSystem.getProjectionMatrix();
        Window window = Minecraft.getInstance().getWindow();
        Matrix4f matrix = new Matrix4f().setPerspective((float) Math.toRadians(70.0f), (float) window.getWidth() / window.getHeight(), 0.05f, 11000.0f);

        RenderSystem.setProjectionMatrix(matrix, VertexSorting.DISTANCE_TO_ORIGIN);

        PoseStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushPose();
        modelViewStack.setIdentity();
        modelViewStack.translate(0, 0, -5.0f);
        modelViewStack.scale(0.03125f, -0.03125f, 0.03125f);
        RenderSystem.applyModelViewMatrix();

        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        pose.pushPose();
        for (AnalyzerGraph graph : this.graphs.values()) {
            graph.render(guiGraphics);
        }
        pose.popPose();

        RenderSystem.setProjectionMatrix(old, VertexSorting.ORTHOGRAPHIC_Z);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        pose.popPose();

        modelViewStack.popPose();
        RenderSystem.applyModelViewMatrix();
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
        }
    }

    public AnalyzerOverlay() {
    }

    public static void registerGraph(ResourceLocation id, IAnalyzerGraphCreator graph) {
        IAnalyzerGraphCreator original = ALL_GRAPHS.putIfAbsent(id, graph);
        if (original != null) {
            throw new IllegalArgumentException("Graph for ID " + id + " already registered!");
        }
    }

}
