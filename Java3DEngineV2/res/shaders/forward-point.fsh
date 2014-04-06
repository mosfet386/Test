#version 120
#include "lighting.glh"

varying vec3 worldPos0;
varying vec3 normal0;
varying vec2 textureCoord0;

uniform sampler2D diffuse; //Where to read textures from, defaults to zero
uniform PointLight pointLight;

void main()
{
    gl_FragColor=texture2D(diffuse,textureCoord0.xy)*calcPointLight(pointLight, normalize(normal0),worldPos0);
}