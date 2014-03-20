package mosfet.game.level.tiles;

import mosfet.game.gfx.Screen;
import mosfet.game.level.Level;

public class BasicTile extends Tile{

	protected int tileId; //gets assigned manually for each tile in Tile Class
	protected int tileColor;
	
	@Override
	public void render(Screen screen, Level level, int x, int y) {
		screen.render(x,y,tileId,tileColor,0x00,1);
	}
	@Override
	public void tick(){}
	//tileColor: actual colors on tile
	//levelMapColor: index of level tile
	public BasicTile(int id, int x, int y, int tileColor, int levelMapColor) {
		super(id,false,false,levelMapColor);
		this.tileId=x+y*32;
		this.tileColor=tileColor;
	}

}
