 //Write OpenGL shader program to the graphics card

package com.base.engine.rendering;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.naming.InitialContext;

import com.base.engine.core.Matrix4f;
import com.base.engine.core.Transform;
import com.base.engine.core.Util;
import com.base.engine.core.Vector3f;

public class Shader {

	private int program;
	private HashMap<String,Integer> uniforms;
	
	public Shader(String fileName)
	{
		//Acquire graphics card memory for a shader program
		program=glCreateProgram();
		if(program==0){
			System.err.println("Shader 'program creation' failed,"
								+ " couldn't find a vaild memory location!");
			System.exit(1);
		}
		uniforms=new HashMap<String,Integer>();
		
		String vertexShaderText=loadShader(fileName+".vsh");
		String fragmentShaderText=loadShader(fileName+".fsh");
		
		addVertexShader(vertexShaderText);
		addFragmentShader(fragmentShaderText);
		
		addAllAttributes(vertexShaderText);
		
		//Compile our defined shader programs for the GFX Card
		compileShader();
		
		addAllUniforms(vertexShaderText);
		addAllUniforms(fragmentShaderText);
	}
	
	public void bind(){glUseProgram(program);}
	
	@SuppressWarnings("deprecation")
	private void compileShader()
	{
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
	
	private void setAttribLocation(String attributeName, int location)
	{
		
		//required by versions of up to 200
		glBindAttribLocation(program,location,attributeName);
	}
	
	@SuppressWarnings("deprecation")
	private void addProgram(String text,int type)
	{
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
	
	private void addVertexShader(String text)
	{
		addProgram(text,GL_VERTEX_SHADER);
	}
	
	private void addFragmentShader(String text)
	{
		addProgram(text,GL_FRAGMENT_SHADER);
	}
	
	//Allows attributes to be read directly from OpenGL shader files
	private void addAllAttributes(String shaderText)
	{
		final String ATTRIBUTE_KEYWORD="attribute";
		int attributeStartLocation=shaderText.indexOf(ATTRIBUTE_KEYWORD);
		int attributeNumber=0;
		while(attributeStartLocation!=-1)
		{
			//should have whitespace before & after attribute keyword
			if(!(attributeStartLocation!=0
					&& (Character.isWhitespace(shaderText.charAt(attributeStartLocation-1)) || shaderText.charAt(attributeStartLocation-1)==';')
					&& Character.isWhitespace(shaderText.charAt(attributeStartLocation+ATTRIBUTE_KEYWORD.length()))))
				continue;
			
			int begin=attributeStartLocation+ATTRIBUTE_KEYWORD.length()+1;
			int end=shaderText.indexOf(';',begin);
			String attributeLine=shaderText.substring(begin,end).trim();
			String attributeName=attributeLine.substring(attributeLine.indexOf(' ')+1,attributeLine.length()).trim();

			//required by versions of up to 200
			//this is set inside the shader for newer versions
			setAttribLocation(attributeName,attributeNumber);
			attributeNumber++;
			
			attributeStartLocation=shaderText.indexOf(
					ATTRIBUTE_KEYWORD,attributeStartLocation+ATTRIBUTE_KEYWORD.length());
		}
	}
	
	//Allows uniforms to be read directly from OpenGL shader files
	private void addAllUniforms(String shaderText)
	{
		HashMap<String,ArrayList<GLSLStruct>> structs=findUniformStructs(shaderText);
		
		final String UNIFORM_KEYWORD="uniform";
		int uniformStartLocation=shaderText.indexOf(UNIFORM_KEYWORD);
		while(uniformStartLocation!=-1)
		{
			//should have whitespace before & after struct keyword
			if(!(uniformStartLocation!=0
					&& (Character.isWhitespace(shaderText.charAt(uniformStartLocation-1)) || shaderText.charAt(uniformStartLocation-1)==';')
					&& Character.isWhitespace(shaderText.charAt(uniformStartLocation+UNIFORM_KEYWORD.length()))))
				continue;
			
			int begin=uniformStartLocation+UNIFORM_KEYWORD.length()+1;
			int end=shaderText.indexOf(';',begin);
			String uniformLine=shaderText.substring(begin,end);
			
			int whiteSpacePos=uniformLine.indexOf(' ');
			String uniformName=uniformLine.substring(whiteSpacePos+1,uniformLine.length()).trim();
			String uniformType=uniformLine.substring(0,whiteSpacePos).trim();
			
			addUniformWithStructCheck(uniformName,uniformType,structs);
			
			uniformStartLocation=shaderText.indexOf(
					UNIFORM_KEYWORD,uniformStartLocation+UNIFORM_KEYWORD.length());
		}
	}
	
	private void addUniformWithStructCheck(String uniformName, 
			String uniformType, HashMap<String,ArrayList<GLSLStruct>> structs)
	{
		boolean addThis=true;
		ArrayList<GLSLStruct> structComponents=structs.get(uniformType);
	
		if(structComponents!=null)
		{
			//for every struct variable append variable name to struct name
			//struct.variable
			addThis=false;
			for(GLSLStruct struct : structComponents)
			{
				addUniformWithStructCheck(uniformName+"."+struct.name,struct.type,structs);
			}
		}
		if(addThis)
			addUniform(uniformName);
	}
	
	//works with addAllUniforms to pull uniforms directly from shader files
	private HashMap<String,ArrayList<GLSLStruct>> findUniformStructs(String shaderText)
	{
		HashMap<String,ArrayList<GLSLStruct>> resultHashMap=new HashMap<String,ArrayList<GLSLStruct>>();
		
		//Loop for each stuct in file
		final String STRUCT_KEYWORD="struct";
		int structStartLocation=shaderText.indexOf(STRUCT_KEYWORD);
		while(structStartLocation!=-1)
		{
			//should have whitespace before & after struct keyword
			if(!(structStartLocation!=0
					&& (Character.isWhitespace(shaderText.charAt(structStartLocation-1)) || shaderText.charAt(structStartLocation-1)==';')
					&& Character.isWhitespace(shaderText.charAt(structStartLocation+STRUCT_KEYWORD.length()))))
				continue;
			
			int nameBegin=structStartLocation+STRUCT_KEYWORD.length()+1;
			int braceBegin=shaderText.indexOf('{',nameBegin);
			int braceEnd=shaderText.indexOf('}',braceBegin);
			
			String structName=shaderText.substring(nameBegin,braceBegin).trim();

			//Loop for each variable in struct
			ArrayList<GLSLStruct> structComponent=new ArrayList<GLSLStruct>();
			int componentSemicolonPos=shaderText.indexOf(';',braceBegin);
			while(componentSemicolonPos!=-1 && componentSemicolonPos<braceEnd)
			{
				GLSLStruct currentGLSLStruct=new GLSLStruct();
				int componentNameStart=componentSemicolonPos;
				while(!Character.isWhitespace(
						shaderText.charAt(componentNameStart - 1)))
					 componentNameStart--;
				
				int componentTypeEnd = componentNameStart - 1;
				int componentTypeStart = componentTypeEnd;

				while(!Character.isWhitespace(shaderText.charAt(componentTypeStart - 1)))
					componentTypeStart--;
				
				String componentName=shaderText.substring(
						componentNameStart, componentSemicolonPos);
				String componentType=shaderText.substring(
						componentTypeStart, componentTypeEnd);
				currentGLSLStruct.name=componentName;
				currentGLSLStruct.type=componentType;
				
				structComponent.add(currentGLSLStruct);
				System.out.println(componentName);
				System.out.println(componentType);
				
				componentSemicolonPos=shaderText.indexOf(';',componentSemicolonPos+1);
			}
			
			resultHashMap.put(structName,structComponent);
			
			structStartLocation=shaderText.indexOf(
					STRUCT_KEYWORD,structStartLocation+STRUCT_KEYWORD.length());
		}
		
		return resultHashMap;
	}
	
	private class GLSLStruct
	{
		public String name;
		public String type;
	}
	
	private void addUniform(String uniform)
	{
		int uniformLocation=glGetUniformLocation(program,uniform);
		if(uniformLocation==-1){
			System.err.println("Error: Couldn't find uniform "+uniform);
			new Exception().printStackTrace();
			System.exit(1);
		}
		uniforms.put(uniform,uniformLocation);
	}
	
	public void setUniformi(String uniformName,int value)
	{
		glUniform1i(uniforms.get(uniformName),value);
	}
	
	public void setUniformf(String uniformName,float value)
	{
		glUniform1f(uniforms.get(uniformName),value);
	}
	
	public void setUniform(String uniformName,Vector3f value)
	{
		glUniform3f(uniforms.get(uniformName),value.getX(),value.getY(),value.getZ());
	}
	
	public void setUniform(String uniformName,Matrix4f value)
	{
		//true for row major order, value[row][col]
		glUniformMatrix4(uniforms.get(uniformName),true,Util.createFlippedBuffer(value));
	}
	
	public void updateUniforms(Transform transform, Material material, 
										RenderingEngine renderingEngine)
	{
		
	}
	
	private static String loadShader(String fileName)
	{
		
		StringBuilder shaderSource=new StringBuilder();
		BufferedReader shaderReader=null;
		final String INCLUDE_DIRECTIVE="#include";
		
		try{
			shaderReader=new BufferedReader(new FileReader("./res/shaders/"+fileName));
			String line;
			while((line=shaderReader.readLine())!=null){
				if(line.startsWith(INCLUDE_DIRECTIVE))
				{
					String includeFile=line.substring(INCLUDE_DIRECTIVE.length()+2,line.length()-1);
					shaderSource.append(loadShader(includeFile));
				}
				else
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
