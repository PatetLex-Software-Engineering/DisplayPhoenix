#version 150

#moj_import <fog.glsl>
#moj_import <color.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 lightColor;
in vec2 texCoord0;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if ((color.r == 1 && color.g == 0 && color.b == 1) || ditherFog(vertexDistance, FogEnd - 8, FogEnd, gl_FragCoord)) {
        discard;
    }
    color = color * ColorModulator;
    if (color.r != 0 || color.g != 0 || color.b != 0) {
        color = shadeTranslucent(color, vertexColor, lightColor, vertexDistance, FogStart, FogEnd, FogColor);
    }

    fragColor = color;
}
