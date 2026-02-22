package pelemenguin.tinkersanalyzer.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.joml.Intersectionf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = TinkersAnalyzer.MODID, value = Dist.CLIENT)
public class AnalyzerLayout {

    public static final AnalyzerLayout INSTANCE = new AnalyzerLayout();
    private HashMap<UUID, AnalyzerLayoutEntry> entries = new HashMap<>();
    protected static final HashMap<UUID, AnalyzerLayoutEntry> DEFAULT_LAYOUTS = new HashMap<>();
    private final HashMap<UUID, Matrix4f> cachedMatrices = new HashMap<>();

    private static final Logger LOGGER = LogUtils.getLogger();

    private boolean editingMode = false;
    private float initYaw = 0.0f;
    private float initPitch = 0.0f;

    private Matrix4f editingModeMatrixToOrigin = new Matrix4f();
    private Matrix4f editingModeMatrixToPreviousView = new Matrix4f();

    public AnalyzerLayout() {}

    public void load(Analyzer analyzer) {
        LocalPlayer player = Minecraft.getInstance().player;
        Set<UUID> keySet = analyzer.getAllGraphs().keySet();
        if (editingMode && player != null) {
            this.editingModeMatrixToOrigin = RotationHelper.transposedRotationMatrix(player.getYRot(), player.getXRot(), this.editingModeMatrixToOrigin);
        }
        for (UUID uuid : keySet) {
            Matrix4f layoutMatrix = this.getLayoutFor(uuid).getTransformationMatrix();
            this.cachedMatrices.put(uuid, this.cachedMatrices.getOrDefault(uuid, new Matrix4f()).set(layoutMatrix));
        }
    }

    public AnalyzerLayoutEntry getLayoutFor(UUID uuid) {
        return this.entries.getOrDefault(uuid, DEFAULT_LAYOUTS.getOrDefault(uuid, AnalyzerLayoutEntry.DEFAULT_LAYOUT));
    }

    public void transformGraph(PoseStack pose, UUID uuid, AnalyzerGraph graph) {
        pose.mulPoseMatrix(this.cachedMatrices.get(uuid));
        pose.translate(-0.5f * graph.getWidth(), -0.5f * graph.getHeight(), 0);
    }

    // ===== Editing Mode ===== //

    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        if (!INSTANCE.editingMode) return;
        event.setCanceled(true);

        ArrayList<UUID> foundGraphs = new ArrayList<>();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        float relativeYaw = player.getYRot() - INSTANCE.initYaw;
        float relativePitch = player.getXRot() - INSTANCE.initPitch;
        INSTANCE.findSelectedGraphs(foundGraphs, relativeYaw, relativePitch);

        LOGGER.debug(foundGraphs.toString());
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (TinkersAnalyzerButtons.SWITCH_LAYOUT_EDITING_MODE.consumeClick()) {
            if (INSTANCE.editingMode) {
                INSTANCE.initPitch = 0.0f;
                INSTANCE.initYaw = 0.0f;
                INSTANCE.editingMode = false;
            } else {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return;
                INSTANCE.initYaw = player.getYRot();
                INSTANCE.initPitch = player.getXRot();
                INSTANCE.editingMode = true;
                INSTANCE.editingModeMatrixToPreviousView = RotationHelper.rotationMatrix(
                        INSTANCE.initYaw,
                        INSTANCE.initPitch,
                        INSTANCE.editingModeMatrixToPreviousView
                    );
            }
        }
    }

    private Vector3f getGraphPosition(UUID uuid, float relativeX, float relativeY, Vector3f resultDest, Vector4f calcDest) {
        Matrix4f matrix = this.cachedMatrices.get(uuid);
        if (matrix == null) {
            return resultDest.set(relativeX, relativeY, 0);
        }
        calcDest.set(relativeX, relativeY, 0, 1);
        calcDest.mul(matrix);
        return resultDest.set(calcDest.x(), calcDest.y(), calcDest.z());
    }

    private static final Vector3f ZERO_VECTOR = new Vector3f(0, 0, 0);
    private void findSelectedGraphs(Collection<UUID> dest, float yaw, float pitch) {
        Vector3f curView = new Vector3f(0, 0, 1).rotateY(-yaw).rotateX(-pitch);

        for (Map.Entry<UUID, AnalyzerGraph> entry : AnalyzerOverlay.INSTANCE.graphs.entrySet()) {
            UUID uuid = entry.getKey();
            AnalyzerGraph graph = entry.getValue();
            Vector3f center = new Vector3f();
            Vector4f temp = new Vector4f();
            this.getGraphPosition(uuid, 0, 0, center, temp);

            float graphWidth = graph.getWidth();
            float graphHeight = graph.getHeight();
            Vector3f vertex1 = this.getGraphPosition(uuid, -0.5f * graphWidth, -0.5f * graphHeight, new Vector3f(), temp);
            Vector3f vertex2 = this.getGraphPosition(uuid, -0.5f * graphWidth, 0.5f * graphHeight, new Vector3f(), temp);
            Vector3f vertex3 = this.getGraphPosition(uuid, 0.5f * graphWidth, -0.5f * graphHeight, new Vector3f(), temp);

            float argument = checkCollide(vertex1, vertex2, vertex3, curView);
            if (argument <= 1.0f) continue;
            dest.add(uuid);
        }
    }

    private static float checkCollide(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f ray) {
        float result = Intersectionf.intersectRayTriangle(ZERO_VECTOR, ray, vertex1, vertex2, vertex3, 1e-9f);
        if (result >= 0.0f) return result;
        Vector3f vertex4 = new Vector3f(vertex2).sub(vertex1).add(vertex3);
        result = Intersectionf.intersectRayTriangle(ZERO_VECTOR, ray, vertex4, vertex2, vertex3, 1e-9f);
        return result;
    }

    public void pushTransformation(PoseStack pose) {
        if (editingMode) {
            pose.mulPoseMatrix(this.editingModeMatrixToOrigin);
            pose.mulPoseMatrix(this.editingModeMatrixToPreviousView);
        }
    }

}
