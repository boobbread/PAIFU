package mjolk.engine.core.rendering;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.*;
import mjolk.engine.core.lighting.DirectionLight;
import mjolk.engine.core.lighting.PointLight;
import mjolk.engine.core.lighting.ShadowMap;
import mjolk.engine.core.lighting.SpotLight;
import mjolk.engine.core.managers.ShaderManager;
import mjolk.engine.core.utils.Transformation;
import mjolk.engine.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class EntityRenderer implements IRenderer {

    ShaderManager shader;
    ShaderManager depthShader;
    ShadowMap shadowMap;
    private Map<Model, List<Entity>> entities;

    Matrix4f lightViewProjectionMatrix;

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
        shader.createUniform("lightViewProjectionMatrix");

        shader.createUniform("ambientLight");
        shader.createMaterialUniform("material");

        shader.createUniform("specularPower");
        shader.createDirectionalLightUniform("directionalLight");

        shader.createPointLightListUniform("pointLights", 5);
        shader.createSpotLightListUniform("spotLights", 5);

        shader.createUniform("shadowMap");

        shadowMap = new ShadowMap();
        setupDepthShader();
    }

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionLight directionLight) {

        renderDepthMap(spotLights[0]);
        Texture depthMap = shadowMap.getDepthMapTexture();
        glViewport(0, 0, Launcher.getWindow().getWidth(), Launcher.getWindow().getHeight());

        shader.bind();

        shader.setUniform("projectionMatrix", Launcher.getWindow().updateProjectionMatrix(camera));
        shader.setUniform("shadowMap", depthMap.getId());
        shader.setUniform("lightViewProjectionMatrix", lightViewProjectionMatrix);

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

    public void bindDepth(Model model) {
        GL30.glBindVertexArray(model.getId());

        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        GL30.glBindVertexArray(0);
    }

    public void unbindDepth() {
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

    private void setupDepthShader() throws Exception {
        depthShader = new ShaderManager();
        depthShader.createVertexShader(Utils.loadResource("/shader/depth_vertex.vsh"));
        depthShader.createFragmentShader(Utils.loadResource("/shader/depth_fragment.fsh"));
        depthShader.link();

        depthShader.createUniform("lightViewProjectionMatrix");
    }

    public void renderDepthMap(SpotLight spotLight) {
        glEnable(GL_DEPTH_TEST);
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);

        glClear(GL_DEPTH_BUFFER_BIT);
        depthShader.bind();

        Vector3f coneDir = spotLight.getConeDirection();
        Vector3f lightPos = spotLight.getPointLight().getPosition();

        float fovy = (float) Math.toRadians(30);
//        (2 * Math.acos(spotLight.getCutoff())
        // .lookAt requires an eye (position), centre (where to look) and an up direction (0,1,0)
        Matrix4f lightViewMatrix = new Matrix4f().lookAt(lightPos, new Vector3f(lightPos).add(new Vector3f(coneDir)), new Vector3f(0, 1, 0));
        Matrix4f lightProjectionMatrix = new Matrix4f().perspective(fovy, 1f, 0.1f, 50.0f);
        lightViewProjectionMatrix = new Matrix4f(lightProjectionMatrix).mul(lightViewMatrix);


        depthShader.setUniform("lightViewProjectionMatrix", lightViewProjectionMatrix);

        for (Model model : entities.keySet()) {
            bindDepth(model);

            List<Entity> list = entities.get(model);
            for (Entity entity : list) {
                GL11.glDrawElements(GL11.GL_TRIANGLES, entity.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }

            unbindDepth();
        }

        depthShader.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
