package com.base.engine.rendering;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.base.engine.core.Util;
import com.base.engine.rendering.resourceManagment.MeshResource;
import com.base.engine.rendering.resourceManagment.TextureResource;

public class Texture 
{

	private static final int BYTES_PER_PIXEL=4;
	private String fileName;
	private TextureResource resource;
	private static HashMap<String,TextureResource> loadedTextures=new HashMap<String,TextureResource>(); 
	
	public Texture(String fileName)
	{
		this.fileName=fileName;
		TextureResource oldResource=loadedTextures.get(fileName);
		if(oldResource!=null)
		{
			resource=oldResource;
			resource.addReference();
		}
		else
		{
			resource=new TextureResource(loadTexture(fileName));
			loadedTextures.put(fileName,resource);
		}
	}

	//this method is called by the garbage collector
	@Override
	protected void finalize()
	{
		if(resource.removeReference() && !fileName.isEmpty())
			loadedTextures.remove(fileName);		
	}
	
	public int getID(){return resource.getId();}
	
	public void bind(){
		glBindTexture(GL_TEXTURE_2D,resource.getId());
	}

	private static int loadTexture(String fileName)
	{
		String[] splitArray = fileName.split("\\.");
		String ext = splitArray[splitArray.length - 1];

		try
		{		
			BufferedImage image=ImageIO.read(new File("./res/textures/"+fileName));
			int[] pixels=image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
			
			ByteBuffer buffer=Util.createByteBuffer(BYTES_PER_PIXEL*image.getHeight()*image.getWidth());
			boolean hasAlpha=image.getColorModel().hasAlpha();
			
			for(int y=0; y<image.getHeight(); y++)
			{
				for(int x=0; x<image.getWidth(); x++)
				{
					int pixel=pixels[x+y*image.getWidth()];
					buffer.put((byte)((pixel >> 16) & 0xFF)); //red
					buffer.put((byte)((pixel >> 8) & 0xFF)); //green
					buffer.put((byte)(pixel & 0xFF)); //blue
					if(hasAlpha) 
						buffer.put((byte)((pixel >> 24) & 0xFF)); //alpha
					else
						buffer.put((byte)(0xFF)); //all ones otherwise
				}
			}
			buffer.flip();
			
			int id=glGenTextures();
			glBindTexture(GL_TEXTURE_2D,id);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			
			glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA8,image.getWidth(),
					image.getHeight(),0,GL_RGBA,GL_UNSIGNED_BYTE,buffer);
			
			return id;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		return 0;
	}
	
	//SlickUtil
//	private static int loadTexture(String fileName)
//	{
//		String[] splitArray = fileName.split("\\.");
//		String ext = splitArray[splitArray.length - 1];
//
//		try
//		{		
//			int id = TextureLoader.getTexture(ext, new FileInputStream(new File("./res/textures/" + fileName))).getTextureID();
//
//			return id;
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			System.exit(1);
//		}
//
//		return 0;
//	}

}
