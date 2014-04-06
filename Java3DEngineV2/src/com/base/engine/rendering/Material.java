package com.base.engine.rendering;

import java.util.HashMap;

import com.base.engine.core.Vector3f;

public class Material {

	private HashMap<String,Texture> textureHashMap;
	private HashMap<String,Vector3f> vectorHashMap;
	private HashMap<String,Float> floatHashMap;

	public Material()
	{
		textureHashMap=new HashMap<String,Texture>();
		vectorHashMap=new HashMap<String,Vector3f>();
		floatHashMap=new HashMap<String,Float>();
	}
	
	public void addTexture(String name, Texture texture)
	{
		textureHashMap.put(name, texture);
	}
	
	public Texture getTexture(String name)
	{
		Texture result=textureHashMap.get(name);
		if(result!=null)
			return result;
		
		return new Texture("textgrid.png");
	}
	
	public void addVector(String name, Vector3f vector)
	{
		vectorHashMap.put(name, vector);
	}
	
	public Vector3f getVector(String name)
	{
		Vector3f result=vectorHashMap.get(name);
		if(result!=null)
			return result;
		
		return new Vector3f(0,0,0);
	}
	
	public void addFloat(String name, Float value)
	{
		floatHashMap.put(name, value);
	}
	
	public float getFloat(String name)
	{
		Float result=floatHashMap.get(name);
		if(result!=null)
			return result;
	
		return 0;
	}
}
