package mjolk.engine.graphics.rendering.renderer;

import mjolk.engine.Launcher;
import mjolk.engine.graphics.lighting.shadow.ShadowAtlas;
import mjolk.engine.graphics.rendering.ScreenQuad;
import mjolk.engine.core.entity.Scene;
import mjolk.engine.graphics.lighting.DirectionLight;
import mjolk.engine.graphics.lighting.PointLight;
import mjolk.engine.graphics.lighting.SpotLight;
import mjolk.engine.graphics.shader.ShaderManager;
import mjolk.engine.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Arrays;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

public class LightingRenderer {
    private static final Logger LOGGER = Logger.getLogger(LightingRenderer.class.getName());

    private ShaderManager shader;
    private ShaderManager debug;
    private ScreenQuad quad;

    public LightingRenderer() throws Exception {
        LOGGER.info("LightingRenderer constructor called");
        shader = new ShaderManager();
        debug = new ShaderManager();
    }

    public void init() throws Exception {
        LOGGER.info("LightingRenderer init called");
        quad = new ScreenQuad();

        shader.createVertexShader(Utils.loadShader("/shader/lighting_pass.vsh"));
        shader.createFragmentShader(Utils.loadShader("/shader/lighting_pass.fsh"));
        shader.link();

        shader.createUniform("gPosition");
        shader.createUniform("gNormal");
        shader.createUniform("gAlbedoSpec");

        shader.createUniform("viewPos");

        // Automatically create flattened light arrays
        shader.createDirectionalLightArray("dirLight", 1); // max 10 directional lights
        shader.createPointLightArray("pointLight", 20);      // max 20 point lights
        shader.createSpotLightArray("spotLight", 10);       // max 10 spot lights

        shader.createUniform("shadowAtlas");

        shader.createMatrixArray("dirLightSpaceMatrices", 1);
        shader.createMatrixArray("spotLightSpaceMatrices", 10);
        shader.createFloatArray("pointLightFarPlanes", 20);

        shader.createVector4fArray("dirLightRects", 1);
        shader.createVector4fArray("spotLightRects", 10);
        shader.createVector4fArray("pointLightFrontRects", 20);
        shader.createVector4fArray("pointLightBackRects", 20);

        debug.createVertexShader(Utils.loadShader("/shader/debug.vsh"));
        debug.createFragmentShader(Utils.loadShader("/shader/debug.fsh"));
        debug.link();

        debug.createUniform("shadowAtlas");

        quad.init();
    }

    public void render(Scene scene, GeometryRenderer geometryRenderer, ShadowRenderer shadowRenderer) throws Exception {
        glClear(GL_COLOR_BUFFER_BIT);
        glDisable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

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

        shadowRenderer.getAtlas().getDepthTexture().bind(3);
        shader.setUniform("shadowAtlas", 3);

        int textureUnit = 3;

        // ---- Directional shadows ----
        Matrix4f[] dirMatrices = new Matrix4f[dirLights.length];
        for (int i = 0; i < dirLights.length; i++) {
            dirMatrices[i] = dirLights[i].getViewProjectionMatrix();
        }

        shader.setMatrixArray("dirLightSpaceMatrices", dirMatrices);

        Vector4f[] dirRects = new Vector4f[dirLights.length];
        for (int i = 0; i < dirLights.length; i++)
            dirRects[i] = dirLights[i].getShadowRect();

        shader.setVector4fArray("dirLightRects", dirRects);

        textureUnit += dirLights.length;

        Matrix4f[] spotMatrices = new Matrix4f[spotLights.length];
        for (int i = 0; i < spotLights.length; i++) {
            spotMatrices[i] = spotLights[i].getViewProjectionMatrix();
        }

        shader.setMatrixArray("spotLightSpaceMatrices", spotMatrices);

        Vector4f[] spotRects = new Vector4f[spotLights.length];
        for (int i = 0; i < spotLights.length; i++)
            spotRects[i] = spotLights[i].getShadowRect();

        shader.setVector4fArray("spotLightRects", spotRects);

        textureUnit += spotLights.length;

        float[] farPlanes = new float[pointLights.length];
        for (int i = 0; i < pointLights.length; i++) {
            farPlanes[i] = pointLights[i].getFarPlane();
        }

        Vector4f[] pointRectsFront = new Vector4f[pointLights.length];
        for (int i = 0; i < pointLights.length; i++)
            pointRectsFront[i] = pointLights[i].getFrontRect();

        shader.setVector4fArray("pointLightFrontRects", pointRectsFront);

        Vector4f[] pointRectsBack = new Vector4f[pointLights.length];
        for (int i = 0; i < pointLights.length; i++)
            pointRectsBack[i] = pointLights[i].getBackRect();

        shader.setVector4fArray("pointLightBackRects", pointRectsBack);

        shader.setFloatArray("pointLightFarPlanes", farPlanes);

        textureUnit += pointLights.length;

        quad.render();
//
//        debug.bind();
//        glBindTexture(GL_TEXTURE_2D, shadowRenderer.getAtlas().getDepthTexture().getId());
//        debug.setUniform("shadowAtlas", 0);
//
//        quad.render();
//        debug.unbind();
    }

    public void cleanup() {
        shader.cleanup();
        quad.cleanup();
    }

}

