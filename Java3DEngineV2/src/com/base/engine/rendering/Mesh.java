package com.base.engine.rendering;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.util.ArrayList;
import java.util.HashMap;

import com.base.engine.core.Util;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.meshloading.IndexedModel;
import com.base.engine.rendering.meshloading.OBJModel;
import com.base.engine.rendering.resourceManagment.MeshResource;

public class Mesh 
{
	private String fileName;
	private MeshResource resource;
	private static HashMap<String,MeshResource> loadedModels=new HashMap<String,MeshResource>(); 
	
	
	public Mesh(String fileName)
	{
		this.fileName=fileName;
		MeshResource oldResource=loadedModels.get(fileName);
		if(oldResource!=null)
		{
			resource=oldResource;
			resource.addReference();
		}
		else
		{
			loadMesh(fileName);
			loadedModels.put(fileName,resource);
		}
	}
	
	public Mesh(Vertex[] vertices, int[] indices)
	{
		this(vertices,indices,false);
	}
	
	public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals)
	{
		fileName="";
		addVertices(vertices,indices,calcNormals);
	}	
	
	//this method is called by the garbage collector
	@Override
	protected void finalize()
	{
		if(resource.removeReference() && !fileName.isEmpty())
			loadedModels.remove(fileName);		
	}
	
	private void addVertices(Vertex[] vertices,int[] indices,boolean calcNormals)
	{
		if(calcNormals)
			calcNormals(vertices,indices);
		
		//the number of floats representing this vertices in 3Space
		resource=new MeshResource(indices.length);//*Vertex.SIZE;
		
		//use vbo as a buffer pointer
		glBindBuffer(GL_ARRAY_BUFFER,resource.getVbo());
		//add vertices to the start of the buffer pointer
		//Indicate this is an array, format the data for OpenGL, indicate data will not change
		glBufferData(GL_ARRAY_BUFFER,Util.createFlippedBuffer(vertices),GL_STATIC_DRAW);
		//indicate this is an array of indices for vertices
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,resource.getIbo());
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,Util.createFlippedBuffer(indices),GL_STATIC_DRAW);
	}
	
	public void draw()
	{
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		glBindBuffer(GL_ARRAY_BUFFER,resource.getVbo());
		//this buffer is attribute 0
		//using 3 objects, containing floats, comprised of Vertex.SIZE*4 bytes of data
		//with each object offset by 0
		glVertexAttribPointer(0,3,GL_FLOAT,false,Vertex.SIZE*4,0);
		glVertexAttribPointer(1,2,GL_FLOAT,false,Vertex.SIZE*4,12);
		glVertexAttribPointer(2,3,GL_FLOAT,false,Vertex.SIZE*4,20);
		
		//ibo is a buffer to indices used for drawing triangles
		//0 is the offset into the buffer
		//optimize to reuse vertices
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,resource.getIbo());
		glDrawElements(GL_TRIANGLES,resource.getSize(),GL_UNSIGNED_INT,0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
	}
	
	//to calculate lighting effects (spread)
	private void calcNormals(Vertex[] vertices,int[] indices)
	{
		for(int i=0;i<indices.length;i+=3){
			int i0=indices[i];
			int i1=indices[i+1];
			int i2=indices[i+2];
			Vector3f v1=vertices[i1].getPos().sub(vertices[i0].getPos());
			Vector3f v2=vertices[i2].getPos().sub(vertices[i0].getPos());
			Vector3f normal=v1.cross(v2).normalize();
			vertices[i0].setNormal(vertices[i0].getNormal().add(normal));
			vertices[i1].setNormal(vertices[i1].getNormal().add(normal));
			vertices[i2].setNormal(vertices[i2].getNormal().add(normal));
		}
		for(int i=0;i<vertices.length;i++){
			vertices[i].setNormal(vertices[i].getNormal().normalize());
		}
	}
	
	private Mesh loadMesh(String fileName)
	{
		String[] splitArray=fileName.split("\\.");
		String ext=splitArray[splitArray.length-1];
		
		if(!ext.equals("obj")){
			System.err.println("Error: File format not supported for mesh data: "+ext);
			System.err.println("Only .obj files are currently supported.");
			System.exit(1);
		}

		OBJModel test=new OBJModel("./res/models/"+fileName);
		IndexedModel model=test.toIndexedModel();
		model.calcNormals();
		
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		
		for(int i=0; i<model.getPositions().size(); i++)
		{
			vertices.add(new Vertex(model.getPositions().get(i),
									model.getTextureCoords().get(i),
									model.getNormals().get(i)));
		}
		
		Vertex[] vertexData=new Vertex[vertices.size()];
		vertices.toArray(vertexData);
		
		Integer[] indexData=new Integer[model.getIndices().size()];
		model.getIndices().toArray(indexData);
		
		addVertices(vertexData, Util.toIntArray(indexData),false);
		
//		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
//		ArrayList<Integer> indices=new ArrayList<Integer>();
//		
//		
//		BufferedReader meshReader=null;
//		try{
//			meshReader=new BufferedReader(new FileReader("./res/models/"+fileName));
//			String line;
//			while((line=meshReader.readLine())!=null){
//				String[] tokens=line.split(" ");
//				tokens=Util.removeEmptyStrings(tokens); //remove empty strings from each line
//				if(tokens.length==0||tokens[0].equals("#")){continue;} //comments and empty lines
//				else if(tokens[0].equals("v")){
//					vertices.add(new Vertex(new Vector3f(Float.valueOf(tokens[1]),
//														Float.valueOf(tokens[2]),
//														Float.valueOf(tokens[3]))));
//				}
//				else if(tokens[0].equals("f")){ //split to remove normal & texture order components
//					indices.add(Integer.parseInt(tokens[1].split("/")[0])-1);
//					indices.add(Integer.parseInt(tokens[2].split("/")[0])-1);
//					indices.add(Integer.parseInt(tokens[3].split("/")[0])-1);
//					if(tokens.length>4){ //triangulate quadralaters
//						indices.add(Integer.parseInt(tokens[1].split("/")[0])-1);
//						indices.add(Integer.parseInt(tokens[3].split("/")[0])-1);
//						indices.add(Integer.parseInt(tokens[4].split("/")[0])-1);
//					}
//				}
//			}
//			meshReader.close();
//			Vertex[] vertexData=new Vertex[vertices.size()];
//			vertices.toArray(vertexData);
//			Integer[] indexData=new Integer[indices.size()];
//			indices.toArray(indexData);
//			addVertices(vertexData, Util.toIntArray(indexData),true);
//		}
//		catch(Exception e){
//			e.printStackTrace();
//			System.exit(1);
//		}
		return null;
	}
}
