#version 150

const int ditherPattern[64] = int[64](0,32,8,40,2,34,10,42,48,16,56,24,50,18,58,26,12,44,4,36,14,46,6,38,60,28,52,20,62,30,54,22,3,35,11,43,1,33,9,41,51,19,59,27,49,17,57,25,15,47,7,39,13,45,5,37,63,31,55,23,61,29,53,21);

vec4 linear_fog_orig(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {
    if (vertexDistance <= fogStart) {
        return inColor;
    }

    float fogValue = vertexDistance < fogEnd ? smoothstep(fogStart, fogEnd, vertexDistance) : 1.0;
    return vec4(mix(inColor.rgb, fogColor.rgb, fogValue * fogColor.a), inColor.a);
}

vec4 linear_fog(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {
    if (vertexDistance <= fogStart) {
        return inColor;
    }

    float diff = fogEnd - fogStart;
    float fogValue = vertexDistance < fogEnd ? 1 - (fogEnd - vertexDistance) / diff : 1.0;
    fogValue = fogValue * fogValue * fogValue;
    /*float inColorValue = max(inColor.r, max(inColor.g, inColor.b)); // Affect bright pixels more than dark pixels
    fogValue = fogValue - 0.5 + inColorValue * 0.5;
    if (fogValue < 0) {
        return inColor;
    }*/
    return vec4(mix(inColor.rgb, fogColor.rgb, fogValue * fogColor.a), inColor.a);
}

float linear_fog_fade(float vertexDistance, float fogStart, float fogEnd) {
    if (vertexDistance <= fogStart) {
        return 1.0;
    } else if (vertexDistance >= fogEnd) {
        return 0.0;
    }

    return smoothstep(fogEnd, fogStart, vertexDistance);
}

bool ditherFog(float vertexDistance, float fogStart, float fogEnd, vec4 fragCoord) {
    //return vertexDistance > fogEnd;
    if (vertexDistance > fogEnd) {
        return true;
    }
    float ditherValue = ditherPattern[int(fragCoord.x)%8 * 8 + int(fragCoord.y)%8] / 64.0;
    float diff = fogEnd - fogStart;
    return vertexDistance + diff * ditherValue > fogEnd;
}

float fog_distance(mat4 modelViewMat, vec3 pos, int shape) {
    if (shape == 0) {
        return length((modelViewMat * vec4(pos, 1.0)).xyz);
    } else {
        float distXZ = length((modelViewMat * vec4(pos.x, 0.0, pos.z, 1.0)).xyz);
        float distY = length((modelViewMat * vec4(0.0, pos.y, 0.0, 1.0)).xyz);
        return max(distXZ, distY);
    }
}
