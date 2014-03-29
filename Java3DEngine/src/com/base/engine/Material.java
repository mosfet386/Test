package com.base.engine;

public class Material {

	private Texture texture;
	private Vector3f color;
	private float specularIntensity;
	private float specularPower; //reflection width cos^specularPower
	
	public Material(Texture texture){this(texture,new Vector3f(1,1,1));}
	public Material(Texture texture, Vector3f color){
		this(texture,color,2,32);
	}
	public Material(Texture texture,Vector3f color,
						float specularIntensity,float specularExponent){
		this.texture=texture;
		this.color=color;
		this.specularIntensity=specularIntensity;
		this.specularPower=specularExponent;
	}
	public Texture getTexture(){return texture;}
	public Vector3f getColor(){return color;}
	public void setTexture(Texture texture){this.texture = texture;}
	public void setColor(Vector3f color){this.color = color;}
	public float getSpecularIntensity(){return specularIntensity;}
	public float getSpecularPower(){return specularPower;}
	public void setSpecularIntensity(float specularIntensity){this.specularIntensity = specularIntensity;}
	public void setSpecularPower(float specularExponent){this.specularPower=specularExponent;}
}
