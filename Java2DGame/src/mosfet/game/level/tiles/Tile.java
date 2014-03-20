package mosfet.game.level.tiles;

import mosfet.game.gfx.GameColor;
import mosfet.game.gfx.Screen;
import mosfet.game.level.Level;

public abstract class Tile {
	
	//tiles that represent window frame
	public static final Tile[] tiles=new Tile[256];
	public static final Tile VOID=new BasicSolidTile(0,0,0,GameColor.get(-1,-1,-1,-1),0xFF000000);
	public static final Tile STONE=new BasicSolidTile(1,1,0,GameColor.get(-1,333,-1,-1),0xFF555555);
	public static final Tile GRASS=new BasicTile(2,2,0,GameColor.get(-1,131,141,-1),0xFF00FF00);
	public static final Tile WATER=new AnimatedTile(3,new int[][]{{0,5},{1,5},{2,5},{1,5}},
													GameColor.get(-1,004,115,-1),0xFF0000FF,500);
	private int levelMapColor; //index of level tile
	
	protected byte id;
	protected boolean solid;
	protected boolean emitter; //for light
	

	public byte getid(){return id;}
	public boolean isSolid(){return solid;}
	public boolean isEmitter(){return emitter;}
	public int getLevelMapColor(){
		return levelMapColor;
	}
	public abstract void render(Screen screen, Level level, int x, int y);
	public abstract void tick();
	public Tile(int id, boolean isSolid, boolean isEmitter, int levelMapColor){
		this.id=(byte)id;
		if(tiles[id]!=null){throw new RuntimeException("Duplicate tile id="+id);}
		this.solid=isSolid;
		this.emitter=isEmitter;
		this.levelMapColor=levelMapColor; //index of level tile
		tiles[id]=this;
	}
}
