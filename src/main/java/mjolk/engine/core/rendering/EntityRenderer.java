package mjolk.engine.core.rendering;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.Camera;
import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Model;
import mjolk.engine.core.lighting.DirectionLight;
import mjolk.engine.core.lighting.PointLight;
import mjolk.engine.core.lighting.SpotLight;
import mjolk.engine.core.managers.ShaderManager;
import mjolk.engine.core.utils.Transformation;
import mjolk.engine.core.utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRenderer implements IRenderer {

    ShaderManager shader;
    private Map<Model, List<Entity>> entities;

    public EntityRenderer() throws Exception {
        entities = new HashMap<>();
        shader = new ShaderManager();
    }

    @Override
    public void init() throws Exception {
        shader.createVertexShader(Utils.loadResource("/shader/entity_vertex.vsh"));
        shader.createFragmentShader(Utils.loadResource("/shader/entity_fragment.fsh"));
        shader.link();

        shader.createUniform("textureSampler");

        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");

        shader.createUniform("ambientLight");
        shader.createMaterialUniform("material");

        shader.createUniform("specularPower");
        shader.createDirectionalLightUniform("directionalLight");

        shader.createPointLightListUniform("pointLights", 5);
        shader.createSpotLightListUniform("spotLights", 5);
    }

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionLight directionLight) {
        shader.bind();

        shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix(camera));
        RenderManager.renderLights(pointLights, spotLights, directionLight, shader);

        for (Model model : entities.keySet()) {
            bind(model);
            List<Entity> list = entities.get(model);
            for (Entity entity : list) {
                prepare(entity, camera);
                GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbind();
        }

        entities.clear();
        shader.unbind();
    }

    @Override
    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        shader.setUniform("material", model.getMaterial());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        GL30.glBindVertexArray(0);
    }

    @Override
    public void prepare(Object entity, Camera camera) {
        shader.setUniform("textureSampler", 0);
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix((Entity) entity));
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
    }

    @Override
    public void cleanup() {
        shader.cleanup();
    }

    public Map<Model, List<Entity>> getEntities() {
        return entities;
    }
}
