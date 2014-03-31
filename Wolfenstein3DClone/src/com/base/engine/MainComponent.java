package com.base.engine;

public class MainComponent {
	
	public static final int WIDTH=800;
	public static final int HEIGHT=600;
	public static final String TITLE="Java3DEngine";
	//Render up-to frame-cap regardless of how fast updates may occur
	public static final double FRAME_CAP=5000.0;
			
	private boolean isRunning;
	private Game game;
	
	public static void main(String args[]){
		Window.createWindow(WIDTH,HEIGHT,TITLE);
		MainComponent game=new MainComponent();
		game.start();
	}
	public MainComponent(){
		RenderUtility.initGraphics(); //Set OpenGL preferences
		isRunning=false;
		game=new Game();
	}
	public void start(){
		if(isRunning)
			return;
		run();
	}
	public void stop(){
		if(!isRunning)
			return;
		isRunning=false;
	}
	private void run(){
		
		isRunning=true;
		boolean shouldRender=false;
		int frames=0;
		long frameCounter=0;
		final double frameTime=1/FRAME_CAP;
		long lastTime=Time.getTime();
		double unprocessedTime=0;//Seconds remaining to process game updates
		
		while(isRunning){
			if(Window.isCloseRequested()){stop();}
			long startTime=Time.getTime();
			long elapsedTime=startTime-lastTime;
			lastTime=startTime;
			unprocessedTime+=elapsedTime/(double)Time.SECOND;
			frameCounter+=elapsedTime;
			//For every second of unprocessed time perform FRAME_CAP Updates
			//For slower systems this results in more time updating less time rendering
			//lower FPS
			while(unprocessedTime>frameTime){
				if(Window.isCloseRequested()){stop();}
				shouldRender=true;
				unprocessedTime-=frameTime;
				//Update Game
				Time.setDelta(frameTime);
				Input.update();
				game.input();
				game.update();
				if(frameCounter>=Time.SECOND)
				{
//					System.out.println(frames);
					frames=0;frameCounter=0;
				}
			}
			if(shouldRender){render();frames++;}
			else{
				try {Thread.sleep(1);}
				catch (InterruptedException e){e.printStackTrace();}
			}
		}
		cleanup();
	}
	private void render(){
		RenderUtility.clearScreen();
		game.render();
		Window.render();
	}
	private void cleanup(){Window.dispose();}

}
