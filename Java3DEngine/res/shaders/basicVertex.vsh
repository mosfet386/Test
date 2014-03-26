#version 330

//use first configuration
//glEnableVertexAttribArray(0)
//take inputs into vec3 position
layout (location=0) in vec3 position;

//set color as vec4 output
out vec4 color;

uniform mat4 transform;

void main()
{
	//clamps the values of position between 0-1 // uniformFloat_sin(time)
	color=transform*vec4(clamp(position,0.0,1.0),1.0);
	
	//built in to place fragment on screen
	//transform * transpose(vec4)
    gl_Position = transform * vec4(position,1.0);
}
