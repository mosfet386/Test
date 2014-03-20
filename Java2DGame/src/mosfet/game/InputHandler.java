package mosfet.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class InputHandler implements KeyListener{

	public class Key{
		private boolean pressed=false;
		private int numTimesPressed=0;
		public void toggle(boolean isPressed){
			pressed=isPressed;
			if(pressed){numTimesPressed++;}
		}
		public boolean isPressed(){
			return pressed;
		}
		public int getNumTimesPressed(){
			return numTimesPressed;
		}
	}
	public Key up=new Key();
	public Key down=new Key();
	public Key left=new Key();
	public Key right=new Key();
	
	//this constructed will add the key listener to the main class frame
	public InputHandler(Game game){
		game.addKeyListener(this);
	}
	@Override
	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(),true);
	}
	@Override
	public void keyReleased(KeyEvent e) {
		toggleKey(e.getKeyCode(),false);
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	public void toggleKey(int keyCode, boolean isPressed){
		if(keyCode==KeyEvent.VK_W||keyCode==KeyEvent.VK_UP){up.toggle(isPressed);}
		if(keyCode==KeyEvent.VK_A||keyCode==KeyEvent.VK_LEFT){left.toggle(isPressed);}
		if(keyCode==KeyEvent.VK_S||keyCode==KeyEvent.VK_DOWN){down.toggle(isPressed);}
		if(keyCode==KeyEvent.VK_D||keyCode==KeyEvent.VK_RIGHT){right.toggle(isPressed);}
	}
}
