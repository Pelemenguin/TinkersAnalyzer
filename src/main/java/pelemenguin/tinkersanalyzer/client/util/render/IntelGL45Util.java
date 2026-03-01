package pelemenguin.tinkersanalyzer.client.util.render;

import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

/**
 * Fix intel graphics driver bug
 */
public class IntelGL45Util {

    public static void glBlitNamedFramebuffer(
            int readFramebuffer,
            int drawFramebuffer,
            int srcX0,
            int srcY0,
            int srcX1,
            int srcY1,
            int dstX0,
            int dstY0,
            int dstX1,
            int dstY1,
            int mask,
            int filter
    ) {
        if (drawFramebuffer == 0) {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFramebuffer);
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, drawFramebuffer);
            GL30.glBlitFramebuffer(
                    srcX0,
                    srcY0,
                    srcX1,
                    srcY1,
                    dstX0,
                    dstY0,
                    dstX1,
                    dstY1,
                    mask,
                    filter
            );
        } else GL45.glBlitNamedFramebuffer(
                readFramebuffer,
                drawFramebuffer,
                srcX0,
                srcY0,
                srcX1,
                srcY1,
                dstX0,
                dstY0,
                dstX1,
                dstY1,
                mask,
                filter
        );
    }

}
