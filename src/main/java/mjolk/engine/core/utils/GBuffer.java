package mjolk.engine.core.utils;

import mjolk.engine.core.entity.Texture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL30.*;

public class GBuffer {

    public enum GBUFFER_TEXTURE_TYPE {
        GBUFFER_TEXTURE_TYPE_POSITION,
        GBUFFER_TEXTURE_TYPE_DIFFUSE,
        GBUFFER_TEXTURE_TYPE_NORMAL,
        GBUFFER_TEXTURE_TYPE_TEXCOORD,
        GBUFFER_NUM_TEXTURES
    }

    private int fbo;
    private int[] textures = new int[4];
    private int depthTexture;

    public GBuffer (int windowWidth, int windowHeight) {
        fbo = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fbo);

        // Create the gbuffer textures
        GL11.glGenTextures(textures); // LWJGL can fill an int[] directly

        for (int i = 0; i < textures.length; i++) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures[i]);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB32F, windowWidth, windowHeight,
                    0, GL11.GL_RGB, GL11.GL_FLOAT, 0L);
            GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,
                    GL30.GL_COLOR_ATTACHMENT0 + i,
                    GL11.GL_TEXTURE_2D,
                    textures[i],
                    0);
        }

        // Depth texture
        depthTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT32F,
                windowWidth, windowHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, 0L);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
                GL11.GL_TEXTURE_2D, depthTexture, 0);

        // Set draw buffers
        int[] drawBuffers = {
                GL30.GL_COLOR_ATTACHMENT0,
                GL30.GL_COLOR_ATTACHMENT1,
                GL30.GL_COLOR_ATTACHMENT2,
                GL30.GL_COLOR_ATTACHMENT3
        };
        GL20.glDrawBuffers(drawBuffers);

        // Check framebuffer status
        int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            System.err.printf("FB error, status: 0x%x\n", status);
        }

        // Restore default FBO
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
    }

    public void bindForWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
    }

    public void bindForReading() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
    }

    public void setReadBuffer(GBUFFER_TEXTURE_TYPE textureType) {
        glReadBuffer(GL_COLOR_ATTACHMENT0 + textureType.ordinal());
    }

    public void cleanup() {
        for (int i : textures) {
            glDeleteTextures(i);
        }
    }
}
