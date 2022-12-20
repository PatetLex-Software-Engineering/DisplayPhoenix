#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if ((color.r == 1 && color.g == 0 && color.b == 1)) {
        discard;
    }
    color = color * vertexColor * ColorModulator;
	
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
}