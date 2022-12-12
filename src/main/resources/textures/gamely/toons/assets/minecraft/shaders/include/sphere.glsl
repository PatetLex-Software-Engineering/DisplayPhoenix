#version 150

// World curvature, currently unused due to unresolved issues
vec3 to_sphere(vec3 position) {
    return position;
    /*float radius = length(position.xz);
    float angle = atan(position.z, position.x);
    return vec3(cos(angle) * radius * cos(radius / 500), position.y - (1 - cos(radius / 500)) * radius, sin(angle) * radius * cos(radius / 500));*/
}