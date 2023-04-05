#version 120
precision highp float;

uniform vec2 size;
uniform float radius;
uniform vec4 color;

float roundSDF(vec2 p, vec2 b, float r) {
   return length(max(abs(p) - b, 0.0)) - r;
}

void main() {
   vec2 rectHalf = size * .5;

   float distance = roundSDF(rectHalf - (gl_TexCoord[0].st * size), rectHalf - radius - 1., radius);
   float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, distance)) * color.a;

   gl_FragColor = vec4(color.rgb, smoothedAlpha);
}