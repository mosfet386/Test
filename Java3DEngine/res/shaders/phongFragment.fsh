#version 330

const int MAX_POINT_LIGHTS=4;
const int MAX_SPOT_LIGHTS=4;
struct BaseLight{
	vec3 color;
	float intensity;
};
struct DirectionalLight{
	BaseLight base;
	vec3 direction; //direction oriented away from pont of reflection
};
struct Attenuation{
	float constant;
	float linear;
	float exponent;
};
struct PointLight{
	BaseLight base;
	Attenuation atten; //directional lighting does not require this
	vec3 position;
	float range;
};
struct SpotLight{
	PointLight pointLight;
	vec3 direction;
	float cutoff;
};

in vec3 worldPos0;
in vec3 normal0;
in vec2 textureCoord0;

uniform vec3 eyePos;
uniform vec3 baseColor; //to set specific color
uniform sampler2D sampler; //Where to read textures from, defaults to zero
uniform vec3 ambientLight; //additional background light
uniform DirectionalLight directionalLight;
uniform float specularIntensity; //the reflection intensity
uniform float specularPower; //exponentiate specular factor (cos), effects reflection width
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

out vec4 fragColor;

vec4 calcLight(BaseLight base,vec3 direction,vec3 normal){
	float diffuseFactor=dot(normal,-direction);
	vec4 diffuseColor=vec4(0,0,0,0);
	vec4 specularColor=vec4(0,0,0,0);
	if(diffuseFactor>0){ //reflected light is observed
		diffuseColor=vec4(base.color,1.0)*base.intensity*diffuseFactor;
		vec3 directionToEye=normalize(eyePos-worldPos0);
		vec3 reflectDirection=normalize(reflect(direction,normal));
		float specularFactor=dot(directionToEye,reflectDirection);
		specularFactor=pow(specularFactor,specularPower);
		if(specularFactor>0){specularColor=vec4(base.color,1.0)*specularIntensity*specularFactor;}
	}
	return diffuseColor+specularColor;
}
vec4 calcDirectionalLight(DirectionalLight directionalLight,vec3 normal){
	return calcLight(directionalLight.base,-directionalLight.direction,normal);
}
vec4 calcPointLight(PointLight pointLight,vec3 normal){
	vec3 lightDirection=worldPos0-pointLight.position;
	float distanceToPoint=length(lightDirection);
	if(distanceToPoint>pointLight.range){return vec4(0,0,0,0);}
	lightDirection=normalize(lightDirection);
	vec4 color=calcLight(pointLight.base,lightDirection,normal);
	float attenuation=	pointLight.atten.constant+
						pointLight.atten.linear*distanceToPoint+
						pointLight.atten.exponent*distanceToPoint*distanceToPoint
						+0.0001; //to prevent divide by 0, GPU will evaluate an if statement
						//to optimize, so this is a safty measure
	return color/attenuation;
}
vec4 calcSpotLight(SpotLight spotLight,vec3 normal){
	vec3 lightDirection=normalize(worldPos0-spotLight.pointLight.position);
	float spotFactor=dot(lightDirection,spotLight.direction);
	vec4 color=vec4(0,0,0,0);
	if(spotFactor>spotLight.cutoff){
		color=calcPointLight(spotLight.pointLight,normal)*
						(1-(1.0-spotFactor)/(1.0-spotLight.cutoff));
	}
	return color;
}

void main()
{
	vec4 color=vec4(baseColor,1);
	vec4 textureColor=texture2D(sampler,textureCoord0.xy);

	//add ambient light color
	vec4 totalLight=vec4(ambientLight,1);

	//add texture light color
	if(textureColor!=vec4(0,0,0,0)) //if textureColor supplied, multiply it by color
		color*=textureColor;

	//add directional light color
	vec3 normal=normalize(normal0);
	totalLight+=calcDirectionalLight(directionalLight,normal);

	//add point light(s) color //OpenGL expects a constant for #of loops
	for(int i=0;i<MAX_POINT_LIGHTS;i++){
		if(pointLights[i].base.intensity>0) //ignore lights not implimented
  			totalLight+=calcPointLight(pointLights[i],normal);
	}

	//add spot light(s) color
	for(int i=0;i<MAX_SPOT_LIGHTS;i++){
		if(spotLights[i].pointLight.base.intensity>0)
			totalLight+=calcSpotLight(spotLights[i],normal);
	}

	fragColor=color*totalLight;
}