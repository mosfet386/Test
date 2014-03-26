package com.base.engine;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class RenderUtility {

	public static void clearScreen(){
		//Clear all colors from screen, and depth buffer
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
	}
	//Set this 3D engine's default state
	public static void initGraphics(){
		//values are floats between 0-1
		glClearColor(0.0f,0.0f,0.0f,0.0f); //color used to clear screen (BLACK)
		
		//if objects do not appear, check that vertex order is clockwise
		glFrontFace(GL_CW); //order that faces are drawn is clockwise
		glCullFace(GL_BACK); //face that could be culled
		glEnable(GL_CULL_FACE); //require manual creation of object back faces
		
		//When new pixels are created, keep track of depth order
		glEnable(GL_DEPTH_TEST);
		
		//Open GL3.0 feature, GAMMA correction, otherwise dark colors
		glEnable(GL_FRAMEBUFFER_SRGB);
	}
	public static String getOpenGLVersion(){return glGetString(GL_VERSION);}
	
}
