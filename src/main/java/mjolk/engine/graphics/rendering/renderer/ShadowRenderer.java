package mjolk.engine.graphics.rendering.renderer;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Scene;
import mjolk.engine.core.maths.Transformation;
import mjolk.engine.core.utils.Utils;
import mjolk.engine.graphics.lighting.Light;
import mjolk.engine.graphics.lighting.PointLight;
import mjolk.engine.graphics.lighting.SpotLight;
import mjolk.engine.graphics.lighting.shadow.ShadowAtlas;
import mjolk.engine.graphics.shader.ShaderManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ShadowRenderer {

    private static final Logger LOGGER = Logger.getLogger(ShadowRenderer.class.getName());
    private ShaderManager shader;
    private ShaderManager pointLightShader;

    private ShadowAtlas atlas;

    public ShadowRenderer() throws Exception {
        shader = new ShaderManager();
        pointLightShader = new ShaderManager();
    }

    public void init() throws Exception {

        shader.createVertexShader(Utils.loadShader("/shader/shadow_pass.vsh"));
        shader.createFragmentShader(Utils.loadShader("/shader/shadow_pass.fsh"));
        shader.link();

        shader.createUniform("lightSpaceMatrix");
        shader.createUniform("model");

        pointLightShader.createVertexShader(Utils.loadShader("/shader/pointlight/point_shadow.vsh"));
        pointLightShader.createFragmentShader(Utils.loadShader("/shader/pointlight/point_shadow.fsh"));

        pointLightShader.link();

        pointLightShader.createUniform("lightPos");
        pointLightShader.createUniform("model");
        pointLightShader.createUniform("lightView");
        pointLightShader.createUniform("paraboloidSide");
        pointLightShader.createUniform("farPlane");
        pointLightShader.createUniform("nearPlane");

        atlas = new ShadowAtlas();
    }


    public void render(Scene scene) {

        atlas.bind();
        shader.bind();
        glClear(GL_DEPTH_BUFFER_BIT);

        for (Light light : scene.getLights()) {
            if (!light.castsShadows()) continue;
            if (light instanceof PointLight && !(light instanceof SpotLight)) {
                renderPointLightShadow(scene, (PointLight) light);
                continue;
            }

            Matrix4f lightSpaceMatrix = light.getViewProjectionMatrix();
            Vector4f r = light.getShadowRect();

            glViewport(
                    (int)(r.x * ShadowAtlas.SIZE),
                    (int)(r.y * ShadowAtlas.SIZE),
                    (int)(r.z * ShadowAtlas.SIZE),
                    (int)(r.w * ShadowAtlas.SIZE)
            );

            shader.setUniform("lightSpaceMatrix", lightSpaceMatrix);

            for (Entity e : scene.getEntities()) {
                Matrix4f model = Transformation.createTransformationMatrix(e);
                shader.setUniform("model", model);

                glBindVertexArray(e.getModel().getId());
                glDrawElements(GL_TRIANGLES, e.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
                glBindVertexArray(0);
            }

        }


        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0,
                Launcher.getWindow().getWidth(),
                Launcher.getWindow().getHeight());

    }

    private void renderPointLightShadow(Scene scene, PointLight light) {

        shader.unbind();

        pointLightShader.bind();
        pointLightShader.setUniform("lightPos", light.getPosition());
        pointLightShader.setUniform("farPlane", light.getFarPlane());
        pointLightShader.setUniform("nearPlane", light.getNearPlane());

        Vector4f frontRect = light.getFrontRect();
        Vector4f backRect = light.getBackRect();

        // Check if they're the same
        if (frontRect.equals(backRect)) {
            LOGGER.severe("ERROR: Front and back rectangles are the same!");
        }

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glEnable(GL_CULL_FACE);

        glCullFace(GL_BACK);
        renderHemisphere(scene, frontRect, 1, light.getPosition());
        glCullFace(GL_FRONT);
        renderHemisphere(scene, backRect, -1, light.getPosition());
        glCullFace(GL_BACK);
        shader.bind();
    }

    private void renderHemisphere(Scene scene, Vector4f r, int hemi, Vector3f lightPos) {
        int x = Math.round(r.x * ShadowAtlas.SIZE);
        int y = Math.round(r.y * ShadowAtlas.SIZE);
        int w = Math.round(r.z * ShadowAtlas.SIZE);
        int h = Math.round(r.w * ShadowAtlas.SIZE);

        glViewport(x, y, w, h);

        Matrix4f view = new Matrix4f().identity();
        view.translate(-lightPos.x, -lightPos.y, -lightPos.z);

        pointLightShader.setUniform("paraboloidSide", hemi);
        pointLightShader.setUniform("lightView", view);

        for (Entity e : scene.getEntities()) {
            Matrix4f model = Transformation.createTransformationMatrix(e);
            pointLightShader.setUniform("model", model);

            glBindVertexArray(e.getModel().getId());
            glDrawElements(GL_TRIANGLES,
                    e.getModel().getVertexCount(),
                    GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);
        }
    }

    public ShadowAtlas getAtlas() {
        return atlas;
    }

    public void cleanup() {
        shader.cleanup();
        pointLightShader.cleanup();
    }
}
