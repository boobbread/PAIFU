package mjolk.engine.core.lighting;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture;

public class ShadowCubeMap extends ShadowMap {
    private int cubeTexture;
    private int cubeFBO;

    public ShadowCubeMap(int width, int height) throws Exception {
        super(width, height);
        cubeTexture = glGenTextures();
        for (int i = 0; i < 6; i++) {
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL_DEPTH_COMPONENT,
                    width, height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        }
        cubeFBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, cubeFBO);
        glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, cubeTexture, 0);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getCubeTexture() { return cubeTexture; }
    public int getCubeFBO() { return cubeFBO; }
}
