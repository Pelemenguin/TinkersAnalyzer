package pelemenguin.tinkersanalyzer.client.util.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author SpartanB312
 */
public class MSAAFrameBuffer {

    // properties
    private final int fbo;
    private final int colorRbo;
    // private final int depthRbo;
    private final int width;
    private final int height;
    private final int samples;

    // state cache
    private final AtomicBoolean started = new AtomicBoolean(false);
    private int preX = GlStateManager.Viewport.x();
    private int preY = GlStateManager.Viewport.y();
    private int preW = GlStateManager.Viewport.width();
    private int preH = GlStateManager.Viewport.height();
    private int readFbo = 0;

    public MSAAFrameBuffer(int width, int height, int samples) {
        this.width = width;
        this.height = height;
        this.samples = samples;
        // initialize framebuffer
        fbo = GL45.glCreateFramebuffers();
        // attachments
        colorRbo = GL45.glCreateRenderbuffers();
        // depthRbo = GL45.glCreateRenderbuffers();
        GL45.glNamedRenderbufferStorageMultisample(colorRbo, samples, GL45.GL_RGBA8, width, height);
        // GL45.glNamedRenderbufferStorageMultisample(depthRbo, samples, GL45.GL_DEPTH24_STENCIL8, width, height);
        GL45.glNamedFramebufferRenderbuffer(fbo, GL45.GL_COLOR_ATTACHMENT0, GL45.GL_RENDERBUFFER, colorRbo);
        // GL45.glNamedFramebufferRenderbuffer(fbo, GL45.GL_DEPTH_STENCIL_ATTACHMENT, GL45.GL_RENDERBUFFER, depthRbo);
    }

    public void startRendering() {
        RenderSystem.assertOnRenderThread();
        if (started.getAndSet(true)) throw new IllegalStateException("Already started rendering");
        GlStateManager._glBindFramebuffer(GL45.GL_FRAMEBUFFER, fbo);
        // GL45.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT | GL30.GL_STENCIL_BUFFER_BIT);
        readFbo = Minecraft.getInstance().getMainRenderTarget().frameBufferId;//GL45.glGetInteger(GL45.GL_FRAMEBUFFER_BINDING);
        IntelGL45Util.glBlitNamedFramebuffer(readFbo, fbo, 0, 0, width, height, 0, 0, width, height, GL45.GL_COLOR_BUFFER_BIT, GL45.GL_NEAREST);
        preX = GlStateManager.Viewport.x();
        preY = GlStateManager.Viewport.y();
        preW = GlStateManager.Viewport.width();
        preH = GlStateManager.Viewport.height();
        GlStateManager._viewport(0, 0, this.width, this.height);
    }

    public void endRendering() {
        RenderSystem.assertOnRenderThread();
        if (!started.getAndSet(false)) throw new IllegalStateException("Already ended rendering");
        IntelGL45Util.glBlitNamedFramebuffer(fbo, readFbo, 0, 0, width, height, 0, 0, width, height, GL45.GL_COLOR_BUFFER_BIT, GL45.GL_NEAREST);
        GlStateManager._viewport(preX, preY, preW, preH);
        GlStateManager._glBindFramebuffer(GL45.GL_FRAMEBUFFER, readFbo);
    }

    public void destroy() {
        RenderSystem.assertOnRenderThread();
        GL30.glDeleteFramebuffers(fbo);
        GL30.glDeleteRenderbuffers(colorRbo);
        // GL30.glDeleteRenderbuffers(depthRbo);
    }

    public int getColorRbo() {
        return colorRbo;
    }

    public int getFbo() {
        return fbo;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSamples() {
        return samples;
    }

}
