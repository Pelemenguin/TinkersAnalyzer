package pelemenguin.tinkersanalyzer.client.graph.element;

import java.util.List;

import org.joml.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import pelemenguin.tinkersanalyzer.client.util.render.GeometryHelper;
import pelemenguin.tinkersanalyzer.client.util.render.QuadHelper;

public class RadarGraphElement extends AnalyzerGraphElement {

    protected int radius;
    protected int color;
    public static final float RATIO = 4;
    public final int radarRadius;
    private final int radarRadiusSquared;
    protected EntityType<?> trackingEntity;

    public static final int PASSIVE_COLOR = 0xFF00FF00;
    public static final int ENEMY_COLOR = 0xFFFF0000;
    public static final int NEUTRAL_COLOR = 0xFFFFFF00;
    public static final int TRACKING_COLOR = 0xFF00FFFF;

    public RadarGraphElement(int radius) {
        this.radius = radius;
        this.radarRadius = (int) RATIO * radius;
        this.radarRadiusSquared = radarRadius * radarRadius;
    }

    public RadarGraphElement color(int color) {
        this.color = color;
        return this;
    }

    @Override
    public void draw(GuiGraphics guiGraphics) {
        Matrix4f matrix = guiGraphics.pose().last().pose();
        this.drawData(matrix);
        GeometryHelper.drawCircle(this.radius, this.radius, this.radius, matrix, 0xFF000000 | this.color);
    }

    public void trackEntity(EntityType<?> type) {
        this.trackingEntity = type;
    }

    protected int getColorForEntity(Entity entity, Vec3 relativePos) {
        int result;
        if (entity.getType().equals(this.trackingEntity)) {
            result = TRACKING_COLOR;
        } else if (entity instanceof Enemy) {
            result = ENEMY_COLOR;
        } else if (entity instanceof NeutralMob) {
            result = NEUTRAL_COLOR;
        } else {
            result = PASSIVE_COLOR;
        }
        return processHeight(result, relativePos);
    }

    protected int processHeight(int color, Vec3 relativePos) {
        double height = relativePos.y;
        if (height > 0) {
            int value = (int) (height / this.radius * 255);
//            return (value << 24) | (color & 0x00FFFFFF);
            return (color & (((255 - value) << 24) | 0x00FFFFFF)) | (value << 16) | (value << 8) | value;
        } else {
            int value = 255 + (int) (height / this.radius * 255);
            if (value > 0) {
                int r = ((color >> 16) & 0xFF) * value / 255;
                int g = ((color >> 8) & 0xFF) * value / 255;
                int b = (color & 0xFF) * value / 255;
                return 0xFF000000 | (r << 16) | (g << 8) | (b);
            }
            return 0xFF000000;
        }
    }

    protected void drawData(Matrix4f matrix) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        List<Entity> entityList = player.level().getEntities(player, new AABB(
                player.getX() - radarRadius, player.getY() - radarRadius, player.getZ() - radarRadius,
                player.getX() + radarRadius, player.getY() + radarRadius, player.getZ() + radarRadius
            ), t -> t.distanceToSqr(player) <= radarRadiusSquared);

        float cosYaw = Mth.cos(Mth.DEG_TO_RAD * player.getYRot());
        float sinYaw = Mth.sin(Mth.DEG_TO_RAD * player.getYRot());

        QuadHelper.prepareDrawQuads();
        for (Entity entity : entityList) {
            if (!(entity instanceof LivingEntity)) continue;
            Vec3 pos = entity.position().subtract(player.position());
            Vec3 screenPos = new Vec3((-pos.x * cosYaw - pos.z * sinYaw) / RATIO, pos.y / RATIO, (pos.x * sinYaw - pos.z * cosYaw) / RATIO)
                    .add(this.radius, 0, this.radius);
            QuadHelper.drawSquare((float) screenPos.x(), (float) screenPos.z(), 1, matrix, getColorForEntity(entity, screenPos));
        }
        QuadHelper.finishDrawQuads();
    }

    @Override
    public int getWidth() {
        return this.radius * 2 + 1;
    }

    @Override
    public int getHeight() {
        return this.radius * 2 + 1;
    }

}
