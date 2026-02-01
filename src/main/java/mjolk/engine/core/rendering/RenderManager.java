package mjolk.engine.core.rendering;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.Camera;
import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Model;
import mjolk.engine.core.lighting.DirectionLight;
import mjolk.engine.core.lighting.PointLight;
import mjolk.engine.core.lighting.SpotLight;
import mjolk.engine.core.managers.ShaderManager;
import mjolk.engine.core.managers.WindowManager;
import mjolk.engine.core.utils.Constants;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

import static mjolk.engine.core.utils.Constants.SPECULAR_POWER;
import static org.lwjgl.opengl.GL11C.glViewport;

public class RenderManager {

    private final WindowManager window;
    private EntityRenderer entityRenderer;

    public RenderManager() {
        System.out.println("RenderManager constructor called");
        window = Launcher.getWindow();
    }

    public void init() throws Exception {
        System.out.println("RenderManager init called");
        entityRenderer = new EntityRenderer();
        entityRenderer.init();
    }

    public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights, DirectionLight directionLight, ShaderManager shader) {

        shader.setUniform("ambientLight", Constants.AMBIENT_LIGHT);
        shader.setUniform("specularPower", SPECULAR_POWER);

        int numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            shader.setUniform("spotLights", spotLights[i], i);
        }

        numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            shader.setUniform("pointLights", pointLights[i], i);
        }

        if (directionLight != null) {
            shader.setUniform("directionalLight", directionLight);
        }

    }

    public void render(Camera camera, DirectionLight directionLight, PointLight[] pointLights, SpotLight[] spotLights) {
        clear();

        if (window.isResize()) {
            glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }

        entityRenderer.render(camera, pointLights, spotLights, directionLight);
    }

    public void processEntities(Entity entity) {
        List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
        if (entityList != null) {
            entityList.add(entity);
        } else {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entityRenderer.getEntities().put(entity.getModel(), newEntityList);
        }
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        entityRenderer.cleanup();
    }
}
