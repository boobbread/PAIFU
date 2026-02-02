package mjolk.engine.graphics.lighting.shadow;

import mjolk.engine.graphics.material.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ShadowMap {

    public int SHADOW_MAP_WIDTH = 4096;

    public int SHADOW_MAP_HEIGHT = 4096;

    private final int depthMapFBO;

    private final Texture depthMap;

    public ShadowMap(int width, int height) throws Exception {
        this.SHADOW_MAP_WIDTH = width;
        this.SHADOW_MAP_HEIGHT = height;

        // Create a FBO to render the depth map
        depthMapFBO = glGenFramebuffers();

        // Create the depth map texture
        depthMap = new Texture(SHADOW_MAP_WIDTH, SHADOW_MAP_HEIGHT, GL_DEPTH_COMPONENT24);
        glBindTexture(GL_TEXTURE_2D, depthMap.getId());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Attach the depth map texture to the FBO
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap.getId(), 0);
        // Set only depth
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new Exception("Could not create FrameBuffer");
        }

        // Unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Texture getDepthTexture() {
        return depthMap;
    }

    public int getDepthMapFBO() {
        return depthMapFBO;
    }


    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getWidth() {
        return SHADOW_MAP_WIDTH;
    }

    public int getHeight() {
        return SHADOW_MAP_HEIGHT;
    }

    public void cleanup() {
        glDeleteFramebuffers(depthMapFBO);
        depthMap.cleanup();
    }
}
