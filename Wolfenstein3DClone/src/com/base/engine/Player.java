package com.base.engine;

public class Player {

	private Camera camera;
	private boolean mouseLocked = false;
	private Vector2f centerPosition = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	private Vector3f movementVector;
	
	private static final float PLAYER_SIZE=0.15f;
	private static final float MOUSE_SENSITIVITY=0.3f;
	private static final float MOVE_SPEED=3f;
	private static final Vector3f zeroVector=new Vector3f(0,0,0);
	
	public Player(Vector3f position){
		camera=new Camera(position,new Vector3f(0,0,1),new Vector3f(0,1,0));
	}
	
	public void input(){
		//clean up, zeroVector,+,- not necessary here
		movementVector=zeroVector; //Redirect the camera forward
		if(Input.getKey(Input.KEY_W)){movementVector=movementVector.add(camera.getForward());}
		if(Input.getKey(Input.KEY_S)){movementVector=movementVector.sub(camera.getForward());;}
		if(Input.getKey(Input.KEY_A)){movementVector=movementVector.add(camera.getLeft());}
		if(Input.getKey(Input.KEY_D)){movementVector=movementVector.add(camera.getRight());}
		
		if(Input.getKey(Input.KEY_ESCAPE))
		{
			Input.setCursor(true);
			mouseLocked = false;
		}
		if(Input.getMouse(0)){
			Input.setMousePosition(centerPosition);
			Input.setCursor(false);
			mouseLocked=true;
		}
		if(mouseLocked){
			Vector2f deltaPos=Input.getMousePosition().sub(centerPosition);
			boolean rotY=deltaPos.getX()!=0;
			boolean rotX=deltaPos.getY()!=0;
			if(rotY){camera.rotateY(deltaPos.getX()*MOUSE_SENSITIVITY);}
			if(rotX){camera.rotateX(-deltaPos.getY()*MOUSE_SENSITIVITY);}
			if(rotY||rotX){Input.setMousePosition(centerPosition);}
		}
	}
	
	public void update(){
		
		float moveAmount=(float)(MOVE_SPEED*Time.getDelta()); //speed system independent
		movementVector.setY(0);
		if(movementVector.length()>0){movementVector=movementVector.normalize();}
		
		Vector3f oldPos=camera.getPos();
		Vector3f newPos=oldPos.add(movementVector.mul(moveAmount));
		Vector3f collisionVector=Game.getLevel().checkCollision(oldPos,newPos,PLAYER_SIZE,PLAYER_SIZE);
		movementVector=movementVector.mul(collisionVector);
		
		camera.move(movementVector,moveAmount);
	}

	public void render(){
		
	}
	
	public Camera getCamera(){return camera;}
	
}
