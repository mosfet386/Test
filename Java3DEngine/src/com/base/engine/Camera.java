package com.base.engine;

public class Camera {
	
	//absolute up in  the world for convenience
	public static final Vector3f yAxis=new Vector3f(0,1,0);
	private Vector3f pos; //camera position
	private Vector3f forward; //forward direction
	private Vector3f up; //up direction
	
	boolean mouseLocked = false;
	Vector2f centerPosition = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	
	public Camera(){this(new Vector3f(0,0,0),new Vector3f(0,0,1),new Vector3f(0,1,0));}
	public Camera(Vector3f pos,Vector3f forward,Vector3f up){
		this.pos=pos;
		this.forward=forward;
		this.up=up;
		up.normalize();
		forward.normalize();
		
	}
	public Vector3f getPos(){return pos;}
	public Vector3f getForward(){return forward;}
	public Vector3f getUp(){return up;}
	public void setPos(Vector3f pos){this.pos = pos;}
	public void setForward(Vector3f forward){this.forward = forward;}
	public void setUp(Vector3f up){this.up = up;}
	public Vector3f getLeft(){
		return forward.cross(up).normalize(); //right hand rule
	}
	public Vector3f getRight(){
		return up.cross(forward).normalize(); //right hand rule
	}
	public void move(Vector3f dir,float amount){pos=pos.add(dir.mul(amount));}
	public void rotateX(float angle){
		Vector3f HorizontalAxis=yAxis.cross(forward).normalize();
		forward.rotate(angle,HorizontalAxis).normalize();
		up=forward.cross(HorizontalAxis).normalize();
	}
	public void rotateY(float angle){
		Vector3f HorizontalAxis=yAxis.cross(forward).normalize();
		forward.rotate(angle,yAxis).normalize();
		up=forward.cross(HorizontalAxis).normalize();
	}
	public Vector3f rotate(float angle,Vector3f axis){
		
		return null;
	}
	public void input(){
		float moveAmount=(float)(10*Time.getDelta());
		float rotationAmount=(float)(100*Time.getDelta());
		if(Input.getKey(Input.KEY_W)){move(getForward(),moveAmount);}
		if(Input.getKey(Input.KEY_S)){move(getForward(),-moveAmount);}
		if(Input.getKey(Input.KEY_A)){move(getLeft(),moveAmount);}
		if(Input.getKey(Input.KEY_D)){move(getRight(),moveAmount);}
		
		if(Input.getKey(Input.KEY_UP)){rotateX(-rotationAmount);}
		if(Input.getKey(Input.KEY_DOWN)){rotateX(rotationAmount);}
		if(Input.getKey(Input.KEY_LEFT)){rotateY(-rotationAmount);}
		if(Input.getKey(Input.KEY_RIGHT)){rotateY(rotationAmount);}
		
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
		float sensitivity=0.5f;
		if(mouseLocked){
			Vector2f deltaPos=Input.getMousePosition().sub(centerPosition);
			boolean rotY=deltaPos.getX()!=0;
			boolean rotX=deltaPos.getY()!=0;

			if(rotY){rotateY(deltaPos.getX()*sensitivity);}
			if(rotX){rotateX(-deltaPos.getY()*sensitivity);}
			if(rotY||rotX){Input.setMousePosition(new Vector2f(Window.getWidth()/2, Window.getHeight()/2));}
		}
	}

}
