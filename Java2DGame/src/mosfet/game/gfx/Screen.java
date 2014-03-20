package mosfet.game.gfx;


public class Screen {

	public static final int MAP_WIDTH=64;
	public static final int MAP_WIDTH_MASK=MAP_WIDTH-1;
	public static final byte BIT_MIRROR_X=0x01;
	public static final byte BIT_MIRROR_Y=0x02;
	public int[] pixels;//screen pixels
	public int xOffset=0;
	public int yOffset=0;
	public int width;
	public int height;
	public SpriteSheet sheet;
	
	public void render(int xPos, int yPos, int tile, int color, int mirrorDir, int scale){
		//int tile, index of pixel on spitesheet
		xPos-=xOffset;
		yPos-=yOffset;
		boolean mirrorX=(mirrorDir & BIT_MIRROR_X)>0;
		boolean mirrorY=(mirrorDir & BIT_MIRROR_Y)>0;
		int scaleMap=scale-1;
		int xTile=tile%32;
		int yTile=tile/32;
		int tileOffset=(xTile<<3)+(yTile<<3)*sheet.width;
		for(int y=0;y<8;y++){
			int ySheet=y;
			if(mirrorY){ySheet=7-y;}
			//account for tile scaling
			//tiles are scaled in multiples of unit tile size
			//scaled tiles are offset by half width&height to center
			//scaling can be applied to any individual tile
			int yPixel=y+yPos+(y*scaleMap)-((scaleMap<<3)/2);
			//x:position in tile xPos:offSet on screen
			for(int x=0;x<8;x++){
				int xSheet=x;
				if(mirrorX){xSheet=7-x;}
				int xPixel=x+xPos+(x*scaleMap)-((scaleMap<<3)/2);
				//255 in color bounds
				//col is the corresponding color from RGB-555-(6Rshades[0-255])..
				//6^3 colors 216
				int col=(color>>(sheet.pixels[xSheet+ySheet*sheet.width+tileOffset]*8))&255;
				//ignore 255 transparent color
				if(col<255){
					//keep tile from being drawn out of window bounds
					//screen will not be redrawn for these areas
					for(int yScale=0;yScale<scale;yScale++){
						if(yPixel+yScale<0||yPixel+yScale>=height){continue;}
						for(int xScale=0;xScale<scale;xScale++){
							if(xPixel+xScale<0||xPixel+xScale>=width){continue;}
							pixels[(xPixel+xScale)+(yPixel+yScale)*width]=col;
						}
					}
				}
			}
		}
	}
//	public void render(int xPos, int yPos, int tile, int color){
//		render(xPos,yPos,tile,color,0x00);
//	}
    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
	//multiple Screen objects with there own sheets
	public Screen(int width, int height, SpriteSheet sheet){
		this.width=width;
		this.height=height;
		this.sheet=sheet;
		pixels=new int[width*height]; //set to size of screen
	}
}
