#version 400 core

in vec2 TexCoords;
out vec4 FragColor;

// ----- G-buffer -----
uniform sampler2D gPosition;
uniform sampler2D gNormal;
uniform sampler2D gAlbedoSpec;

// ----- Camera -----
uniform vec3 viewPos;

// ----- Directional Lights -----
uniform int numDirLights;
uniform vec3 dirLightDirections[10];
uniform vec3 dirLightColors[10];
uniform float dirLightIntensities[10];

// ----- Point Lights -----
uniform int numPointLights;
uniform vec3 pointLightPositions[20];
uniform vec3 pointLightColors[20];
uniform float pointLightIntensities[20];
uniform float pointLightConstants[20];
uniform float pointLightLinears[20];
uniform float pointLightExponents[20];

// ----- Spot Lights -----
uniform int numSpotLights;
uniform vec3 spotLightPositions[10];
uniform vec3 spotLightColors[10];
uniform float spotLightIntensities[10];
uniform float spotLightConstants[10];
uniform float spotLightLinears[10];
uniform float spotLightExponents[10];
uniform vec3 spotLightDirections[10];
uniform float spotLightCutoffs[10];

void main() {
    vec3 FragPos = texture(gPosition, TexCoords).rgb;
    vec3 Normal = normalize(texture(gNormal, TexCoords).rgb);
    vec3 Albedo = texture(gAlbedoSpec, TexCoords).rgb;
    float SpecularStrength = texture(gAlbedoSpec, TexCoords).a;

    vec3 lighting = vec3(0.0);

    // ----- Directional Lights -----
    for (int i = 0; i < numDirLights; ++i) {
        vec3 lightDir = normalize(-dirLightDirections[i]);
        float diff = max(dot(Normal, lightDir), 0.0);
        vec3 diffuse = diff * dirLightColors[i] * dirLightIntensities[i];
        lighting += diffuse;
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

        vec3 diffuse = diff * pointLightColors[i] * pointLightIntensities[i] * attenuation;
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

        vec3 diffuse = diff * spotLightColors[i] * spotLightIntensities[i] * attenuation * intensity;
        lighting += diffuse;
    }

    FragColor = vec4(lighting * Albedo, 1.0);
}
