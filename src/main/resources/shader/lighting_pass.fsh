#version 400 core

in vec2 TexCoords;
out vec4 FragColour;

// ----- G-buffer -----
uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

// ----- Camera -----
uniform vec3 viewPos;

// ----- Directional Lights -----
uniform int numDirLights;
uniform vec3 dirLightDirections[10];
uniform vec3 dirLightColours[10];
uniform float dirLightIntensities[10];

// ----- Point Lights -----
uniform int numPointLights;
uniform vec3 pointLightPositions[20];
uniform vec3 pointLightColours[20];
uniform float pointLightIntensities[20];
uniform float pointLightConstants[20];
uniform float pointLightLinears[20];
uniform float pointLightExponents[20];

// ----- Spot Lights -----
uniform int numSpotLights;
uniform vec3 spotLightPositions[10];
uniform vec3 spotLightColours[10];
uniform float spotLightIntensities[10];
uniform float spotLightConstants[10];
uniform float spotLightLinears[10];
uniform float spotLightExponents[10];
uniform vec3 spotLightDirections[10];
uniform float spotLightCutoffs[10];

uniform sampler2D dirShadowMaps[10];
uniform mat4 dirLightSpaceMatrices[10];

uniform sampler2D spotShadowMaps[10];
uniform mat4 spotLightSpaceMatrices[10];

float ShadowCalculation(vec3 fragPosWorld, vec3 normal, vec3 lightDir, sampler2D shadowMap, mat4 lightSpaceMatrix) {
    vec4 fragPosLightSpace = lightSpaceMatrix * vec4(fragPosWorld, 1.0);
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;

    // Outside light frustum → no shadow
    projCoords = projCoords * 0.5 + 0.5;

    if (projCoords.z > 1.0)
    return 0.0;

    if (projCoords.x < 0.0 || projCoords.x > 1.0 ||
    projCoords.y < 0.0 || projCoords.y > 1.0)
    return 0.0;

    float bias = max(0.005 * (1.0 - dot(normal, lightDir)), 0.001);

    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);

    for (int x = -1; x <= 1; ++x) {
        for (int y = -1; y <= 1; ++y) {
            float pcfDepth = texture(
            shadowMap,
            projCoords.xy + vec2(x, y) * texelSize
            ).r;

            shadow += (projCoords.z - bias > pcfDepth) ? 1.0 : 0.0;
        }
    }

    return shadow / 9.0;
}

void main() {
    vec3 FragPos = texture(gPosition, TexCoords).rgb;
    vec3 Normal = normalize(texture(gNormal, TexCoords).rgb);
    vec3 Albedo = texture(gAlbedoSpec, TexCoords).rgb;
    float SpecularStrength = texture(gAlbedoSpec, TexCoords).a;

    vec3 lighting = vec3(0.0);

    // ----- Directional Lights -----
    for (int i = 0; i < numDirLights; i++) {
        vec3 lightDir = normalize(-dirLightDirections[i]);

        float diff = max(dot(Normal, lightDir), 0.0);
        vec3 diffuse = diff * dirLightColours[i] * dirLightIntensities[i];

        float shadow = ShadowCalculation(
        FragPos,
        Normal,
        lightDir,
        dirShadowMaps[i],
        dirLightSpaceMatrices[i]
        );

        lighting += (1.0 - shadow) * diffuse;
    }

    // ----- Point Lights -----
    for (int i = 0; i < numPointLights; ++i) {
        vec3 lightDir = pointLightPositions[i] - FragPos;
        float distance = length(lightDir);
        lightDir = normalize(lightDir);

        float diff = max(dot(Normal, lightDir), 0.0);

        float attenuation = 1.0 / (pointLightConstants[i] +
        pointLightLinears[i] * distance +
        pointLightExponents[i] * distance * distance);

        vec3 diffuse = diff * pointLightColours[i] * pointLightIntensities[i] * attenuation;
        lighting += diffuse;
    }

    // ----- Spot Lights -----
    for (int i = 0; i < numSpotLights; ++i) {
        vec3 lightDir = spotLightPositions[i] - FragPos;
        float distance = length(lightDir);
        lightDir = normalize(lightDir);

        float diff = max(dot(Normal, lightDir), 0.0);

        float theta = dot(lightDir, normalize(-spotLightDirections[i]));
        float epsilon = 0.1;
        float intensity = clamp((theta - spotLightCutoffs[i]) / epsilon, 0.0, 1.0);

        float attenuation = 1.0 / (spotLightConstants[i] +
        spotLightLinears[i] * distance +
        spotLightExponents[i] * distance * distance);

        vec3 diffuse = diff * spotLightColours[i] * spotLightIntensities[i] * attenuation * intensity;

        float shadow = ShadowCalculation(
        FragPos,
        Normal,
        lightDir,
        spotShadowMaps[i],
        spotLightSpaceMatrices[i]
        );

        lighting += (1.0 - shadow) * diffuse;
    }

    vec3 ambient = 0.05 * Albedo;

    FragColour = vec4(ambient + lighting * Albedo, 1.0);
}
