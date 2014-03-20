package mosfet.game.entities;

import mosfet.game.level.Level;
import mosfet.game.level.tiles.Tile;

//Moving entities
public abstract class Mob extends Entity{

	protected String name;
	protected int speed;
	protected int numSteps=0;
	protected boolean isMoving;
	protected int movingDir=1; //0-Up,1-Down,2-Left,3-Right
	protected int scale=1;
	
	public String getName(){return name;}
	public abstract boolean hasCollided(int dx, int dy);
	protected boolean isSolidTile(int dx, int dy, int x, int y){
		if(level==null){return false;}
		//is the requested x,y solid?
		Tile currentTile=level.getTile((this.x+x)>>3,(this.y+y)>>3);
		Tile newTile=level.getTile((this.x+x+dx)>>3,(this.y+y+dy)>>3);
		if(!currentTile.equals(newTile) && newTile.isSolid()){return true;}
		else{return false;}
	}
	public void move(int dx, int dy){
		//avoid 2xSpeed for two simultaneous directions
		if(dx!=0 && dy!=0){
			move(dx,0);
			move(0,dy);
			numSteps--;
			return;
		}
		numSteps++;
		if(!hasCollided(dx,dy)){
			if(dy<0){movingDir=0;}
			if(dy>0){movingDir=1;}
			if(dx<0){movingDir=2;}
			if(dx>0){movingDir=3;}
			x+=dx*speed;
			y+=dy*speed;
		}
	}
	public Mob(Level level, String name, int x, int y, int speed) {
		super(level);
		this.name=name;
		this.x=x;
		this.y=y;
		this.speed=speed;
	}
}
