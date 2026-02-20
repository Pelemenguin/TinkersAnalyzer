package pelemenguin.tinkersanalyzer.library;

public record AnalyzerLayoutEntry(
        /**
         * Horizontal rotation angle.
         * When it aligns with the player's view, the angle is 0 degrees.
         * Leftward (or counterclockwise when viewed from above) is the positive rotation direction.
         */
        float yaw,
        /**
         * Vertical rotation angle.  
         * It is 0 degrees when aligned with the player's line of sight.
         * Downward is the positive rotation direction.
         */
        float pitch,
        /**
         * Distance to the camera entity.
         */
        float r
    ) {

}
