#version 400 core

layout (location = 0) in vec3 position;

uniform mat4 model;
out vec3 WorldPos;

void main() {
    WorldPos = vec3(model * vec4(position, 1.0));
    gl_Position = vec4(WorldPos, 1.0);
}


