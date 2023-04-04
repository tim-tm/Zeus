#version 330 core
layout(location = 0) uniform vec2 position;
layout(location = 1) uniform vec2 size;
layout(location = 2) uniform float radius;
layout(location = 3) uniform float tickness;
layout(location = 4) uniform vec3 color;

float roundedRectangle (vec2 pos, vec2 size, float radius, float thickness)
{
    float d = length(max(abs(uv - pos),size) - size) - radius;
    return smoothstep(0.66, 0.33, d / thickness * 5.0);
}

void main() {
    vec3 col = mix(color, roundedRectangle(position, size, radius, thickness), 1.0f);
    gl_FragColor = vec4(col, 1.0);
}