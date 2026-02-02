package mjolk.engine.graphics.shader;

import mjolk.engine.graphics.material.Material;
import mjolk.engine.graphics.lighting.DirectionLight;
import mjolk.engine.graphics.lighting.PointLight;
import mjolk.engine.graphics.lighting.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderManager {

    private final int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

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

//        if (uniformLocation < 0) {
//            throw new Exception("Could not get uniform " + uniformName);
//        }

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

    public void setUniform(String uniformName, SpotLight[] spotLights) {
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
            throw new RuntimeException("Could not link shader program");
        }

        if (vertexShaderID != 0) {
            GL20.glDetachShader(programID, vertexShaderID);
        }

        if (fragmentShaderID != 0) {
            GL20.glDetachShader(programID, fragmentShaderID);
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
}
