package mjolk.engine.graphics.rendering.renderer;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.Entity;
import mjolk.engine.graphics.mesh.Model;
import mjolk.engine.graphics.shader.ShaderManager;
import mjolk.engine.core.entity.Scene;
import mjolk.engine.core.maths.Transformation;
import mjolk.engine.core.utils.Utils;
import mjolk.engine.graphics.rendering.GBuffer;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class GeometryRenderer {
    private static final Logger LOGGER = Logger.getLogger(GeometryRenderer.class.getName());

    private GBuffer gBuffer;
    private ShaderManager shader;

    public GeometryRenderer(int width, int height) throws Exception {
        gBuffer = new GBuffer(width, height);
        shader = new ShaderManager();
    }

    public void init() throws Exception {
        gBuffer.init();
        String srcVert = Utils.loadShader("/shader/geometry_pass.vsh");
        String srcFrag = Utils.loadShader("/shader/geometry_pass.fsh");

        shader.createVertexShader(srcVert);
        shader.createFragmentShader(srcFrag);
        shader.link();

        shader.createUniform("model");
        shader.createUniform("view");
        shader.createUniform("projection");

        shader.createUniform("texture_diffuse1");
        shader.createUniform("materialSpecular");
    }

    public void geometryPass(Scene scene) {
        gBuffer.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        shader.bind();

        shader.setUniform("view", Transformation.getViewMatrix(scene.getCamera()));
        shader.setUniform("projection", Launcher.getWindow().updateProjectionMatrix(scene.getCamera()));

        shader.setUniform("texture_diffuse1", 0);

        for (Entity e : scene.renderQueue.keySet()) {

            Matrix4f modelMatrix = scene.renderQueue.get(e).first;
            Model model = scene.renderQueue.get(e).second;

            shader.setUniform("model", modelMatrix);
            shader.setUniform("materialSpecular", model.getMaterial().getSpecularColour());

            model.getTexture().bind(0);

            glBindVertexArray(model.getId());
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);
        }

        gBuffer.unbind();
    }

    public void bindGBufferTextures(int positionUnit, int normalUnit, int albedoSpecUnit) {
        gBuffer.getPositionTexture().bind(positionUnit);
        gBuffer.getNormalTexture().bind(normalUnit);
        gBuffer.getDiffuseSpecTexture().bind(albedoSpecUnit);
    }

    public void cleanup() {
        shader.cleanup();
        gBuffer.cleanup();
    }
}
