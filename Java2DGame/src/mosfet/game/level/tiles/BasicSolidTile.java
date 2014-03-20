package mosfet.game.level.tiles;

public class BasicSolidTile extends BasicTile{

	public BasicSolidTile(int id, int x, int y, int color, int levelMapColor) {
		super(id,x,y,color,levelMapColor);
		this.solid=true;
	}

}
