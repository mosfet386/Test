package com.base.engine.rendering.meshloading;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.base.engine.core.Util;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;

public class OBJModel 
{
	private ArrayList<Vector3f> positions;
	private ArrayList<Vector2f> textureCoords;
	private ArrayList<Vector3f> normals;
	private ArrayList<OBJIndex> indices;
	private boolean hasTextureCoords;
	private boolean hasNormals;
	
	public OBJModel(String fileName)
	{
		positions=new ArrayList<Vector3f>();
		textureCoords=new ArrayList<Vector2f>();
		normals=new ArrayList<Vector3f>();
		indices=new ArrayList<OBJIndex>();
		hasTextureCoords=false;
		hasNormals=false;
		
		BufferedReader meshReader=null;
		try
		{
			meshReader=new BufferedReader(new FileReader(fileName));
			String line;
			while((line=meshReader.readLine())!=null)
			{
				String[] tokens=line.split(" ");
				tokens=Util.removeEmptyStrings(tokens); //remove empty strings from each line
				
				if(tokens.length==0||tokens[0].equals("#")) //comments and empty lines
					continue; 
				else if(tokens[0].equals("v"))
				{
					positions.add(new Vector3f(Float.valueOf(tokens[1]),
												Float.valueOf(tokens[2]),
												Float.valueOf(tokens[3])));
				}
				else if(tokens[0].equals("vt"))
				{
					textureCoords.add(new Vector2f(Float.valueOf(tokens[1]),
													Float.valueOf(tokens[2])));
				}
				else if(tokens[0].equals("vn"))
				{
					normals.add(new Vector3f(Float.valueOf(tokens[1]),
												Float.valueOf(tokens[2]),
												Float.valueOf(tokens[3])));
				}
				else if(tokens[0].equals("f")) //split to remove normal & texture order components
				{
					for(int i=0; i<tokens.length-3; i++)
					{
						indices.add(parseOBJIndex(tokens[1]));
						indices.add(parseOBJIndex(tokens[2+i]));
						indices.add(parseOBJIndex(tokens[3+i]));	
					}
				}
			}
			meshReader.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private OBJIndex parseOBJIndex(String token)
	{
		String[] values=token.split("/");
		
		OBJIndex result=new OBJIndex();
		result.vertexIndex=Integer.parseInt(values[0])-1;
		
		if(values.length>1)
		{
			hasTextureCoords=true;
			result.textureCoordIndex=Integer.parseInt(values[1])-1;
			if(values.length>2)
			{
				hasNormals=true;
				result.normalIndex=Integer.parseInt(values[2])-1;
			}
		}
		
		return result;
	}
	
	public IndexedModel toIndexedModel()
	{
		IndexedModel result=new IndexedModel();
		IndexedModel normalModel=new IndexedModel();
		HashMap<OBJIndex,Integer> resultIndexMap=new HashMap<OBJIndex,Integer>();
		HashMap<Integer,Integer> normalIndexMap=new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> indexMap=new HashMap<Integer,Integer>();
		
		for(int i=0; i<indices.size(); i++)
		{
			OBJIndex currentIndex=indices.get(i);
			Vector3f currentPosition=positions.get(currentIndex.vertexIndex);
			Vector2f currentTextureCoord;
			Vector3f currentNormal;
			
			if(hasTextureCoords)
				currentTextureCoord=textureCoords.get(currentIndex.textureCoordIndex);
			else
				currentTextureCoord=new Vector2f(0,0);
			
			if(hasNormals)
				currentNormal=normals.get(currentIndex.normalIndex);
			else
				currentNormal=new Vector3f(0,0,0);
			
			Integer modelVertexIndex=resultIndexMap.get(currentIndex);

			if(modelVertexIndex==null) //get index of node if it is not unique
			{
				//unoptimized mesh mapped to optimized mesh
				//unoptimized index - i
				//optimized index --- result.getPositions().size
				modelVertexIndex=result.getPositions().size();
				resultIndexMap.put(currentIndex,result.getPositions().size());
				
				result.getPositions().add(currentPosition);
				result.getTextureCoords().add(currentTextureCoord);
				if(hasNormals)
					result.getNormals().add(currentNormal);
			}
			
			Integer normalModelIndex=normalIndexMap.get(currentIndex.vertexIndex);
			
			if(normalModelIndex==null)
			{
				normalModelIndex=normalModel.getPositions().size();
				normalIndexMap.put(currentIndex.vertexIndex,normalModel.getPositions().size());
				
				normalModel.getPositions().add(currentPosition);
				normalModel.getTextureCoords().add(currentTextureCoord);
				normalModel.getNormals().add(currentNormal);
			}
			
			result.getIndices().add(modelVertexIndex);
			normalModel.getIndices().add(normalModelIndex);
			indexMap.put(modelVertexIndex, normalModelIndex);
		}
		
		if(!hasNormals)
		{
			normalModel.calcNormals();
			for(int i=0; i<result.getPositions().size(); i++)
			{
				result.getNormals().add(normalModel.getNormals().get(indexMap.get(i)));
			}
		}
		
		return result;
	}

//	public ArrayList<Vector3f> getPositions(){return positions;}
//	public ArrayList<Vector2f> getTextureCoords(){return textureCoords;}
//	public ArrayList<Vector3f> getNormals(){return normals;}
//	public ArrayList<OBJIndex> getIndices(){return indices;}
}



