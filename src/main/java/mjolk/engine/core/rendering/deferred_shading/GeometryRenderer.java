package mjolk.engine.core.rendering.deferred_shading;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.Camera;
import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Model;
import mjolk.engine.core.managers.ShaderManager;
import mjolk.engine.core.utils.GBuffer;
import mjolk.engine.core.utils.Transformation;
import mjolk.engine.core.utils.Utils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class GeometryRenderer {

    private ShaderManager geoShader;
    private GBuffer gbuffer;

    public void init(int windowWidth, int windowHeight) throws Exception {
        geoShader = new ShaderManager();
        String srcVert = Utils.loadResource("/shader/geometry_pass.vsh");
        String srcFrag = Utils.loadResource("/shader/geometry_pass.fsh");

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        geoShader.createVertexShader(srcVert);
        geoShader.createFragmentShader(srcFrag);

        geoShader.link();

        geoShader.createUniform("gWVP");
        geoShader.createUniform("gWorld");
        geoShader.createUniform("gColourMap");

        gbuffer = new GBuffer(windowWidth, windowHeight);
    }

    public void render(Camera camera, List<Entity> entities) {
        gbuffer.bindForWriting();

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        geoShader.bind();

        Matrix4f viewMatrix = new Matrix4f(Transformation.getViewMatrix(camera));
        Matrix4f projectionMatrix = new Matrix4f(Launcher.getWindow().updateProjectionMatrix(camera));

        for (Entity entity : entities) {

            bind(entity.getModel());

            Matrix4f worldMatrix = new Matrix4f(Transformation.createTransformationMatrix(entity));

            geoShader.setUniform("gColourMap", 0);
            geoShader.setUniform("gWorld", worldMatrix);
            geoShader.setUniform("gWVP", new Matrix4f(projectionMatrix).mul(viewMatrix).mul(worldMatrix));

            GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

            unbind();
        }

        geoShader.unbind();

    }

    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
    }

    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        GL30.glBindVertexArray(0);
    }
}
