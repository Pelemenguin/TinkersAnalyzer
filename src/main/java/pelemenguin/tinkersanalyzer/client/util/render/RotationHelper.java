package pelemenguin.tinkersanalyzer.client.util.render;

import org.joml.Math;
import org.joml.Matrix4f;

/**
 * A helper class for calculate rotations.
 */
public final class RotationHelper {

    /**
     * Calculate a rotation matrix according to given yaw and pitch.
     * 
     * @param yawDegrees   The yaw in degrees. It rotates the XZ plane counterclockwise around the Y-axis.
     * @param pitchDegrees The pitch in degrees. It rotates the YZ plane clockwise around the X-axis 
     * @return The rotation matrix
     */
    public static Matrix4f rotationMatrix(float yawDegrees, float pitchDegrees) {
        return rotationMatrix(yawDegrees, pitchDegrees, new Matrix4f());
    }

    /**
     * Calculate a rotation matrix according to given yaw and pitch.
     * 
     * @param yawDegrees   The yaw in degrees. It rotates the XZ plane clockwise around the Y-axis.
     * @param pitchDegrees The pitch in degrees. It rotates the YZ plane counterclockwise around the X-axis
     * @param dest         The {@link Matrix4f} to store result matrix
     * @return The rotation matrix
     */
    public static Matrix4f rotationMatrix(float yawDegrees, float pitchDegrees, Matrix4f dest) {
        return dest.setRotationYXZ(Math.toRadians(-yawDegrees), Math.toRadians(pitchDegrees), 0);
    }

    public static Matrix4f transposedRotationMatrix(float yawDegrees, float pitchDegrees, Matrix4f dest) {
        return rotationMatrix(yawDegrees, pitchDegrees, dest).transpose();
    }

    private RotationHelper() {}

}
