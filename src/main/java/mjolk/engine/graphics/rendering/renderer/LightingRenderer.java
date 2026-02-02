package mjolk.engine.graphics.rendering.renderer;

import mjolk.engine.graphics.rendering.ScreenQuad;
import mjolk.engine.core.entity.Scene;
import mjolk.engine.graphics.lighting.DirectionLight;
import mjolk.engine.graphics.lighting.PointLight;
import mjolk.engine.graphics.lighting.SpotLight;
import mjolk.engine.graphics.shader.ShaderManager;
import mjolk.engine.core.utils.Utils;

import static org.lwjgl.opengl.GL11.*;

public class LightingRenderer {

    private ShaderManager shader;
    private ScreenQuad quad;

    public LightingRenderer() throws Exception {
        System.out.println("LightingRenderer constructor called");
        shader = new ShaderManager();
    }

    public void init() throws Exception {
        System.out.println("LightingRenderer init called");
        quad = new ScreenQuad();

        shader.createVertexShader(Utils.loadShader("/shader/lighting_pass.vsh"));
        shader.createFragmentShader(Utils.loadShader("/shader/lighting_pass.fsh"));
        shader.link();

        shader.createUniform("gPosition");
        shader.createUniform("gNormal");
        shader.createUniform("gAlbedoSpec");

        shader.createUniform("viewPos");

        // Automatically create flattened light arrays
        shader.createDirectionalLightArray("dirLight", 10); // max 10 directional lights
        shader.createPointLightArray("pointLight", 20);      // max 20 point lights
        shader.createSpotLightArray("spotLight", 10);       // max 10 spot lights

        quad.init();
    }

    public void render(Scene scene, GeometryRenderer geometryRenderer) {
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);

        shader.bind();

        geometryRenderer.bindGBufferTextures(0, 1, 2);
        shader.setUniform("gPosition", 0);
        shader.setUniform("gNormal", 1);
        shader.setUniform("gAlbedoSpec", 2);

        shader.setUniform("viewPos", scene.getCamera().getPosition());

        // Directional lights
        DirectionLight[] dirLights = scene.getDirectionalLights().toArray(new DirectionLight[0]);
        shader.setDirectionalLights("dirLight", dirLights);
        shader.setUniform("numDirLights", dirLights.length);

        // Point lights
        PointLight[] pointLights = scene.getPointLights().toArray(new PointLight[0]);
        shader.setPointLights("pointLight", pointLights);
        shader.setUniform("numPointLights", pointLights.length);

        // Spot lights
        SpotLight[] spotLights = scene.getSpotLights().toArray(new SpotLight[0]);
        shader.setSpotLights("spotLight", spotLights);
        shader.setUniform("numSpotLights", spotLights.length);

        quad.render();
    }

    public void cleanup() {
        shader.cleanup();
        quad.cleanup();
    }

}

