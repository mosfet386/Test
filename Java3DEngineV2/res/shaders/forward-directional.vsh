#version 120

//use first configuration
//glEnableVertexAttribArray(0)
//take inputs into vec3 position
attribute vec3 position;
attribute vec2 textureCoord;
attribute vec3 normal;

//world matrix, transform.getTransformation()
//translationMatrix.mul(rotationMatrix.mul(scaleMatrix));
uniform mat4 model;
//camera transformations
//projectionMatrix.mul(cameraRotation.mul(cameraTranslation.mul(transformationMatrix)));
uniform mat4 MVP;

varying vec3 worldPos0;
varying vec2 textureCoord0;
varying vec3 normal0;

void main()
{
	//built in to place fragment on screen
    gl_Position = MVP * vec4(position,1.0);
   
   	normal0=(model*vec4(normal,0.0)).xyz;
	worldPos0=(model*vec4(position,1.0)).xyz;
   
    //clamps the values of position between 0-1
	textureCoord0=textureCoord;
}
