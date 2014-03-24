package com.base.engine;

public class Vector3f {
	
	private float x,y,z;
	
	public Vector3f(float x,float y, float z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public float getX(){return x;}
	public float getY(){return y;}
	public float getZ(){return z;}
	public void setX(float x){this.x = x;}
	public void setY(float y){this.y = y;}
	public void setZ(float z){this.z = z;}
	public float length(){return (float)Math.sqrt(x*x+y*y+z*z);}
	
	
	public float dot(Vector3f vector){return x*vector.getX()+y*vector.getY()+z*vector.getZ();}
	public Vector3f normalize(){
		float length=length();
		x/=length;
		y/=length;
		z/=length;
		return this;
	}
//	public Vector3f rotate(float angle){
//		double radians=(double) Math.toRadians(angle);
//		double cos=(double)Math.cos(radians);
//		double sin=(double)Math.sin(radians);
//		return new Vector3f((float)(x*cos-y*sin),(float)(x*sin+y*cos));
//	}
	public Vector3f cross(Vector3f vector){
		float xx=y*vector.getZ()-z*vector.getY();
		float yy=z*vector.getX()-x*vector.getZ();
		float zz=x*vector.getY()-y*vector.getX();
		return new Vector3f(xx,yy,zz);
	}
	//scaler vector operations
	public Vector3f add(Vector3f vector){return new Vector3f(x+vector.getX(),y+vector.getY(),z+vector.getZ());}
	public Vector3f add(float value){return new Vector3f(x+value,y+value,z+value);}
	public Vector3f sub(Vector3f vector){return new Vector3f(x-vector.getX(),y-vector.getY(),z-vector.getZ());}
	public Vector3f sub(float value){return new Vector3f(x-value,y-value,z-value);}
	public Vector3f mul(Vector3f vector){return new Vector3f(x*vector.getX(),y*vector.getY(),z*vector.getZ());}
	public Vector3f mul(float value){return new Vector3f(x*value,y*value,z*value);}
	public Vector3f div(Vector3f vector){return new Vector3f(x/vector.getX(),y/vector.getY(),y/vector.getZ());}
	public Vector3f div(float value){return new Vector3f(x/value,y/value,z/value);}
	
	
}