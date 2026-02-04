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
uniform vec3 dirLightDirections[1];
uniform vec3 dirLightColours[1];
uniform float dirLightIntensities[1];

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

uniform sampler2D shadowAtlas;

uniform vec4 dirLightRects[1];
uniform vec4 spotLightRects[10];
uniform vec4 pointLightFrontRects[20];
uniform vec4 pointLightBackRects[20];

uniform mat4 dirLightSpaceMatrices[1];
uniform mat4 spotLightSpaceMatrices[10];
uniform float pointLightFarPlanes[20];

float sampleShadow(vec2 uv, float depth, vec4 rect, float bias) {
    vec2 texelSize = 1.0 / vec2(textureSize(shadowAtlas, 0));
    float shadow = 0.0;

    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            vec2 offset = vec2(x, y) * texelSize;
            vec2 atlasUV = clamp(
            rect.xy + uv * rect.zw + offset,
            rect.xy + texelSize,
            rect.xy + rect.zw - texelSize
            );

            float d = texture(shadowAtlas, atlasUV).r;
            shadow += (depth - bias > d) ? 1.0 : 0.0;
        }
    }
    return shadow / 9.0;
}

float PointShadowCalculation(
vec3 fragPos,
vec3 lightPos,
vec4 frontRect,
vec4 backRect,
float farPlane,
float bias
) {
    vec3 L = fragPos - lightPos;
    float dist = length(L);
    vec3 dir = normalize(L);

    const float seamWidth = 0.005;

    float frontW = smoothstep(-seamWidth, seamWidth, dir.z);
    float backW  = 1.0 - frontW;

    float shadow = 0.0;

    {
        vec3 d = dir;
        d.z = abs(d.z);

        float denom = 1.0 + d.z;
        vec2 uv = d.xy / denom * 0.5 + 0.5;

        if (uv.x >= 0.0 && uv.x <= 1.0 &&
        uv.y >= 0.0 && uv.y <= 1.0) {

            shadow += frontW *
            sampleShadow(uv, dist / farPlane, frontRect, bias);
        }
    }

    {
        vec3 d = dir;
        d.z = abs(d.z);

        float denom = 1.0 + d.z;
        vec2 uv = d.xy / denom * 0.5 + 0.5;

        if (uv.x >= 0.0 && uv.x <= 1.0 &&
        uv.y >= 0.0 && uv.y <= 1.0) {

            shadow += backW *
            sampleShadow(uv, dist / farPlane, backRect, bias);
        }
    }

    return shadow;
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

        // Calculate bias in light space units
        // Orthographic projection: depth is linear in light space
        float bias = max(0.005 * (1.0 - dot(Normal, lightDir)), 0.005);

        // OR use a fixed bias that works for your scene scale
        // float bias = 0.001; // Adjust based on scene scale

        float diff = max(dot(Normal, lightDir), 0.0);
        vec3 diffuse = diff * dirLightColours[i] * dirLightIntensities[i];

        vec4 fragPosLightSpace = dirLightSpaceMatrices[i] * vec4(FragPos, 1.0);

        // Transform to NDC [0,1] range
        vec3 proj = fragPosLightSpace.xyz / fragPosLightSpace.w;
        proj = proj * 0.5 + 0.5;

        float shadow = 0.0;
        if (proj.z <= 1.0 &&
        proj.x >= 0.0 && proj.x <= 1.0 &&
        proj.y >= 0.0 && proj.y <= 1.0) {

            shadow = sampleShadow(proj.xy, proj.z, dirLightRects[i], bias);
        }

        lighting += (1.0 - shadow) * diffuse;
    }

    // ----- Point Lights -----
    for (int i = 0; i < numPointLights; ++i) {
        vec3 lightDir = pointLightPositions[i] - FragPos;
        float distance = length(lightDir);
        lightDir = normalize(lightDir);

        float bias = max(0.005 * (1.0 - dot(Normal, lightDir)), 0.0005);
        bias *= distance / pointLightFarPlanes[i];

        float diff = max(dot(Normal, lightDir), 0.0);

        float attenuation = 1.0 /(pointLightConstants[i] +pointLightLinears[i] * distance +pointLightExponents[i] * distance * distance);

        float shadow = PointShadowCalculation(FragPos, pointLightPositions[i], pointLightFrontRects[i], pointLightBackRects[i], pointLightFarPlanes[i], bias);

        vec3 diffuse = diff * pointLightColours[i] * pointLightIntensities[i] * attenuation;

        lighting += (1.0 - shadow) * diffuse;
    }

    // ----- Spot Lights -----
    for (int i = 0; i < numSpotLights; ++i) {
        vec3 lightDir = spotLightPositions[i] - FragPos;
        float distance = length(lightDir);
        lightDir = normalize(lightDir);

        float bias = max(0.0005 * (1.0 - dot(Normal, lightDir)), 0.0005);

        float diff = max(dot(Normal, lightDir), 0.0);

        float theta = dot(lightDir, normalize(-spotLightDirections[i]));
        float epsilon = 0.1;
        float intensity = clamp((theta - spotLightCutoffs[i]) / epsilon, 0.0, 1.0);

        float attenuation = 1.0 / (spotLightConstants[i] +
        spotLightLinears[i] * distance +
        spotLightExponents[i] * distance * distance);

        vec3 diffuse = diff * spotLightColours[i] * spotLightIntensities[i] * attenuation * intensity;

        vec4 fragPosLightSpace =
        spotLightSpaceMatrices[i] * vec4(FragPos, 1.0);

        vec3 proj = fragPosLightSpace.xyz / fragPosLightSpace.w;
        proj = proj * 0.5 + 0.5;

        float shadow = 0.0;
        if (proj.z <= 1.0 &&
        proj.x >= 0.0 && proj.x <= 1.0 &&
        proj.y >= 0.0 && proj.y <= 1.0) {

            shadow = sampleShadow(
            proj.xy,
            proj.z,
            spotLightRects[i],
            bias
            );
        }

        lighting += (1.0 - shadow) * diffuse;
    }

    vec3 ambient = 0.05 * Albedo;

    FragColour = vec4(ambient + lighting * Albedo, 1.0);
}
