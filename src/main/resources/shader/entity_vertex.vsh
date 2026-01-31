#version 400 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 textureCoords;
layout (location = 2) in vec3 normal;

out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec4 fragLVPos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 lightViewProjectionMatrix;

void main() {
    vec4 worldPos = transformationMatrix * vec4(position, 1.0);

    mat3 normalMatrix = transpose(inverse(mat3(transformationMatrix)));
    fragNormal = normalize(normalMatrix * normal);

    fragPos = worldPos.xyz;
    fragTextureCoord = textureCoords;
    fragLVPos = lightViewProjectionMatrix * worldPos;

    gl_Position = projectionMatrix * viewMatrix * worldPos;
}
