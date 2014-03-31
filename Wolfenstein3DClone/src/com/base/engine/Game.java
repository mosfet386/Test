package com.base.engine;


public class Game {

	private static Level level;
	private static boolean isRunning;
	private static int levelNumber=0;
	
	public Game(){
		loadNextLevel();
	}
	
	public void input(){
		level.input();
	}
	
	public void update(){
		if(isRunning){level.update();}
	}
	
	public void render(){
		if(isRunning){level.render();}
	}

	public static Level getLevel(){
		return level;
	}
	
	//Is the player still alive
	public static void setIsRunning(boolean value)
	{
		isRunning=value;
	}
	
	public static void loadNextLevel()
	{
		levelNumber++;
		level=new Level("level"+levelNumber+".png","WolfCollection.png");
		
		Transform.setProjection(70,Window.getWidth(),Window.getHeight(),0.01f,1000f);
		Transform.setCamera(level.getPlayer().getCamera());
		isRunning=true;
	}
}
