package com.base.engine;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

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
		//convert to RGB format, but already using RGB format
		//converts linear to exponential color format
		//glEnable(GL_FRAMEBUFFER_SRGB);
		//Enable texture drawing by OpenGL
		
		//prevents clipping when near rendered objects
		glEnable(GL_DEPTH_CLAMP);
		glEnable(GL_TEXTURE_2D);
	}
	public static String getOpenGLVersion(){return glGetString(GL_VERSION);}
	public static void setTextures(boolean enabled){
		if(enabled){glEnable(GL_TEXTURE_2D);}
		else{glDisable(GL_TEXTURE_2D);}
	}
	public static void setClearColor(Vector3f color){
		glClearColor(color.getX(),color.getY(),color.getZ(),1.0f);
	}
	public static void unbindTextures(){
		glBindTexture(GL_TEXTURE_2D,0);
	}
	
}
