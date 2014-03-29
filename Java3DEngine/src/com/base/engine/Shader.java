//Write OpenGL shader program to the graphics card

package com.base.engine;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Shader {

	private int program;
	private HashMap<String,Integer> uniforms;
	
	public Shader(){
		//Acquire graphics card memory for a shader program
		program=glCreateProgram();
		if(program==0){
			System.err.println("Shader 'program creation' failed,"
								+ " couldn't find a vaild memory location!");
			System.exit(1);
		}
		uniforms=new HashMap<String,Integer>();
	}
	public void bind(){glUseProgram(program);}
	@SuppressWarnings("deprecation")
	public void compileShader(){
		glLinkProgram(program);
		if(glGetProgram(program,GL_LINK_STATUS)==0){
			System.err.println(glGetShaderInfoLog(program,1024));
			System.exit(1);
		}
		glValidateProgram(program);
		if(glGetProgram(program,GL_VALIDATE_STATUS)==0){
			System.err.println(glGetShaderInfoLog(program,1024));
			System.exit(1);
		}
	}
	@SuppressWarnings("deprecation")
	private void addProgram(String text,int type){
		//Acquire memory for this type of shader
		int shader=glCreateShader(type);
		if(shader==0){
			System.err.println("Shader 'creation' failed,"
					+ " couldn't find a vaild memory location!");
			System.exit(1);
		}
		glShaderSource(shader,text);
		glCompileShader(shader);
		if(glGetShader(shader,GL_COMPILE_STATUS)==0){
			System.err.println(glGetShaderInfoLog(shader,1024));
			System.exit(1);
		}
		//lastly, dispatch this shader program
		glAttachShader(program,shader);
	}
	public void addVertexShaderFromFile(String text){
		addProgram(loadShader(text),GL_VERTEX_SHADER);
	}
	public void addFragmentShaderFromFile(String text){
		addProgram(loadShader(text),GL_FRAGMENT_SHADER);
	}
	public void addGeometryShaderFromFile(String text){
		addProgram(loadShader(text),GL_GEOMETRY_SHADER);
	}
	public void addUniform(String uniform){
		int uniformLocation=glGetUniformLocation(program,uniform);
		if(uniformLocation==-1){
			System.err.println("Error: Couldn't find uniform "+uniform);
			new Exception().printStackTrace();
			System.exit(1);
		}
		uniforms.put(uniform,uniformLocation);
	}
	public void setUniformi(String uniformName,int value){
		glUniform1i(uniforms.get(uniformName),value);
	}
	public void setUniformf(String uniformName,float value){
		glUniform1f(uniforms.get(uniformName),value);
	}
	public void setUniform(String uniformName,Vector3f value){
		glUniform3f(uniforms.get(uniformName),value.getX(),value.getY(),value.getZ());
	}
	public void setUniform(String uniformName,Matrix4f value){
		//true for row major order, value[row][col]
		glUniformMatrix4(uniforms.get(uniformName),true,Util.createFlippedBuffer(value));
	}
	public void updateUniforms(Matrix4f worldMatrix,Matrix4f projectedMatrix,Material material){
		
	}
	private static String loadShader(String fileName){
		
		StringBuilder shaderSource=new StringBuilder();
		BufferedReader shaderReader=null;
		
		try{
			shaderReader=new BufferedReader(new FileReader("./res/shaders/"+fileName));
			String line;
			while((line=shaderReader.readLine())!=null){
				shaderSource.append(line).append("\n");
			}
			shaderReader.close();
		}
		catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		return shaderSource.toString();
	}
}
