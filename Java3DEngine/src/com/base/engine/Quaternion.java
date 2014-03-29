//w is real, (x,y,z) are imaginary
package com.base.engine;

public class Quaternion {

	private float x,y,z,w; //thats right... w!
	
	public Quaternion(float x,float y,float z,float w){
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=w;
	}
	public float getX(){return x;}
	public float getY(){return y;}
	public float getZ(){return z;}
	public float getW(){return w;}
	public void setX(float x){this.x = x;}
	public void setY(float y){this.y = y;}
	public void setZ(float z){this.z = z;}
	public void setW(float w){this.w = w;}
	public float length(){return (float)Math.sqrt(x*x+y*y+z*z+w*w);}
	public Quaternion normalize(){
		float length=length();
		x/=length;
		y/=length;
		z/=length;
		w/=length;
		return this;
	}
	public Quaternion conjagate(){return new Quaternion(-x,-y,-z,w);}
	public Quaternion mul(Quaternion q){
		//z*z,y*y,x*x = -1,-1,-1; hence the subtraction
		float ww=w*q.getW()-x*q.getX()-y*q.getY()-z*q.getZ();
		float xx=x*q.getW()+w*q.getX()+y*q.getZ()-z*q.getY();
		float yy=y*q.getW()+w*q.getY()+z*q.getX()-x*q.getZ();
		float zz=z*q.getW()+w*q.getZ()+x*q.getY()-y*q.getX();
		return new Quaternion(xx,yy,zz,ww);
	}
	public Quaternion mul(Vector3f v){
		float ww=-x*v.getX()-y*v.getY()-z*v.getZ();
		float xx= w*v.getX()+y*v.getZ()-z*v.getY();
		float yy= w*v.getY()+z*v.getX()-x*v.getZ();
		float zz= w*v.getZ()+x*v.getY()-y*v.getX();
		return new Quaternion(xx,yy,zz,ww);
	}
	
}
