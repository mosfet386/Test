package com.base.engine.rendering.resourceManagment;
import static org.lwjgl.opengl.GL15.*;

public class MeshResource 
{
	private int vbo; //Vertex Buffer Object, array of vertices on graphics card
	private int ibo; //index Buffer Object, array of ints on graphics card
	private int size; //amount of data in mesh object
	private int refCount;
	
	public MeshResource(int size)
	{
		vbo=glGenBuffers();
		ibo=glGenBuffers();
		this.size=size;
		this.refCount=1;
	}
	
	//this method is called by the garbage collector
	@Override
	protected void finalize()
	{
		glDeleteBuffers(vbo);
		glDeleteBuffers(ibo);
		size=0;
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

	public int getVbo(){return vbo;}

	public int getIbo(){return ibo;}

	public int getSize(){return size;}

	public void setSize(int size){this.size = size;}
}
