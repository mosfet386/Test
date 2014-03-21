package mosfet.game.entities;

import mosfet.game.InputHandler;
import mosfet.game.gfx.Font;
import mosfet.game.gfx.GameColor;
import mosfet.game.gfx.Screen;
import mosfet.game.level.Level;

public class Player extends Mob{

	private InputHandler input;
	private int color=GameColor.get(-1,111,145,543);
	private int scale=1; //tile scaling 1=noScaling
	protected boolean isSwimming=false;
	private int tickCount=0;
	private String username;
	
	public String getUsername() {
		return this.username;
	}
	@Override
	public boolean hasCollided(int dx, int dy) {
		int xMin=0, xMax=7;
		int yMin=10, yMax=11;
		//check collision box parameter for solid tile intersection
		for(int x=xMin;x<xMax;x++){
			if(isSolidTile(dx,dy,x,yMin)){return true;}
			if(isSolidTile(dx,dy,x,yMax)){return true;}
		}
		for(int y=yMin;y<yMax;y++){
			if(isSolidTile(dx,dy,xMin,y)){return true;}
			if(isSolidTile(dx,dy,xMax,y)){return true;}
		}
		return false;
	}
	@Override
	public void render(Screen screen) {
		int xTile=0;
		int yTile=28;

		int animSpeed=3;//animation speed, not movement
		int flipTop=(numSteps>>animSpeed)&1;//animate every 16steps
		int flipBottom=(numSteps>>animSpeed)&1;//animate every 16steps
		if(movingDir==1){xTile+=2;}//moving down, base tile +2
		else if(movingDir>1){//moving left or right
			xTile+=4+((numSteps>>animSpeed)&1)*2;//base player tile +4 add 2 for anim
			flipTop=(movingDir-1)%2;
			flipBottom=(movingDir-1)%2;
		}
		
		//offset player
		int modifier = 8*scale;
		int xOffset=x-modifier/2;
		int yOffset=y-modifier/2;
		
		//draw player splashing animation
		if(isSwimming){
			int waterColor=0;
			yOffset+=4;
			if(tickCount%60<15){waterColor=GameColor.get(-1,-1,255,-1);}
			else if(15<=tickCount%60&&tickCount%60<30){yOffset-=1;waterColor=GameColor.get(-1,225,115,-1);}
			else if(30<=tickCount%60&&tickCount%60<45){waterColor=GameColor.get(-1,115,-1,225);}
			else {yOffset-=1;waterColor=GameColor.get(-1,225,115,-1);}
			screen.render(xOffset,yOffset+3,0+27*32,waterColor,0x00,1);
			screen.render(xOffset+8,yOffset+3,0+27*32,waterColor,0x01,1);
		}
		//draw the four player tiles
		//rendering upper half of player
		screen.render(xOffset+(modifier*flipTop),yOffset
				,xTile+yTile*32,color,flipTop,scale);
		screen.render(xOffset+modifier-(modifier*flipTop),yOffset
				,xTile+1+yTile*32,color,flipTop,scale);
		//rendering lower half of player
		if(!isSwimming){
			screen.render(xOffset+(modifier*flipBottom),yOffset+modifier
					,xTile+(yTile+1)*32,color,flipBottom,scale);
			screen.render(xOffset+modifier-(modifier*flipBottom),yOffset+modifier
					,xTile+1+(yTile+1)*32,color,flipBottom,scale);
		}		
		if(username!=null){
			Font.render(username,screen,xOffset-(username.length()-2)*8/2,yOffset-10,GameColor.get(-1,-1,-1,555),1);
		}
	}
	@Override
	public void tick() {
		int dx=0;
		int dy=0;
		//if input is null, then this is not the local player
		if(input!=null){
			//change player's position on screen
			if(input.up.isPressed()){dy--;}
			if(input.down.isPressed()){dy++;}
			if(input.left.isPressed()){dx--;}
			if(input.right.isPressed()){dx++;}
		}
		if(dx!=0 || dy!=0){move(dx,dy); isMoving=true;}
		else{isMoving=false;}
		//is the player on a water tile?
		if(level.getTile(this.x>>3,this.y>>3).getid()==3){isSwimming=true;}
		if(isSwimming && level.getTile(this.x>>3,this.y>>3).getid()!=3){isSwimming=false;}	
		tickCount++;
	}
	public Player(Level level, int x, int y, InputHandler input, String username) {
		super(level, "Player", x, y, 1);
		this.input=input;
		this.username=username;
	}

}
