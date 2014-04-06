#version 120

attribute vec3 position;
attribute vec2 textureCoord;

varying vec2 textureCoord0;

uniform mat4 MVP; //world matrix, M-model,V-View,P-projection

void main(){
	gl_Position=MVP*vec4(position,1.0);
	textureCoord0=textureCoord;
}