#version 120

//no in or out varibles in gl 120
//varying or in for OpenGL v330
varying vec2 textureCoord0;

uniform vec3 ambientIntensity; //to set specific color
uniform sampler2D sampler; //Where to read textures from, defaults to zero


void main()
{
		gl_FragColor=texture2D(sampler,textureCoord0.xy)*vec4(ambientIntensity,1);
}