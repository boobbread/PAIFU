package mjolk.engine.graphics.rendering;

import mjolk.engine.graphics.material.Texture;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.opengl.GL30.*;

public class GBuffer {

    // needs a position, diffuse, normal and specular texture
    private Texture positionTexture;
    private Texture normalTexture;
    private Texture diffuseSpecTexture;

    private int fbo;
    private int depthRBO;

    private int width, height;

    public GBuffer(int width, int height) throws Exception {
        System.out.println("GBuffer constructor called");
        this.width = width;
        this.height = height;
    }

    public void init() throws Exception {
        System.out.println("GBuffer init called");
        fbo = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        positionTexture = new Texture(width, height, GL30.GL_RGB16F, GL30.GL_RGB, GL30.GL_FLOAT);
        GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, positionTexture.getId(), 0);

        normalTexture = new Texture(width, height, GL30.GL_RGB16F, GL30.GL_RGB, GL30.GL_FLOAT);
        GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, normalTexture.getId(), 0);

        diffuseSpecTexture = new Texture(width, height, GL30.GL_RGBA16F, GL30.GL_RGBA, GL30.GL_FLOAT);
        GL30.glFramebufferTexture2D(GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, diffuseSpecTexture.getId(), 0);

        depthRBO = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthRBO);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT24, width, height);
        GL30.glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRBO);

        int[] attachments = {GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2};
        GL20.glDrawBuffers(attachments);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("GBuffer framebuffer not complete!");
        }

        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    public void bind() {
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    public void unbind() {
        GL30.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public Texture getPositionTexture() { return positionTexture; }
    public Texture getNormalTexture() { return normalTexture; }
    public Texture getDiffuseSpecTexture() { return diffuseSpecTexture; }

    public void cleanup() {
        positionTexture.cleanup();
        normalTexture.cleanup();
        diffuseSpecTexture.cleanup();

        glDeleteFramebuffers(fbo);
    }
}
