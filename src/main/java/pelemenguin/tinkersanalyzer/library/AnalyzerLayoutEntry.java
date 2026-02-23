package pelemenguin.tinkersanalyzer.library;

import java.util.function.Supplier;

import org.joml.Matrix4f;

import pelemenguin.tinkersanalyzer.client.util.render.RotationHelper;

public class AnalyzerLayoutEntry {

    /**
     * Horizontal rotation angle.
     * When it aligns with the player's view, the angle is 0 degrees.
     * Rightward (or clockwise when viewed from above) is the positive rotation direction.
     */
    private float yaw;
    /**
     * Vertical rotation angle.  
     * It is 0 degrees when aligned with the player's line of sight.
     * Downward is the positive rotation direction.
     */
    private float pitch;
    /**
     * Distance to the camera entity.
     */
    private float r;

    /**
     * The default {@link AnalyzerLayoutEntry}.
     */
    public static final Supplier<AnalyzerLayoutEntry> DEFAULT_LAYOUT = () -> new AnalyzerLayoutEntry(0.0f, 0.0f, 192.0f);

    private boolean matrixNeedsUpdate = true;
    private Matrix4f transformationMatrix = new Matrix4f();

    public static final float MIN_DISTANCE = 16.0f;
    public static final float MAX_DISTANCE = 512.0f;

    public AnalyzerLayoutEntry(float yaw, float pitch, float r) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.r = r;
    }

    public float yaw() {
        return this.yaw;
    }

    public float pitch() {
        return this.pitch;
    }

    public float r() {
        return this.r;
    }

    public void addYaw(float yaw) {
        this.yaw += yaw;
        this.yaw = ((this.yaw + 180) % 360) - 180;
        this.matrixNeedsUpdate = true;
    }

    public void addPitch(float pitch) {
        this.pitch += pitch;
        this.pitch = ((this.pitch + 180) % 360) - 180;
        this.matrixNeedsUpdate = true;
    }

    public void addR(float r) {
        this.r += r;
        if (this.r < MIN_DISTANCE) {
            this.r = MIN_DISTANCE;
        } else if (this.r > MAX_DISTANCE) {
            this.r = MAX_DISTANCE;
        }
        this.matrixNeedsUpdate = true;
    }

    /**
     * Get the transformation matrix that transforms the graph to the corresponding position of this layout
     * @return The transformation matrix
     */
    public Matrix4f getTransformationMatrix() {
        if (!this.matrixNeedsUpdate) return this.transformationMatrix;
        RotationHelper.rotationMatrix(this.yaw, this.pitch)
                .translate(0, 0, -this.r(), this.transformationMatrix);
        this.matrixNeedsUpdate = false;
        return this.transformationMatrix;
    }

}
