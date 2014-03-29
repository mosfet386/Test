package com.base.engine;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Mesh {

	private int vbo; //Vertex Buffer Object, array of vertices on graphics card
	private int ibo; //index Buffer Object, array of ints on graphics card
	private int size; //amount of data in mesh object
	
	public Mesh(String fileName){
		initMeshData();
		loadMesh(fileName);
	}
	public Mesh(Vertex[] vertices, int[] indices){
		this(vertices,indices,false);
	}
	public Mesh(Vertex[] vertices, int[] indices, boolean calcNormals){
		initMeshData();
		addVertices(vertices,indices,calcNormals);
	}
	private void initMeshData(){
		vbo=glGenBuffers(); //create OpenGL pointer, requires GL15
		ibo=glGenBuffers(); //indices of vertices
		size=0;
	}
	private void addVertices(Vertex[] vertices,int[] indices,boolean calcNormals){
		if(calcNormals){calcNormals(vertices,indices);}
		//the number of floats representing this vertices in 3Space
		size=indices.length;//*Vertex.SIZE;
		//use vbo as a buffer pointer
		glBindBuffer(GL_ARRAY_BUFFER,vbo);
		//add vertices to the start of the buffer pointer
		//Indicate this is an array, format the data for OpenGL, indicate data will not change
		glBufferData(GL_ARRAY_BUFFER,Util.createFlippedBuffer(vertices),GL_STATIC_DRAW);
		//indicate this is an array of indices for vertices
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,ibo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER,Util.createFlippedBuffer(indices),GL_STATIC_DRAW);
	}
	public void draw(){
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		
		glBindBuffer(GL_ARRAY_BUFFER,vbo);
		//this buffer is attribute 0
		//using 3 objects, containing floats, comprised of Vertex.SIZE*4 bytes of data
		//with each object offset by 0
		glVertexAttribPointer(0,3,GL_FLOAT,false,Vertex.SIZE*4,0);
		glVertexAttribPointer(1,2,GL_FLOAT,false,Vertex.SIZE*4,12);
		glVertexAttribPointer(2,3,GL_FLOAT,false,Vertex.SIZE*4,20);
		
		//ibo is a buffer to indices used for drawing triangles
		//0 is the offset into the buffer
		//optimize to reuse vertices
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,ibo);
		glDrawElements(GL_TRIANGLES,size,GL_UNSIGNED_INT,0);
		
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
	}
	//to calculate lighting effects (spread)
	private void calcNormals(Vertex[] vertices,int[] indices){
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
	private Mesh loadMesh(String fileName){
		String[] splitArray=fileName.split("\\.");
		String ext=splitArray[splitArray.length-1];
		if(!ext.equals("obj")){
			System.err.println("Error: File format not supported for mesh data: "+ext);
			System.err.println("Only .obj files are currently supported.");
			System.exit(1);
		}
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();
		
		
		BufferedReader meshReader=null;
		try{
			meshReader=new BufferedReader(new FileReader("./res/models/"+fileName));
			String line;
			while((line=meshReader.readLine())!=null){
				String[] tokens=line.split(" ");
				tokens=Util.removeEmptyStrings(tokens); //remove empty strings from each line
				if(tokens.length==0||tokens[0].equals("#")){continue;} //comments and empty lines
				else if(tokens[0].equals("v")){
					vertices.add(new Vertex(new Vector3f(Float.valueOf(tokens[1]),
														Float.valueOf(tokens[2]),
														Float.valueOf(tokens[3]))));
				}
				else if(tokens[0].equals("f")){ //split to remove normal & texture order components
					indices.add(Integer.parseInt(tokens[1].split("/")[0])-1);
					indices.add(Integer.parseInt(tokens[2].split("/")[0])-1);
					indices.add(Integer.parseInt(tokens[3].split("/")[0])-1);
					if(tokens.length>4){ //triangulate quadralaters
						indices.add(Integer.parseInt(tokens[1].split("/")[0])-1);
						indices.add(Integer.parseInt(tokens[3].split("/")[0])-1);
						indices.add(Integer.parseInt(tokens[4].split("/")[0])-1);
					}
				}
			}
			meshReader.close();
			Vertex[] vertexData=new Vertex[vertices.size()];
			vertices.toArray(vertexData);
			Integer[] indexData=new Integer[indices.size()];
			indices.toArray(indexData);
			addVertices(vertexData, Util.toIntArray(indexData),true);
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
}
