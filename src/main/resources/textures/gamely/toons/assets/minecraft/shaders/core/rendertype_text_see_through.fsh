#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor;
    if (color.a < 0.1) {
        discard;
    }
    color.rgb *= color.rgb;
    color.rgb = color.rgb * 0.9 + vec3(0.07, 0.06, 0.1);
    color.rgb -= (1 - color.a) * 0.07;
    color.a = vertexColor.a;
    fragColor = color * ColorModulator;
}
