package com.base.engine;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class Mesh {

	private int vbo; //Vertex Buffer Object, array of vertices on graphics card
	private int ibo; //index Buffer Object, array of ints on graphics card
	private int size; //amount of data in mesh object
	
	public Mesh(){
		vbo=glGenBuffers(); //create OpenGL pointer, requires GL15
		ibo=glGenBuffers(); //indices of vertices
		size=0;
	}
	public void addVertices(Vertex[] vertices,int[] indices){
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
		
			glBindBuffer(GL_ARRAY_BUFFER,vbo);
			//this buffer is defined by starting at 0,
			//using 3 objects, containing floats, comprised of Vertex.SIZE*4 bytes of data
			//with each object offset by 0
			glVertexAttribPointer(0,3,GL_FLOAT,false,Vertex.SIZE*4,0);
			
			//ibo is a buffer to indices used for drawing triangles
			//0 is the offset into the buffer
			//optimize to reuse vertices
			glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,ibo);
			glDrawElements(GL_TRIANGLES,size,GL_UNSIGNED_INT,0);
		
		glDisableVertexAttribArray(0);
	}
}
