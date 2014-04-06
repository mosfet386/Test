package com.base.engine.core;

import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Window;

public class CoreEngine {
		
	private boolean isRunning;
	private Game game;
	private RenderingEngine renderingEngine;
	private int width;
	private int height;
	private double frameTime;
	
	public CoreEngine(int width, int height, double frameRate, Game game)
	{
		this.width=width;
		this.height=height;
		this.game=game;
		this.isRunning=false;
		this.frameTime=1.0/frameRate;
	}
	
	public void createWindow(String title)
	{
		Window.createWindow(width,height,title);
		this.renderingEngine=new RenderingEngine();
	}
	
	public void start()
	{
		if(isRunning)
			return;
		run();
	}
	
	public void stop()
	{
		if(!isRunning)
			return;
		isRunning=false;
	}
	
	private void run()
	{
		isRunning=true;
		
		int frames=0;
		long frameCounter=0;
		game.init();

		double lastTime=Time.getTime();
		double unprocessedTime=0;//Seconds remaining to process game updates
		
		while(isRunning)
		{
			boolean render=false;
			
			double startTime=Time.getTime();
			double elapsedTime=startTime-lastTime;
			lastTime=startTime;
			
			unprocessedTime+=elapsedTime;
			frameCounter+=elapsedTime;
			
			//For every second of unprocessed time perform FRAME_CAP Updates
			//For slower systems this results in more time updating less time rendering
			//lower FPS
			while(unprocessedTime>frameTime)
			{
				if(Window.isCloseRequested()){stop();}
				
				render=true;
				unprocessedTime-=frameTime;
				
				game.input((float)frameTime);
				Input.update();
				
				game.update((float)frameTime);
				
				if(frameCounter>=1.0)
				{
					System.out.println(frames);
					frames=0;
					frameCounter=0;
				}
			}
			if(render)
			{
				game.render(renderingEngine);
				Window.render();
				frames++;
			}
			else{
				try {Thread.sleep(1);}
				catch (InterruptedException e){e.printStackTrace();}
			}
		}
		cleanup();
	}
	
	private void cleanup(){Window.dispose();}
	
}
