#version 150

#moj_import <sphere.glsl>

in vec3 Position;
in vec4 Color;
in ivec2 UV2;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec4 ColorModulator;

out float vertexDistance;
flat out vec4 vertexColor;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(to_sphere(Position), 1.0);

    vertexDistance = length((ModelViewMat * vec4(Position, 1.0)).xyz);
    vertexColor = (Color * vec4(0.4, 0.15, 0.1, 1.0) + vec4(0.2, 0.05, 0.08, 0.0)) * ColorModulator * texelFetch(Sampler2, UV2 / 16, 0);
}
