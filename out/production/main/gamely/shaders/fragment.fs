#version 400 core

in vec2 fragmentTextureCoordinates;
in vec3 fragmentNormal;
in vec3 fragmentPosition;
in vec3 toLights[%MAX_LIGHTS%];
in vec3 toCamera;
in float fogFactor;

out vec4 fragmentColor;

uniform sampler2D textureAtlas;
uniform vec3 ambientLight;
uniform vec3 skyColor;
uniform float reflectance;
uniform vec4 lightsColor[%MAX_LIGHTS%];
uniform vec3 lightsAttenuation[%MAX_LIGHTS%];

void main() {
    vec3 normal = normalize(fragmentNormal);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);
    for (int i = 0; i < %MAX_LIGHTS%; i++) {
        float d = length(toLights[i]);
        float a = lightsAttenuation[i].x + (lightsAttenuation[i].y * d) + (lightsAttenuation[i].z * d * d);
        if (a == 0) {
            a = 1;
        }
        vec3 lightVector = normalize(toLights[i]);
        float nDot1 = dot(normal, lightVector);
        float brightness = max(nDot1, lightsColor[i].w);
        totalDiffuse = totalDiffuse + ((brightness * lightsColor[i].xyz) / a);

        vec3 reflectedLight = reflect(-lightVector, normal);
        float specularFactor = pow(max(dot(reflectedLight, normalize(toCamera)), lightsColor[i].w), 10);
        totalSpecular = totalSpecular + ((specularFactor * reflectance * lightsColor[i].xyz) / a);
    }
    vec4 lighting = (vec4(totalDiffuse, 1.0) + vec4(totalSpecular, 1.0));
    if (totalDiffuse == vec3(0.0)) {
        if (totalSpecular == vec3(0.0)) {
            lighting = vec4(1.0);
        }
    }

    fragmentColor = texture(textureAtlas, fragmentTextureCoordinates) * vec4(ambientLight, 1) * lighting;
    fragmentColor = mix(vec4(skyColor, 1.0), fragmentColor, fogFactor);
}