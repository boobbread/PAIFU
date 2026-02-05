package mjolk.engine.graphics.shader;

import mjolk.engine.graphics.material.Material;
import mjolk.engine.graphics.lighting.DirectionLight;
import mjolk.engine.graphics.lighting.PointLight;
import mjolk.engine.graphics.lighting.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL40;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL31.GL_UNIFORM_BUFFER;

public class ShaderManager {

    private final int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    private int geometryShaderID;

    private final Map<String, Integer> uniforms;

    public ShaderManager() throws Exception {
        programID = GL20.glCreateProgram();
        if (programID == 0) {
            throw new Exception("Could not create program");
        }

        uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);

        if (uniformLocation < 0) {
            throw new Exception("Could not get uniform " + uniformName);
        }

        uniforms.put(uniformName, uniformLocation);
    }

    public void createMaterialUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    public void createDirectionalLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createDirectionalLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createPointLightUniform(String uniformName) throws Exception {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".constant");
        createUniform(uniformName + ".linear");
        createUniform(uniformName + ".exponent");
    }

    public void createPointLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightUniform(String uniformName) throws Exception {
        createPointLightUniform(uniformName + ".pl");
        createUniform(uniformName + ".coneDirection");
        createUniform(uniformName + ".cutoff");
    }

    public void createSpotLightListUniform(String uniformName, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createDirectionalShadowArray(String baseName, int maxCount) throws Exception {
        for (int i = 0; i < maxCount; i++) {
            createUniform(baseName + "[" + i + "]");
        }
    }

    public void createMatrixArray(String baseName, int maxCount) throws Exception {
        for (int i = 0; i < maxCount; i++) {
            createUniform(baseName + "[" + i + "]");
        }
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL20.glUniformMatrix4fv(uniforms.get(uniformName), false,
                    value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
        GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector4f value) {
        GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, boolean value) {
        float res = 0;
        if (value) {
            res = 1;
        }
        GL20.glUniform1f(uniforms.get(uniformName), res);
    }

    public void setUniform(String uniformName, float value) {
        GL20.glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".ambient", material.getAmbientColour());
        setUniform(uniformName + ".diffuse", material.getDiffuseColour());
        setUniform(uniformName + ".specular", material.getSpecularColour());
        setUniform(uniformName + ".hasTexture", material.hasTexture() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    public void setUniform(String uniformName, DirectionLight directionLight) {
        setUniform(uniformName + ".colour", directionLight.getColour());
        setUniform(uniformName + ".direction", directionLight.getDirection());
        setUniform(uniformName + ".intensity", directionLight.getIntensity());
    }

    public void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".colour", pointLight.getColour());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        setUniform(uniformName + ".constant", pointLight.getConstant());
        setUniform(uniformName + ".linear", pointLight.getLinear());
        setUniform(uniformName + ".exponent", pointLight.getExponent());
    }

    public void setUniform(String uniformName, SpotLight spotLight) {
        setUniform(uniformName + ".pl", spotLight.getPointLight());
        setUniform(uniformName + ".coneDirection", spotLight.getConeDirection());
        setUniform(uniformName + ".cutoff", spotLight.getCutoff());
    }

    public void setUniform(String uniformName, PointLight[]  pointLights) {
        int numLights = pointLights != null ? pointLights.length : 0;
        for(int i = 0; i < numLights; i++) {
            setUniform(uniformName, pointLights[i], i);
        }
    }

    public void setUniform(String uniformName, PointLight pointLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", pointLight);
    }

    public void setUniform(String uniformName, SpotLight[] spotLights) throws Exception {
        int numLights = spotLights != null ? spotLights.length : 0;
        for(int i = 0; i < numLights; i++) {
            setUniform(uniformName, spotLights[i], i);
        }
    }

    public void setUniform(String uniformName, SpotLight spotLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", spotLight);
    }

    public void setUniform(String uniformName, DirectionLight[] lights) {
        int count = lights != null ? lights.length : 0;
        for (int i = 0; i < count; i++) {
            setUniform(uniformName + "[" + i + "]", lights[i]);
        }
    }

    public void setPointLights(String baseName, PointLight[] lights) {
        for (int i = 0; i < lights.length; i++) {
            PointLight pl = lights[i];
            setUniform(baseName + "Positions[" + i + "]", pl.getPosition());
            setUniform(baseName + "Colours[" + i + "]", pl.getColour());
            setUniform(baseName + "Intensities[" + i + "]", pl.getIntensity());
            setUniform(baseName + "Constants[" + i + "]", pl.getConstant());
            setUniform(baseName + "Linears[" + i + "]", pl.getLinear());
            setUniform(baseName + "Exponents[" + i + "]", pl.getExponent());
        }
    }

    public void setDirectionalLights(String baseName, DirectionLight[] lights) {
        for (int i = 0; i < lights.length; i++) {
            DirectionLight dl = lights[i];
            setUniform(baseName + "Directions[" + i + "]", dl.getDirection());
            setUniform(baseName + "Colours[" + i + "]", dl.getColour());
            setUniform(baseName + "Intensities[" + i + "]", dl.getIntensity());
        }
    }

    public void setSpotLights(String baseName, SpotLight[] lights) {
        for (int i = 0; i < lights.length; i++) {
            SpotLight sl = lights[i];
            setUniform(baseName + "Positions[" + i + "]", sl.getPointLight().getPosition());
            setUniform(baseName + "Colours[" + i + "]", sl.getPointLight().getColour());
            setUniform(baseName + "Intensities[" + i + "]", sl.getPointLight().getIntensity());
            setUniform(baseName + "Constants[" + i + "]", sl.getPointLight().getConstant());
            setUniform(baseName + "Linears[" + i + "]", sl.getPointLight().getLinear());
            setUniform(baseName + "Exponents[" + i + "]", sl.getPointLight().getExponent());
            setUniform(baseName + "Directions[" + i + "]", sl.getConeDirection());
            setUniform(baseName + "Cutoffs[" + i + "]", sl.getCutoff());
        }
    }

    public void setShadowMaps(
            String uniformBase,
            int startTextureUnit,
            int count
    ) {
        for (int i = 0; i < count; i++) {
            GL20.glUniform1i(
                    uniforms.get(uniformBase + "[" + i + "]"),
                    startTextureUnit + i
            );
        }
    }

    public void setMatrixArray(String baseName, Matrix4f[] matrices) {
        for (int i = 0; i < matrices.length; i++) {
            setUniform(baseName + "[" + i + "]", matrices[i]);
        }
    }


    // ---- Directional Lights ----
    public void createDirectionalLightArray(String baseName, int maxCount) throws Exception {
        createUniform("numDirLights");
        for (int i = 0; i < maxCount; i++) {
            createUniform(baseName + "Directions[" + i + "]");
            createUniform(baseName + "Colours[" + i + "]");
            createUniform(baseName + "Intensities[" + i + "]");
        }
    }

    // ---- Point Lights ----
    public void createPointLightArray(String baseName, int maxCount) throws Exception {
        createUniform("numPointLights");
        for (int i = 0; i < maxCount; i++) {
            createUniform(baseName + "Positions[" + i + "]");
            createUniform(baseName + "Colours[" + i + "]");
            createUniform(baseName + "Intensities[" + i + "]");
            createUniform(baseName + "Constants[" + i + "]");
            createUniform(baseName + "Linears[" + i + "]");
            createUniform(baseName + "Exponents[" + i + "]");
        }
    }

    // ---- Spot Lights ----
    public void createSpotLightArray(String baseName, int maxCount) throws Exception {
        createUniform("numSpotLights");
        for (int i = 0; i < maxCount; i++) {
            createUniform(baseName + "Positions[" + i + "]");
            createUniform(baseName + "Colours[" + i + "]");
            createUniform(baseName + "Intensities[" + i + "]");
            createUniform(baseName + "Constants[" + i + "]");
            createUniform(baseName + "Linears[" + i + "]");
            createUniform(baseName + "Exponents[" + i + "]");
            createUniform(baseName + "Directions[" + i + "]");
            createUniform(baseName + "Cutoffs[" + i + "]");
        }
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    public void createGeometryShader(String shaderCode) throws Exception {
        geometryShaderID = createShader(shaderCode, GL40.GL_GEOMETRY_SHADER);
    }

    public int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderID = GL20.glCreateShader(shaderType);
        if (shaderID == 0) {
            throw new Exception("Could not create shader of type: " + shaderType);
        }

        GL20.glShaderSource(shaderID, shaderCode);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0) {
            throw new Exception("Could not compile shader of type: " + shaderType
                    + "\n" + GL20.glGetShaderInfoLog(shaderID, 1024));
        }

        GL20.glAttachShader(programID, shaderID);

        return shaderID;
    }

    public void link() throws Exception {

        glLinkProgram(programID);
        int status = glGetProgrami(programID, GL_LINK_STATUS);
        if (status == GL_FALSE) {
            String log = glGetProgramInfoLog(programID);
            throw new RuntimeException("Could not link shader program: " + log);
        }

        if (vertexShaderID != 0) {
            GL20.glDetachShader(programID, vertexShaderID);
        }

        if (fragmentShaderID != 0) {
            GL20.glDetachShader(programID, fragmentShaderID);
        }

        if (geometryShaderID != 0) {
            GL20.glDetachShader(programID, geometryShaderID);
        }

        GL20.glValidateProgram(programID);
        if (glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0) {
            throw new Exception("Could not validate program: " + glGetProgramInfoLog(programID, 1024));
        }
    }

    public void bind() {
        GL20.glUseProgram(programID);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programID != 0) {
            GL20.glDeleteProgram(programID);
        }
    }

    public void createFloatArray(String uniformName, int maxCount) throws Exception {
        for (int i = 0; i < maxCount; i++) {
            createUniform(uniformName + "[" + i + "]");
        }
    }

    public void setFloatArray(String uniformName, float[] farPlanes) {
        for (int i = 0; i < farPlanes.length; i++) {
            setUniform(uniformName + "[" + i + "]", farPlanes[i]);
        }
    }

    public void createVector4fArray(String uniformName, int maxCount) throws Exception {
        for (int i = 0; i < maxCount; i++) {
            createUniform(uniformName + "[" + i + "]");
        }
    }

    public void setVector4fArray(String uniformName, Vector4f[] vec) {
        for (int i = 0; i < vec.length; i++) {
            setUniform(uniformName + "[" + i + "]", vec[i]);
        }
    }

    /**
     * Creates a Uniform Buffer Object
     * @param size (bytes) the size of the UBO
     * @param bindingIndex 1 = point lights, 2 = spotlights, 3 = directional lights
     * @return (int) The UBO buffer
     */
    public int createUBO(int size, int bindingIndex) {
        if (size <= 0) {
            throw new IllegalArgumentException("UBO size must be positive and non-zero");
        }

        if (size % 16 != 0) {
            throw new IllegalArgumentException("UBO size must be a multiple of 16 bytes");
        }

        int ubo = glGenBuffers();

        glBindBuffer(GL_UNIFORM_BUFFER, ubo);
        glBufferData(GL_UNIFORM_BUFFER, size, GL_DYNAMIC_DRAW);
        glBindBufferBase(GL_UNIFORM_BUFFER, bindingIndex, ubo);
        glBindBuffer(GL_UNIFORM_BUFFER, 0);

        return ubo;
    }

    public void setDirectionLightUBO(DirectionLight[] directionLights, int ubo) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int numDirLights = Math.min(directionLights.length, 64);

            ByteBuffer buffer = stack.malloc(8208);

            // Header
            buffer.putInt(numDirLights);
            buffer.putFloat(0); // Padding
            buffer.putFloat(0);
            buffer.putFloat(0);

            // Lights
            for (int i = 0; i < numDirLights; i++) {
                DirectionLight l = directionLights[i];

                // vec4 direction
                buffer.putFloat(l.getDirection().x);
                buffer.putFloat(l.getDirection().y);
                buffer.putFloat(l.getDirection().z);
                buffer.putFloat(0);

                // vec4 colour
                buffer.putFloat(l.getColour().x);
                buffer.putFloat(l.getColour().y);
                buffer.putFloat(l.getColour().z);
                buffer.putFloat(0);

                // vec4 intensity
                buffer.putFloat(l.getIntensity());
                buffer.putFloat(0);
                buffer.putFloat(0);
                buffer.putFloat(0);

                // vec4 rect
                buffer.putFloat(l.getShadowRect().x);
                buffer.putFloat(l.getShadowRect().y);
                buffer.putFloat(l.getShadowRect().z);
                buffer.putFloat(l.getShadowRect().w);

                l.getViewProjectionMatrix().get(buffer);
            }

            buffer.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);
        }
    }

    public void setPointLightUBO(PointLight[] pointLights, int ubo) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int numPointLights = Math.min(pointLights.length, 64);

            ByteBuffer buffer = stack.malloc(6160);

            // Header
            buffer.putInt(numPointLights);
            buffer.putFloat(0);
            buffer.putFloat(0);
            buffer.putFloat(0);

            // Lights
            for (int i = 0; i < numPointLights; i++) {
                PointLight l = pointLights[i];

                // vec4 pos
                buffer.putFloat(l.getPosition().x);
                buffer.putFloat(l.getPosition().y);
                buffer.putFloat(l.getPosition().z);
                buffer.putFloat(0);

                // vec4 colour
                buffer.putFloat(l.getColour().x);
                buffer.putFloat(l.getColour().y);
                buffer.putFloat(l.getColour().z);
                buffer.putFloat(0);

                // vec4 params
                buffer.putFloat(l.getIntensity());
                buffer.putFloat(l.getConstant());
                buffer.putFloat(l.getLinear());
                buffer.putFloat(l.getExponent());

                // vec4 frontRect
                buffer.putFloat(l.getFrontRect().x);
                buffer.putFloat(l.getFrontRect().y);
                buffer.putFloat(l.getFrontRect().z);
                buffer.putFloat(l.getFrontRect().w);

                // vec4 backRect
                buffer.putFloat(l.getBackRect().x);
                buffer.putFloat(l.getBackRect().y);
                buffer.putFloat(l.getBackRect().z);
                buffer.putFloat(l.getBackRect().w);

                // vec4 farPlane
                buffer.putFloat(l.getFarPlane());
                buffer.putFloat(0);
                buffer.putFloat(0);
                buffer.putFloat(0);
            }

            buffer.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);
        }
    }

    public void setSpotLightUBO(SpotLight[] spotLights, int ubo) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int numSpotLights = Math.min(spotLights.length, 64);

            ByteBuffer buffer = stack.malloc(10256);

            buffer.putInt(numSpotLights);
            buffer.putFloat(0);
            buffer.putFloat(0);
            buffer.putFloat(0);

            for (int i = 0; i < numSpotLights; i++) {
                SpotLight l = spotLights[i];

                // vec4 position
                buffer.putFloat(l.getPosition().x);
                buffer.putFloat(l.getPosition().y);
                buffer.putFloat(l.getPosition().z);
                buffer.putFloat(0);

                // vec4 colour
                buffer.putFloat(l.getColour().x);
                buffer.putFloat(l.getColour().y);
                buffer.putFloat(l.getColour().z);
                buffer.putFloat(0);

                // vec4 params
                buffer.putFloat(l.getIntensity());
                buffer.putFloat(l.getConstant());
                buffer.putFloat(l.getLinear());
                buffer.putFloat(l.getExponent());

                // vec4 rect
                buffer.putFloat(l.getShadowRect().x);
                buffer.putFloat(l.getShadowRect().y);
                buffer.putFloat(l.getShadowRect().z);
                buffer.putFloat(l.getShadowRect().w);

                // vec4 direction
                buffer.putFloat(l.getConeDirection().x);
                buffer.putFloat(l.getConeDirection().y);
                buffer.putFloat(l.getConeDirection().z);
                buffer.putFloat(0);

                // mat4 lightSpaceMatrix
                l.getViewProjectionMatrix().get(buffer);

                // vec4 cutoff
                buffer.putFloat(l.getCutoff());
                buffer.putFloat(0);
                buffer.putFloat(0);
                buffer.putFloat(0);
            }

            buffer.flip();

            glBindBuffer(GL_UNIFORM_BUFFER, ubo);
            glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer);
            glBindBuffer(GL_UNIFORM_BUFFER, 0);
        }
    }

}
