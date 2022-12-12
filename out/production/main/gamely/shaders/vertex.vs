#version 400 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 fragmentTextureCoordinates;
out vec3 fragmentNormal;
out vec3 fragmentPosition;
out vec3 toLights[%MAX_LIGHTS%];
out vec3 toCamera;
out float fogFactor;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform float renderDistance;
uniform vec3 lightsPosition[%MAX_LIGHTS%];

void main() {
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    for (int i = 0; i < %MAX_LIGHTS%; i++) {
        toLights[i] = lightsPosition[i] - worldPosition.xyz;
    }

    toCamera = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    float distance = length((viewMatrix * worldPosition).xyz);
    fogFactor = clamp(exp(-pow(((distance * 0.1) * renderDistance), 4.5)), 0.0, 1.0);

    fragmentNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
    fragmentPosition = worldPosition.xyz;
    fragmentTextureCoordinates = textureCoordinates;
}