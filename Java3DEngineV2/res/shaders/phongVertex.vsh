#version 330

//use first configuration
//glEnableVertexAttribArray(0)
//take inputs into vec3 position
layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoord;
layout (location=2) in vec3 normal;

//world matrix, transform.getTransformation()
//translationMatrix.mul(rotationMatrix.mul(scaleMatrix));
uniform mat4 transform;
//camera transformations
//projectionMatrix.mul(cameraRotation.mul(cameraTranslation.mul(transformationMatrix)));
uniform mat4 transformProjected;


out vec3 worldPos0;
out vec2 textureCoord0;
out vec3 normal0;

void main()
{
	//built in to place fragment on screen
    gl_Position = transformProjected * vec4(position,1.0);
   
   	normal0=(transform*vec4(normal,0.0)).xyz;
	worldPos0=(transform*vec4(position,1.0)).xyz;
   
    //clamps the values of position between 0-1 // uniformFloat_sin(time)
	textureCoord0=textureCoord;
}
