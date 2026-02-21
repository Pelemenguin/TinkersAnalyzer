package pelemenguin.tinkersanalyzer.library;

import org.joml.Matrix4f;

import pelemenguin.tinkersanalyzer.client.util.render.RotationHelper;

public class AnalyzerLayoutEntry {
    /**
     * Horizontal rotation angle.
     * When it aligns with the player's view, the angle is 0 degrees.
     * Rightward (or clockwise when viewed from above) is the positive rotation direction.
     */
    private final float yaw;
    /**
     * Vertical rotation angle.  
     * It is 0 degrees when aligned with the player's line of sight.
     * Downward is the positive rotation direction.
     */
    private final float pitch;
    /**
     * Distance to the camera entity.
     */
    private final float r;

    /**
     * The default {@link AnalyzerLayoutEntry}.
     */
    public static final AnalyzerLayoutEntry DEFAULT_LAYOUT = new AnalyzerLayoutEntry(0.0f, 0.0f, 192.0f);

    private Matrix4f transformationMatrix = null;

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

    /**
     * Get the transformation matrix that transforms the graph to the corresponding position of this layout
     * @return The transformation matrix
     */
    public Matrix4f getTransformationMatrix() {
        if (transformationMatrix != null) return this.transformationMatrix;
        Matrix4f result = RotationHelper.rotationMatrix(this.yaw, this.pitch)
                .translate(0, 0, -this.r());
        this.transformationMatrix = result;
        return this.transformationMatrix;
    }

}
