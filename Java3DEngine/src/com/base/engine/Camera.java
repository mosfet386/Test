package com.base.engine;

public class Camera {
	
	//absolute up in  the world for convenience
	public static final Vector3f yAxis=new Vector3f(0,1,0);
	private Vector3f pos; //camera position
	private Vector3f forward; //forward direction
	private Vector3f up; //up direction
	
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
		Vector3f left=forward.cross(up); //right hand rule
		left.normalize();
		return left;
	}
	public Vector3f getRight(){
		Vector3f right=up.cross(forward); //right hand rule
		right.normalize();
		return right;
	}
	public void move(Vector3f dir,float amount){pos=pos.add(dir.mul(amount));}
	public void rotateX(float angle){
		Vector3f HorizontalAxis=yAxis.cross(forward);
		HorizontalAxis.normalize();
		forward.rotate(angle,HorizontalAxis);
		forward.normalize();
		up=forward.cross(HorizontalAxis);
		up.normalize();
	}
	public void rotateY(float angle){
		Vector3f HorizontalAxis=yAxis.cross(forward);
		HorizontalAxis.normalize();
		forward.rotate(angle,yAxis);
		forward.normalize();
		up=forward.cross(HorizontalAxis);
		up.normalize();
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
	}
}
