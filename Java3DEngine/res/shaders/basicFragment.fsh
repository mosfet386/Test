#version 330

in vec2 textureCoord0;
uniform vec3 color; //to set specific color
uniform sampler2D sampler; //Where to read textures from, defaults to zero


void main()
{
	vec4 textureColor=texture2D(sampler,textureCoord0.xy);
	if(textureColor==vec4(0,0,0,0)) //if no texture supplied just impose uniform color
		gl_FragColor=vec4(color,1);
	else
		gl_FragColor=textureColor*vec4(color,1);
}