#version 330

// Lebendiger Himmel auf der Grafikkarte: dieselbe Rechnung wie LiveSky.color(), aber fuer jeden Bildpunkt.
// Werte kommen aus einer kleinen Textur (Sampler0, 16x1), die Zeit aus UV0.

uniform sampler2D Sampler0;

in vec3 skyDir;
in vec4 skyInfo;

out vec4 fragColor;

#moj_import <neonlyrics:sky_common.glsl>

// Normale Himmel. DJ und Weltraumschlacht stehen in live_sky_fx.fsh (eigener Shader, wird erst bei Bedarf geladen).
vec3 freshSky(vec3 d, int id, float t) {
    if (id < 8) return calmSky(d, id, t);
    return fGrad(d.y, vec3(.30, .14, .36), vec3(.10, .07, .24), vec3(.03, .03, .10));
}
