package pelemenguin.tinkersanalyzer.client.util.render;

import org.joml.Matrix4f;

public final class GeometryHelper {

    public static void drawCircle(int centerX, int centerY, int radius, Matrix4f matrix, int color) {
        QuadHelper.prepareDrawQuads();
        // Bresenham
        int x = 0;
        int y = radius;
        drawPointOnCircle(x, y, radius, matrix, color, centerX, centerY);
        int p = 3 - 2 * radius;
        while (x < y) {
            if (p < 0) {
                // Draw at (x+1, y)
                p = p + 4 * x + 6;
                x += 1;
            } else {
                p = p + 4 * (x - y) + 10;
                x += 1;
                y -= 1;
            }
            drawPointOnCircle(x, y, radius, matrix, color, centerX, centerY);
        }
        QuadHelper.finishDrawQuads();
    }

    private static void drawPointOnCircle(int x, int y, int r, Matrix4f matrix, int color, int dx, int dy) {
        QuadHelper.drawSquare(dx + x, dy + y, 1, matrix, color);
        QuadHelper.drawSquare(dx + x, dy - y, 1, matrix, color);
        QuadHelper.drawSquare(dx - x, dy + y, 1, matrix, color);
        QuadHelper.drawSquare(dx - x, dy - y, 1, matrix, color);
        QuadHelper.drawSquare(dx + y, dy + x, 1, matrix, color);
        QuadHelper.drawSquare(dx + y, dy - x, 1, matrix, color);
        QuadHelper.drawSquare(dx - y, dy + x, 1, matrix, color);
        QuadHelper.drawSquare(dx - y, dy - x, 1, matrix, color);
    }

    private GeometryHelper() {}

}
