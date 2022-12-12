#version 150

#moj_import <fog.glsl>
#moj_import <color.glsl>

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;

out vec4 fragColor;

void main() {
    fragColor = linear_fog_orig(ColorModulator, sqrt(vertexDistance), 0, 10, FogColor);
}
