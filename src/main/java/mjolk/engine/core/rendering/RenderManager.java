package mjolk.engine.core.rendering;

import mjolk.engine.Launcher;
import mjolk.engine.core.entity.Camera;
import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Model;
import mjolk.engine.core.lighting.DirectionLight;
import mjolk.engine.core.lighting.Light;
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
        window = Launcher.getWindow();
    }

    public void init() throws Exception {
        entityRenderer = new EntityRenderer();
        entityRenderer.init();

        System.out.println("RenderManager INIT");
    }

    public static void renderLights(Light[] lights, ShaderManager shader) {

        shader.setUniform("ambientLight", Constants.AMBIENT_LIGHT);
        shader.setUniform("specularPower", SPECULAR_POWER);

        int numLights = lights != null ? lights.length : 0;
        for (int i = 0; i < numLights; i++) {
            if (lights[i] instanceof SpotLight) {
                shader.setUniform("spotLights", (SpotLight) lights[i], i);
            }

            if (lights[i] instanceof PointLight) {
                shader.setUniform("pointLights", (PointLight) lights[i], i);
            }

            if (lights[i] instanceof DirectionLight) {
                shader.setUniform("directionLight", (DirectionLight) lights[i]);
            }
        }

    }

    public void render(Camera camera, Light[] lights) {
        clear();

        if (window.isResize()) {
            glViewport(0,0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }

        entityRenderer.render(camera, lights);
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

    public void renderScene() {

    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup() {
        entityRenderer.cleanup();
    }
}
