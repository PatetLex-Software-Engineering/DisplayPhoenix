#version 150

#moj_import <fog.glsl>

const vec4 matte = vec4(0.01, 0, 0.055, 0.0);
const vec4 mult = vec4(1.05, 1.07, 1.0, 1.0);

vec3 RGBtoHSV(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 RGBtoHSL(vec3 color) {
    float r = color.r; float g = color.g; float b = color.b;
    float M = max(r, max(g, b));
    float m = min(r, min(g, b));
    float c = M - m;
    float h; float s; float l;
    if (c == 0) {
        h = 0;
        s = 0;
        l = r;
    } else {
        if (M == r) {
            h = (g - b) / c;
            if (h < 0)
                h = h + 6;
        }
        else if (M == g) {
            h = (b - r) / c + 2;
        }
        else if (M == b) {
            h = (r - g) / c + 4;
        }
        h = h * 60;
        if (h < 0) {
            h = h + 360;
        }
        l = (M + m) / 2;
        s = c / (1 - abs(2 * l - 1));
    }
    return vec3(h / 360.0, s, l);
}

float hueToRGB(float p, float q, float t) {
    if (t < 0)
        t += 1.0;
    if (t > 1)
        t -= 1.0;
    if (t < 1.0/6.0)
        return p + (q - p) * 6.0 * t;
    if (t < 1.0/2.0)
        return q;
    if (t < 2.0/3.0)
        return p + (q - p) * (2.0/3.0 - t) * 6.0;
    return p;
}

vec3 HSVtoRGB(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

vec3 HSLtoRGB(vec3 color) {
    float h = color.x; float s = color.y; float l = color.z; 
    float r; float g; float b; float q; float p;
    if (s == 0) {
        r = l;
        g = l;
        b = l;
    } else {
        if (l < 0.5) {
            q = l * (1.0 + s);
        } else {
            q = l + s - l * s;
        }
        p = 2 * l - q;
        r = hueToRGB(p, q, h + 1.0/3.0);
        g = hueToRGB(p, q, h);
        b = hueToRGB(p, q, h - 1.0/3.0);
    }
    return vec3(r, g, b);
}

vec4 basicColorGrading(vec4 color) {
    color = color * mult + matte;
    return color;
}

vec4 shade(vec4 color, vec4 vertexColor, vec4 lightColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {
    /*float brightness = floor(pow((vertexColor.r + vertexColor.g + vertexColor.b + max(vertexColor.r, max(vertexColor.g, vertexColor.b))) * 5, 0.7)) * 0.2;
    brightness = pow(brightness, 0.7);
    vec4 shading = vec4((vertexColor.r * vertexColor.r) * 0.05 + vertexColor.r * 0.95, (vertexColor.g * vertexColor.g) * 0.15 + vertexColor.g * 0.85, vertexColor.b * 0.8 + 0.18, 1.0);
    shading = vec4(normalize(shading.rgb) * brightness, 1.0);
    color = color * shading;
    vec4 color2 = vec4(color.r * color.r * 3, color.g * color.g * 3, color.b * color.b * 3, color.a);
    color = (color * 0.6 + color2 * 0.4) * 0.9;
    color2 = vec4(sqrt(color.r), sqrt(color.g), sqrt(color.b), color.a);
    color = color * 0.8 + color2 * 0.2 + matte;
    return color;*/

    float vertexColorMax = max(vertexColor.r, max(vertexColor.g, vertexColor.b));
    vertexColor.r = vertexColor.r * 0.95 + 0.03;
    vertexColor.g = vertexColor.g;
    vertexColor.b = vertexColor.b * 0.8 + 0.12;
    vec3 hsl = RGBtoHSV(vertexColor.rgb * lightColor.rgb);
    hsl.z = floor(hsl.z * 11.89) / 12; 
    hsl.z = hsl.z * 0.6 + sqrt(hsl.z) * 0.4;
    hsl.z = hsl.z/* * hsl.z*/ * 1.15;
    if (hsl.z > 0.9) {
        hsl.z = 0.9;
    } else if (hsl.z < 0.08) {
        hsl.z = 0.08;
    }
    vertexColor = vec4(HSVtoRGB(hsl), 1.0);

    color = color * vertexColor;

    hsl = RGBtoHSL(color.rgb);
    hsl.y = hsl.y * ((1.3 - hsl.z) / 9 + 1); // Raise saturation of dark pixels
    hsl.y = hsl.y * (1.5 - vertexColorMax * 0.5); // Raise saturation of smooth lighting
    hsl.z = hsl.z * 1.15;
    /*if (hsl.z > 0.03) { // Toon lighting
        //hsl.z = sqrt(hsl.z);
        hsl.z = floor(hsl.z * 20.9 + 0.8) / 21; 
        //hsl.z = hsl.z * hsl.z;
    }*/
    hsl.z = (hsl.z * hsl.z) * 0.4 + hsl.z * 0.65; // Lower gamma, slightly raise contrast
    /*if (hsl.z <= 0.02) { 
        hsl.z = 0.02;
        hsl.y = hsl.y * 0.75;
    }
    else if (hsl.z <= 0.05) { 
        hsl.y = hsl.y * 0.9;
    }*/

    color = vec4(HSLtoRGB(hsl), color.a);
    color = basicColorGrading(color);
    color = linear_fog(color, vertexDistance, fogStart, fogEnd, fogColor); // Fog


    return color;
}

vec4 shadeTranslucent(vec4 color, vec4 vertexColor, vec4 lightColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {

    float vertexColorMax = max(vertexColor.r, max(vertexColor.g, vertexColor.b));
    float lightColorMax = max(lightColor.r, max(lightColor.g, lightColor.b));
    float shine = vertexDistance / fogEnd * lightColorMax;

    vertexColor.r = vertexColor.r * 0.95 + 0.03;
    vertexColor.g = vertexColor.g;
    vertexColor.b = vertexColor.b * 0.8 + 0.12;
    vec3 hsl = RGBtoHSV(vertexColor.rgb * lightColor.rgb);
    hsl.z = floor(hsl.z * 11.89) / 12; 
    hsl.z = hsl.z * 0.4 + sqrt(hsl.z) * 0.6;
    hsl.z = hsl.z * 1.15;
    if (hsl.z > 0.9) {
        hsl.z = 0.9;
    } else if (hsl.z < 0.08) {
        hsl.z = 0.08;
    }
    vertexColor = vec4(HSVtoRGB(hsl), 1.0);

    color = color * vertexColor;

    hsl = RGBtoHSL(color.rgb);
    hsl.y = hsl.y * ((1.3 - hsl.z) / 9 + 1); // Raise saturation of dark pixels
    hsl.y = hsl.y * (1.5 - vertexColorMax * 0.5); // Raise saturation of smooth lighting
    hsl.z = (hsl.z * hsl.z) * 0.4 + hsl.z * 0.6; // Lower gamma
    hsl.z = hsl.z + (hsl.z * shine);
    color = vec4(HSLtoRGB(hsl), color.a);

    color.g = color.g + (1 - color.g) * shine;
    color.b = color.b + (1 - color.b) * shine;
    color = basicColorGrading(color);
    color = linear_fog(color, vertexDistance, fogStart, fogEnd, fogColor); // Fog

    return color;
}