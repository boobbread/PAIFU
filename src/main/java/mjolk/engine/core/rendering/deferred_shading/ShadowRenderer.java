package mjolk.engine.core.rendering.deferred_shading;

import mjolk.engine.core.entity.Entity;
import mjolk.engine.core.entity.Model;
import mjolk.engine.core.lighting.*;
import mjolk.engine.core.managers.ShaderManager;
import mjolk.engine.core.utils.Transformation;
import mjolk.engine.core.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.glFramebufferTexture2D;

public class ShadowRenderer {

    private ShaderManager depthShader;

    private ShadowMap spotDirShadowMap;
    private List<ShadowCubeMap> pointLightShadowMaps;

    private int shadowWidth = 2048;
    private int shadowHeight = 2048;

    private int atlasWidth, atlasHeight, numCols, numRows;

    public void init(Light[] lights) throws Exception {
        depthShader = new ShaderManager();
        depthShader.createVertexShader(Utils.loadResource("/shader/depth_vertex.vsh"));
        depthShader.createFragmentShader(Utils.loadResource("/shader/depth_fragment.fsh"));
        depthShader.link();

        depthShader.createUniform("lightViewProjectionMatrix");
        depthShader.createUniform("modelMatrix");

        int spotDirCount = 0;
        for (Light l : lights) {
            if (l instanceof SpotLight || l instanceof DirectionLight) spotDirCount++;
        }

        if (spotDirCount > 0) {
            numRows = (int) Math.ceil(spotDirCount / 2.0f);
            numCols = 2;
            atlasWidth = shadowWidth * numCols;
            atlasHeight = shadowHeight * numRows;
            spotDirShadowMap = new ShadowMap(atlasWidth, atlasHeight);
        }

        pointLightShadowMaps = new ArrayList<>();
        for (Light l : lights) {
            if (l instanceof PointLight) {
                pointLightShadowMaps.add(new ShadowCubeMap(shadowWidth, shadowHeight));
            }
        }
    }

    public void render(List<Entity> entities, Light[] lights) {
        renderSpotDir(entities, lights);
        renderPoint(entities, lights);
    }

    private void renderSpotDir(List<Entity> entities, Light[] lights) {
        if (spotDirShadowMap == null) return;

        glBindFramebuffer(GL_FRAMEBUFFER, spotDirShadowMap.getDepthMapFBO());
        glClear(GL_DEPTH_BUFFER_BIT);
        depthShader.bind();

        int tileIndex = 0;
        for (Light light : lights) {
            if (!(light instanceof SpotLight || light instanceof DirectionLight)) continue;

            int col = tileIndex % numCols;
            int row = tileIndex / numCols;
            glViewport(col * shadowWidth, row * shadowHeight, shadowWidth, shadowHeight);

            Matrix4f lightVP = light.getProjectionMatrix();
            depthShader.setUniform("lightViewProjectionMatrix", lightVP);

            for (Entity entity : entities) {
                depthShader.setUniform("modelMatrix", Transformation.createTransformationMatrix(entity));
                bind(entity.getModel());
                glDrawElements(GL_TRIANGLES, entity.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
                unbind();
            }
            tileIndex++;
        }

        depthShader.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    private void renderPoint(List<Entity> entities, Light[] lights) {
        int pointIndex = 0;
        for (Light light : lights) {
            if (!(light instanceof PointLight)) continue;

            ShadowCubeMap cubeMap = pointLightShadowMaps.get(pointIndex);
            PointLight point = (PointLight) light;

            Matrix4f proj = new Matrix4f().perspective((float) Math.toRadians(90), 1f, 0.1f, 100f);
            Vector3f pos = point.getPosition();

            Matrix4f[] transforms = new Matrix4f[6];
            transforms[0] = new Matrix4f(proj).lookAt(pos, pos.add(new Vector3f(1,0,0)), new Vector3f(0,-1,0));
            transforms[1] = new Matrix4f(proj).lookAt(pos, pos.add(new Vector3f(-1,0,0)), new Vector3f(0,-1,0));
            transforms[2] = new Matrix4f(proj).lookAt(pos, pos.add(new Vector3f(0,1,0)), new Vector3f(0,0,1));
            transforms[3] = new Matrix4f(proj).lookAt(pos, pos.add(new Vector3f(0,-1,0)), new Vector3f(0,0,-1));
            transforms[4] = new Matrix4f(proj).lookAt(pos, pos.add(new Vector3f(0,0,1)), new Vector3f(0,-1,0));
            transforms[5] = new Matrix4f(proj).lookAt(pos, pos.add(new Vector3f(0,0,-1)), new Vector3f(0,-1,0));

            depthShader.bind();
            glBindFramebuffer(GL_FRAMEBUFFER, cubeMap.getCubeFBO());
            glViewport(0, 0, shadowWidth, shadowHeight);

            for (int face = 0; face < 6; face++) {
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
                        GL_TEXTURE_CUBE_MAP_POSITIVE_X + face,
                        cubeMap.getCubeTexture(), 0);
                glClear(GL_DEPTH_BUFFER_BIT);

                depthShader.setUniform("lightViewProjectionMatrix", transforms[face]);

                for (Entity entity : entities) {
                    depthShader.setUniform("modelMatrix", Transformation.createTransformationMatrix(entity));
                    bind(entity.getModel());
                    glDrawElements(GL_TRIANGLES, entity.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
                    unbind();
                }
            }

            depthShader.unbind();
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
            pointIndex++;
        }
    }

    public int getSpotDirDepthTexture() {
        return spotDirShadowMap != null ? spotDirShadowMap.getDepthMapTexture().getId() : 0;
    }

    public List<ShadowCubeMap> getPointLightShadowMaps() {
        return pointLightShadowMaps;
    }

    private void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
    }

    private void unbind() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

}
