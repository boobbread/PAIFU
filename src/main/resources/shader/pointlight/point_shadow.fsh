#version 400 core

in vec3 WorldPos;

uniform vec3 lightPos;
uniform float farPlane;
uniform int hemisphere;

void main() {
    vec3 L = WorldPos - lightPos;
    float dist = length(L);
    vec3 dir = normalize(L);

    if (hemisphere == 1)
    dir.z = -dir.z;

    float m = 2.0 / (1.0 + dir.z);
    vec2 uv = dir.xy * m * 0.5 + 0.5;

    gl_FragDepth = dist / farPlane;
}


