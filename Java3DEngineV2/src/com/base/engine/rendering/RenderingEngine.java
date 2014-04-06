package com.base.engine.rendering;


import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_CW;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL32.GL_DEPTH_CLAMP;

import java.util.ArrayList;

import com.base.engine.components.BaseLight;
import com.base.engine.components.Camera;
import com.base.engine.core.GameObject;
import com.base.engine.core.Vector3f;

public class RenderingEngine 
{
	private Camera mainCamera;
	
	private Vector3f ambientLight;
	private ArrayList<BaseLight> lights;
	private BaseLight activeLight;
	
	public RenderingEngine()
	{
		lights=new ArrayList<BaseLight>();
		
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
		
//		mainCamera=new Camera((float)Math.toRadians(70.0f),
//				(float)Window.getWidth()/(float)Window.getHeight(),0.01f,1000.0f);		
		
		ambientLight=new Vector3f(0.1f,0.1f,0.1f);
	}
	
	public void render(GameObject object)
	{
		clearScreen();
		lights.clear();
		
		object.addToRenderingEngine(this);
		
		Shader forwardAmbient=ForwardAmbient.getInstance();
		
		object.render(forwardAmbient,this);
		
		//SETUP OPENGL FOR FORWARD LIGHTING
		//Allows colors to be blended by GL
		glEnable(GL_BLEND);
		//Sum the existing colors & new colors (individually multiplied by 1)
		glBlendFunc(GL_ONE,GL_ONE);
		//disable z axis culling (the depth test)
		//this is disabled since already performed during forward lighting
		glDepthMask(false);
		//Only performing lighting on pixels that make it into the image
		//GL_EQUALS, Perform lighting calculation if pixel has same depth value as
		//that nearest to the screen
		glDepthFunc(GL_EQUAL);
		
		for(BaseLight light : lights)
		{
			activeLight=light;
			object.render(light.getShader(),this);
		}
		
		glDepthFunc(GL_LESS);
		glDepthMask(true);
		glDisable(GL_BLEND);
	}
	
	private static void setTextures(boolean enabled)
	{
		if(enabled){glEnable(GL_TEXTURE_2D);}
		else{glDisable(GL_TEXTURE_2D);}
	}
	
	private static void unbindTextures(){
		glBindTexture(GL_TEXTURE_2D,0);
	}
	
	public static void clearScreen()
	{
		//Clear all colors from screen, and depth buffer
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
	}
	
	private static void setClearColor(Vector3f color)
	{
		glClearColor(color.getX(),color.getY(),color.getZ(),1.0f);
	}
	
	public Camera getMainCamera() 
	{
		return mainCamera;
	}

	public void setMainCamera(Camera mainCamera) 
	{
		this.mainCamera = mainCamera;
	}

	public Vector3f getAmbientLight()
	{
		return ambientLight;
	}
	
	public void addLight(BaseLight light)
	{
		lights.add(light);
	}
	
	public BaseLight getActiveLight()
	{
		return this.activeLight;
	}

	public void addCamera(Camera camera)
	{
		mainCamera=camera;
	}
	
	public static String getOpenGLVersion(){return glGetString(GL_VERSION);}
	
}
