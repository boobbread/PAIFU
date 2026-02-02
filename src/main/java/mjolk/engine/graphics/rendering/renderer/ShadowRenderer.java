package mjolk.engine.graphics.rendering.renderer;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Scene;
import mjolk.engine.core.maths.Transformation;
import mjolk.engine.core.utils.Utils;
import mjolk.engine.graphics.lighting.DirectionLight;
import mjolk.engine.graphics.lighting.Light;
import mjolk.engine.graphics.lighting.PointLight;
import mjolk.engine.graphics.lighting.shadow.ShadowMap;
import mjolk.engine.graphics.shader.ShaderManager;
import org.joml.Matrix4f;

import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ShadowRenderer {

    private static final Logger LOGGER = Logger.getLogger(ShadowRenderer.class.getName());
    private ShaderManager shader;

    public ShadowRenderer() throws Exception {
        LOGGER.info("ShadowRenderer constructor called");
        shader = new ShaderManager();
    }

    public void init() throws Exception {
        LOGGER.info("ShadowRenderer init called");

        shader.createVertexShader(Utils.loadShader("/shader/shadow_pass.vsh"));
        shader.createFragmentShader(Utils.loadShader("/shader/shadow_pass.fsh"));
        shader.link();

        shader.createUniform("lightSpaceMatrix");
        shader.createUniform("model");
    }


    public void render(Scene scene) {

        shader.bind();

        for (Light light : scene.getLights()) {
            if (!light.castsShadows()) continue;
            if (!(light instanceof PointLight)) continue;

            ShadowMap shadowMap = light.getShadowMap();
            Matrix4f lightSpaceMatrix = light.getViewProjectionMatrix();

            glViewport(0, 0, shadowMap.getWidth(), shadowMap.getHeight());
            shadowMap.bind();
            glClear(GL_DEPTH_BUFFER_BIT);
            glEnable(GL_DEPTH_TEST);

            shader.setUniform("lightSpaceMatrix", lightSpaceMatrix);

            for (Entity e : scene.getEntities()) {
                Matrix4f model = Transformation.createTransformationMatrix(e);
                shader.setUniform("model", model);

                glBindVertexArray(e.getModel().getId());
                glDrawElements(GL_TRIANGLES, e.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            }

            shadowMap.unbind();
        }


        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0,
                Launcher.getWindow().getWidth(),
                Launcher.getWindow().getHeight());

    }

    public void cleanup() {
        shader.cleanup();
    }
}
