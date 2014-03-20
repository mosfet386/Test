package mosfet.game.level.tiles;

import mosfet.game.gfx.Screen;
import mosfet.game.level.Level;

public class AnimatedTile extends BasicTile{

	private int[][] animationTileCoords;
	private int currentAnimationIndex;
	private long lastIterationTime;
	private int animationDelay;
	
	public void tick(){
		if((System.currentTimeMillis()-lastIterationTime)>=animationDelay){
			lastIterationTime=System.currentTimeMillis();
			currentAnimationIndex=(currentAnimationIndex+1)%animationTileCoords.length;
			tileId=animationTileCoords[currentAnimationIndex][0]+animationTileCoords[currentAnimationIndex][1]*32;
		}
	}
	public AnimatedTile(int id, int[][] animationCoords, int tileColor, 
								int levelMapColor,int animationDelay) {
		super(id,animationCoords[0][0],animationCoords[0][1],tileColor,levelMapColor);
		this.animationTileCoords=animationCoords;
		this.currentAnimationIndex=0;
		this.lastIterationTime=System.currentTimeMillis();
		this.animationDelay=animationDelay;
	}

}
