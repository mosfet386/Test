#version 330

in vec2 textureCoord0;
out vec4 fragColor;
uniform vec3 color; //to set specific color
uniform sampler2D sampler; //Where to read textures from, defaults to zero


void main()
{
	//combine the supplied texture's pixels with the supplied color
	fragColor=texture(sampler,textureCoord0.xy)*vec4(color,1);
}