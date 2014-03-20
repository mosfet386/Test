package mosfet.game.gfx;

public class GameColor {
	
	//select the four colors used for tiles
	//each tile may have its own distinct 4 color set
	//the four game colors ranging from darkest color1 to lightest color4
	//darkest-lightest colors correspond to colors within .png sheet file
	//colors.get(a,b,c,d) usage: 555 full red,green,blue; 505 full red, 0 green,full blue
	public static int get(int color1, int color2, int color3, int color4){
		return (get(color4)<<24)+(get(color3)<<16)+(get(color2)<<8)+get(color1);
	}
	static {
		GameColor.get(555,543,542,123);
	}
	private static int get(int color) {
		//color<0 is invisible, set to 255
		if(color<0){return 255;}
		int r=color/100%10; // 555/100 5.55%10 0-5, always between 0-5
		int g=color/10%10; // 555/10 55.5%10 0-9, always between 0-9
		int b=color%10;	// 555 555%10 0-9, always between 0-9
		return r*36+g*6+b;
	}
}
