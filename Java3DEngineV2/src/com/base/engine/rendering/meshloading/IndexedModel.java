package com.base.engine.rendering.meshloading;

import java.util.ArrayList;

import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Vertex;

public class IndexedModel
{
	private ArrayList<Vector3f> positions;
	private ArrayList<Vector2f> textureCoords;
	private ArrayList<Vector3f> normals;
	private ArrayList<Integer> indices;
	
	public IndexedModel()
	{
		positions=new ArrayList<Vector3f>();
		textureCoords=new ArrayList<Vector2f>();
		normals=new ArrayList<Vector3f>();
		indices=new ArrayList<Integer>();
	}

	public ArrayList<Vector3f> getPositions(){return positions;}
	public ArrayList<Vector2f> getTextureCoords(){return textureCoords;}
	public ArrayList<Vector3f> getNormals(){return normals;}
	public ArrayList<Integer> getIndices(){return indices;}
	
	//to calculate lighting effects (spread)
	//normals of surfaces
	//is a rough approximation
	//approximates normals of surfaces with normals of vertices
	public void calcNormals()
	{
		for(int i=0;i<indices.size();i+=3)
		{
			int i0=indices.get(i);
			int i1=indices.get(i+1);
			int i2=indices.get(i+2);
			
			Vector3f v1=positions.get(i1).sub(positions.get(i0));
			Vector3f v2=positions.get(i2).sub(positions.get(i0));
			
			Vector3f normal=v1.cross(v2).normalize();
			
			normals.get(i0).set(normals.get(i0).add(normal));
			normals.get(i1).set(normals.get(i1).add(normal));
			normals.get(i2).set(normals.get(i2).add(normal));
		}
		for(int i=0;i<positions.size();i++){
			normals.get(i).set(normals.get(i).normalize());
		}
	}
}
