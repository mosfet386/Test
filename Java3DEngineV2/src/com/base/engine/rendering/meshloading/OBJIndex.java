package com.base.engine.rendering.meshloading;

public class OBJIndex 
{
	public int vertexIndex;
	public int textureCoordIndex;
	public int normalIndex;
	
	@Override
	public boolean equals(Object obj)
	{
		OBJIndex index=(OBJIndex)obj;
		return vertexIndex==index.vertexIndex
				&&	textureCoordIndex==index.textureCoordIndex 
				&& 	normalIndex==index.normalIndex;		
	}
	
	@Override
	public int hashCode()
	{
		final int BASE=17;
		final int MULTIPLIER=31;
		int result=BASE;
		result=MULTIPLIER*result+vertexIndex;
		result=MULTIPLIER*result+textureCoordIndex;
		result=MULTIPLIER*result+normalIndex;
		
		return result;
	}
}
