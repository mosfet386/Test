package com.base.engine.rendering;

import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;

public class Vertex {

	public static final int SIZE=8;
	private Vector3f pos;
	private Vector2f textureCoord;
	private Vector3f normal;
	
	public Vertex(Vector3f pos){this(pos,new Vector2f(0,0));}
	public Vertex(Vector3f pos,Vector2f textureCoord,Vector3f normal){
		this.pos=pos;
		this.textureCoord=textureCoord;
		this.normal=normal;
	}
	public Vertex(Vector3f pos,Vector2f textureCoord){
		this(pos,textureCoord,new Vector3f(0,0,0));
	}
	public Vector3f getPos(){return pos;}
	public void setPos(Vector3f pos){this.pos = pos;}
	public Vector2f getTextureCoord(){return textureCoord;}
	public void setTextureCoord(Vector2f textureCoord){this.textureCoord=textureCoord;}
	public Vector3f getNormal(){return normal;}
	public void setNormal(Vector3f normal){this.normal = normal;}
}
