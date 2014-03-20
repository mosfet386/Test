package mosfet.game.level;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import mosfet.game.entities.Entity;
import mosfet.game.gfx.Screen;
import mosfet.game.level.tiles.Tile;


public class Level {

	private byte[] tiles;
	public int width; //level width in 8x8 tiles
	public int height; //level height in 8x8 tiles
	//a list of entities to update movements and draw
	public List<Entity> entities=new ArrayList<Entity>();
	private String imagePath;
	private BufferedImage image;
	
	public void addEntity(Entity entity){
		this.entities.add(entity);
	}
	public Tile getTile(int x, int y) {
		if(x>=width||x<0||y>=height||y<0){return Tile.VOID;}
		else{return Tile.tiles[tiles[x+y*width]];}
	}
	public void renderEntities(Screen screen){for(Entity e:entities){e.render(screen);}}
	//xOffset,yOffset in pixels
	public void renderTiles(Screen screen,int xOffset, int yOffset){
		//limit offset to avoid drawing outside level borders
		if(xOffset<0){xOffset=0;}
		if(xOffset>((width<<3)-screen.width)){xOffset=((width<<3)-screen.width);}
		if(yOffset<0){yOffset=0;}
		if(yOffset>((height<<3)-screen.height)){xOffset=((height<<3)-screen.height);}
		screen.setOffset(xOffset,yOffset);
		for(int y=(yOffset>>3);y<(yOffset+screen.height>>3)+1;y++){
			for(int x=(xOffset>>3);x<(xOffset+screen.width>>3)+1;x++){
				getTile(x,y).render(screen,this,x<<3,y<<3);
			}
		}
//		for(int y=(yOffset>>3);y<((screen.height+yOffset)>>3+1);y++){
//			for(int x=(xOffset>>3);x<((screen.width+xOffset)>>3+1);x++){
//				getTile(x,y).render(screen,this,x<<3,y<<3);
//			}
//		}
	}
	public void tick(){
		//update data for all entities
		for(Entity e:entities){e.tick();}
		//update data for all tile types
		for(Tile t:Tile.tiles){
			if(t==null){break;}
			else{t.tick();}
		}
	}
	public void generateLevel(){
		//32x32 tiles window
		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){
				if(x*y%10<7){tiles[x+y*width]=Tile.GRASS.getid();}
				else{tiles[x+y*width]=Tile.STONE.getid();}
			}
		}
	}
	public void alterTile(int x, int y, Tile newTile){
		tiles[x+y*width]=newTile.getid();
		image.setRGB(x,y,newTile.getLevelMapColor());
	}
	private void saveLevelToFile(){
		try{
			ImageIO.write(image,"png",new File(Level.class.getResource(imagePath).getFile()));
		}catch(IOException e){e.printStackTrace();}
	}
	private void loadTiles(){
		int[] levelMapColors=this.image.getRGB(0,0,width,height,null,0,width);
		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){
				tileCheck: for(Tile t:Tile.tiles){
					if(t!=null && t.getLevelMapColor()==levelMapColors[x+y*width]){
						tiles[x+y*width]=t.getid();
						break tileCheck;
					}
				}
			}
		}
	}
	private void loadLevelFromFile(){
		try{
			image=ImageIO.read(Level.class.getResource(imagePath));
			width=image.getWidth();
			height=image.getHeight();
			tiles=new byte[width*height];
			loadTiles();
		}catch(IOException e){e.printStackTrace();}
	}
	public Level(String imagePath){
		if(imagePath!=null){
			this.imagePath=imagePath;
			loadLevelFromFile();
		}else{
			this.height=64;
			this.width=64;
			tiles=new byte[width*height];
			generateLevel();
		}

	}
}
