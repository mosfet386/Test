package com.base.engine.rendering.resourceManagment;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

public class TextureResource 
{
	private int id;
	private int refCount;
	
	public TextureResource(int id)
	{
		this.id=id;
		this.refCount=1;
	}
	
	//this method is called by the garbage collector
	@Override
	protected void finalize()
	{
		glDeleteBuffers(id);
	}
	
	public void addReference()
	{
		refCount++;
	}
	
	public boolean removeReference()
	{
		refCount--;
		if(refCount==0)
			return true;
					
		return false;
	}

	public int getId(){return id;}
}
