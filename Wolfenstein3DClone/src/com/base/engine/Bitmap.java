package com.base.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bitmap {

	private int width;
	private int height;
	private int[] pixels;
	
	public Bitmap(String fileName) {
		try{
			BufferedImage image=ImageIO.read(new File("./res/bitmaps/"+fileName));
			width=image.getWidth();
			height=image.getHeight();
			pixels=new int[width*height];
			image.getRGB(0,0,width,height,pixels,0,width);
		}catch (IOException e) {e.printStackTrace();}
	}
	
	public Bitmap(int width, int height) {
		this.width=width;
		this.height=height;
		this.pixels=new int[width*height];
	}
	
	public int getWidth() {return width;}
	
	public int getHeight() {return height;}
	
	public int[] getPixels() {return pixels;}
	
	public int getPixel(int x, int y) {return pixels[x+y*this.width];}
	
	public void setPixel(int x, int y, int value) {pixels[x+y*this.width]=value;}
	
	public Bitmap flipX() {
		int[] temp=new int[pixels.length];
		for(int x=0;x<width;x++){
			for(int y=0; y<height; y++){
				temp[x+y*width]=pixels[(width-x-1)+y*width];
			}
		}
		pixels=temp;
		return this;
	}
	public Bitmap flipY() {
		int[] temp=new int[pixels.length];
		for(int x=0;x<width;x++){
			for(int y=0; y<height; y++){
				temp[x+y*width]=pixels[x+(height-y-1)*width];
			}
		}
		pixels=temp;
		return this;
	}
}
