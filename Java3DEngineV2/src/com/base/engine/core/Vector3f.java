package com.base.engine.core;

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
	
	public Vector3f set(float x, float y, float z)
	{
		this.x=x; this.y=y; this.z=z;
		return this;
	}
	
	public Vector3f set(Vector3f vector)
	{
		set(vector.getX(),vector.getY(),vector.getZ());
		return this;
	}
	
	//Vector Swizeling
	public Vector2f getXY(){return new Vector2f(x,y);}
	
	public Vector2f getYZ(){return new Vector2f(y,z);}
	
	public Vector2f getZX(){return new Vector2f(z,x);}
	
	public Vector2f getYX(){return new Vector2f(y,x);}
	
	public Vector2f getZY(){return new Vector2f(z,y);}
	
	public Vector2f getXZ(){return new Vector2f(x,z);}

	public Vector3f abs(){return new Vector3f(Math.abs(x),Math.abs(y),Math.abs(z));}
	
	public Vector3f add(Vector3f vector){return new Vector3f(x+vector.getX(),y+vector.getY(),z+vector.getZ());}
	
	public Vector3f add(float value){return new Vector3f(x+value,y+value,z+value);}
	
	public Vector3f sub(Vector3f vector){return new Vector3f(x-vector.getX(),y-vector.getY(),z-vector.getZ());}
	
	public Vector3f sub(float value){return new Vector3f(x-value,y-value,z-value);}
	
	public Vector3f mul(Vector3f vector){return new Vector3f(x*vector.getX(),y*vector.getY(),z*vector.getZ());}
	
	public Vector3f mul(float value){return new Vector3f(x*value,y*value,z*value);}
	
	public Vector3f div(Vector3f vector){return new Vector3f(x/vector.getX(),y/vector.getY(),y/vector.getZ());}
	
	public Vector3f div(float value){return new Vector3f(x/value,y/value,z/value);}
	
	public float max()
	{
		return Math.max(x,Math.max(y, z));
	}
	
	public boolean equals(Vector3f vector)
	{
		return x==vector.getX() && y==vector.getY() && z==vector.getZ();
	}
	
	public float length(){return (float)Math.sqrt(x*x+y*y+z*z);}
	
	public float dot(Vector3f vector){return x*vector.getX()+y*vector.getY()+z*vector.getZ();}
	
	public Vector3f cross(Vector3f vector)
	{
		float xx=y*vector.getZ()-z*vector.getY();
		float yy=z*vector.getX()-x*vector.getZ();
		float zz=x*vector.getY()-y*vector.getX();
		return new Vector3f(xx,yy,zz);
	}
	
	public Vector3f normalize()
	{
		float length=length();
		return new Vector3f(x/length,y/length,z/length);
	}
	
	public Vector3f lerp(Vector3f dest, float lerpFactor)
	{
		return dest.sub(this).mul(lerpFactor).add(this);
	}

	public Vector3f rotate(Vector3f axis, float angle)
	{
		float sinAngle=(float)Math.sin(-angle);
		float cosAngle=(float)Math.cos(-angle);
		return this.cross(axis.mul(sinAngle)).add(
					(this.mul(cosAngle)).add(
							axis.mul(this.dot(axis.mul(1-cosAngle)))));
		//return this.rotate(new Quaternion().initRotation(axis,angle));
	}
	
	public Vector3f rotate(Quaternion rotation)
	{
		Quaternion conjugate=rotation.conjagate();
		Quaternion w=rotation.mul(this).mul(conjugate); //rotate and throw away imaginary comp

		return new Vector3f(w.getX(),w.getY(),w.getZ());
	}
	
	//	public Vector3f rotate(float angle){
//		double radians=(double) Math.toRadians(angle);
//		double cos=(double)Math.cos(radians);
//		double sin=(double)Math.sin(radians);
//		return new Vector3f((float)(x*cos-y*sin),(float)(x*sin+y*cos));
//	}
	
}
