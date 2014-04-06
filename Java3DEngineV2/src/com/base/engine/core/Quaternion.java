//w is real, (x,y,z) are imaginary
package com.base.engine.core;

public class Quaternion 
{
	private float x,y,z,w; //thats right... w!

	public Quaternion(float x,float y,float z,float w)
	{
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=w;
	}
	public Quaternion(Vector3f axis, float angle)
	{
		float sinHalfAngle=(float)Math.sin(angle/2);
		float cosHalfAngle=(float)Math.cos(angle/2);
		this.x=axis.getX()*sinHalfAngle;
		this.y=axis.getY()*sinHalfAngle;
		this.z=axis.getZ()*sinHalfAngle;
		this.w=cosHalfAngle;
	}
	public float getX(){return x;}
	public float getY(){return y;}
	public float getZ(){return z;}
	public float getW(){return w;}
	public void setX(float x){this.x = x;}
	public void setY(float y){this.y = y;}
	public void setZ(float z){this.z = z;}
	public void setW(float w){this.w = w;}
	public Quaternion set(float x, float y, float z, float w)
	{
		this.x=x; this.y=y; this.z=z; this.w=w;
		return this;
	}
	public Quaternion set(Quaternion q)
	{
		set(q.getX(),q.getY(),q.getZ(),q.getW());
		return this;
	}
	public boolean equals(Quaternion q)
	{
		return x==q.getX() && y==q.getY() && z==q.getZ() && w==q.getW();
	}
	public float length(){return (float)Math.sqrt(x*x+y*y+z*z+w*w);}
	public Quaternion normalize()
	{
		float length=length();
		x/=length;
		y/=length;
		z/=length;
		w/=length;
		return this;
	}
	public Quaternion conjagate(){return new Quaternion(-x,-y,-z,w);}
	public Quaternion mul(Quaternion q)
	{
		//z*z,y*y,x*x = -1,-1,-1; hence the subtraction
		float ww=w*q.getW()-x*q.getX()-y*q.getY()-z*q.getZ();
		float xx=x*q.getW()+w*q.getX()+y*q.getZ()-z*q.getY();
		float yy=y*q.getW()+w*q.getY()+z*q.getX()-x*q.getZ();
		float zz=z*q.getW()+w*q.getZ()+x*q.getY()-y*q.getX();
		return new Quaternion(xx,yy,zz,ww);
	}
	public Quaternion mul(Vector3f v)
	{
		float ww=-x*v.getX()-y*v.getY()-z*v.getZ();
		float xx= w*v.getX()+y*v.getZ()-z*v.getY();
		float yy= w*v.getY()+z*v.getX()-x*v.getZ();
		float zz= w*v.getZ()+x*v.getY()-y*v.getX();
		return new Quaternion(xx,yy,zz,ww);
	}
	public Quaternion mul(float value)
	{
		return new Quaternion(x*value,y*value,z*value,w*value);
	}	
	public Matrix4f toRotationMatrix()
	{
		Vector3f forward=new Vector3f(2.0f*(x*z-w*y),2.0f*(y*z+w*x),1.0f-2.0f*(x*x+y*y));
		Vector3f up=new Vector3f(2.0f*(x*y+w*z),1.0f-2.0f*(x*x+z*z),2.0f*(y*z-w*x));
		Vector3f right=new Vector3f(1.0f-2.0f*(y*y+z*z),2.0f*(x*y-w*z),2.0f*(x*z+w*y));
		
		return new Matrix4f().initRotation(forward,up,right);
	}
	public Vector3f getForward()
	{
		return new Vector3f(0,0,1).rotate(this);
	}
	public Vector3f getBack()
	{
		return new Vector3f(0,0,-1).rotate(this);
	}
	public Vector3f getUp()
	{
		return new Vector3f(0,1,0).rotate(this);
	}
	public Vector3f getDown()
	{
		return new Vector3f(0,-1,0).rotate(this);
	}
	public Vector3f getRight()
	{
		return new Vector3f(1,0,0).rotate(this);
	}
	public Vector3f getLeft()
	{
		return new Vector3f(-1,0,0).rotate(this);
	}
}
