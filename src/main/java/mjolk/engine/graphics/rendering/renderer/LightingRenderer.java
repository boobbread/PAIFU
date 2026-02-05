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
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL31.*;

public class LightingRenderer {
    private static final Logger LOGGER = Logger.getLogger(LightingRenderer.class.getName());

    private ShaderManager shader;
    private ShaderManager debug;
    private ScreenQuad quad;

    private int pointLightsUBO;
    private int spotLightsUBO;
    private int dirLightsUBO;

    public LightingRenderer() throws Exception {
        shader = new ShaderManager();
        debug = new ShaderManager();
    }

    public void init() throws Exception {
        quad = new ScreenQuad();

        shader.createVertexShader(Utils.loadShader("/shader/lighting_pass.vsh"));
        shader.createFragmentShader(Utils.loadShader("/shader/lighting_pass.fsh"));
        shader.link();

        // Shadow atlas texture uniform
        shader.createUniform("shadowAtlas");

        // GBuffer texture uniforms
        shader.createUniform("gPosition");
        shader.createUniform("gNormal");
        shader.createUniform("gAlbedoSpec");

        // Lighting UBOs
        pointLightsUBO = shader.createUBO(6160, 1);

        spotLightsUBO = shader.createUBO(10256, 2);

        dirLightsUBO = shader.createUBO(8208, 3);

        int program = shader.getProgramID();

        int dirBlock   = glGetUniformBlockIndex(program, "DirectionLightBlock"); // = 2
        int pointBlock = glGetUniformBlockIndex(program, "PointLightBlock");     // = 1
        int spotBlock  = glGetUniformBlockIndex(program, "SpotLightBlock");      // = 0

        glUniformBlockBinding(program, dirBlock,   3);
        glUniformBlockBinding(program, pointBlock, 1);
        glUniformBlockBinding(program, spotBlock,  2);

        // Shadow atlas debug view
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

        shadowRenderer.getAtlas().getDepthTexture().bind(3);
        shader.setUniform("shadowAtlas", 3);

        geometryRenderer.bindGBufferTextures(0, 1, 2);
        shader.setUniform("gPosition", 0);
        shader.setUniform("gNormal", 1);
        shader.setUniform("gAlbedoSpec", 2);

        // Directional lights
        DirectionLight[] dirLights = scene.getDirectionalLights().toArray(new DirectionLight[0]);
        shader.setDirectionLightUBO(dirLights, dirLightsUBO);

        // Point lights
        PointLight[] pointLights = scene.getPointLights().toArray(new PointLight[0]);
        shader.setPointLightUBO(pointLights, pointLightsUBO);

        // Spot lights
        SpotLight[] spotLights = scene.getSpotLights().toArray(new SpotLight[0]);
        shader.setSpotLightUBO(spotLights, spotLightsUBO);

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

