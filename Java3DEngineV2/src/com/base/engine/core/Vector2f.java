package com.base.engine.core;

public class Vector2f {
	
	private float x,y;
	
	public Vector2f(float x,float y)
	{
		this.x=x;
		this.y=y;
	}
	
	public void setX(float x){this.x = x;}
	
	public void setY(float y){this.y = y;}
	
	public float getX(){return x;}
	
	public float getY(){return y;}
	
	public Vector2f set(float x, float y)
	{
		this.x=x; this.y=y;
		return this;
	}
	
	public Vector2f set(Vector2f vector)
	{
		set(vector.getX(),vector.getY());
		return this;
	}
	
	public String toString(){return "("+x+","+y+")";}
	
	//scaler vector operations
	public Vector2f add(Vector2f vector){return new Vector2f(x+vector.getX(),y+vector.getY());}
	
	public Vector2f add(float value){return new Vector2f(x+value,y+value);}
	
	public Vector2f sub(Vector2f vector){return new Vector2f(x-vector.getX(),y-vector.getY());}
	
	public Vector2f sub(float value){return new Vector2f(x-value,y-value);}
	
	public Vector2f mul(Vector2f vector){return new Vector2f(x*vector.getX(),y*vector.getY());}
	
	public Vector2f mul(float value){return new Vector2f(x*value,y*value);}
	
	public Vector2f div(Vector2f vector){return new Vector2f(x/vector.getX(),y/vector.getY());}
	
	public Vector2f div(float value){return new Vector2f(x/value,y/value);}

	public float max()
	{
		return Math.max(x,y);
	}
	
	public boolean equals(Vector2f vector)
	{
		return x==vector.getX() && y==vector.getY();
	}
	
	public float length(){return (float)Math.sqrt(x*x+y*y);}
	
	public float dot(Vector2f vector){return x*vector.getX()+y*vector.getY();}
	
	public float cross(Vector2f vector)
	{
		return x*vector.getY()-y*vector.getX();
	}
	
	public Vector2f normalize()
	{
		float length=length();
		x/=length;
		y/=length;
		return new Vector2f(x/length,y/length);
	}
	
	public Vector2f rotate(float angle){
		double radians=(double) Math.toRadians(angle);
		double cos=(double)Math.cos(radians);
		double sin=(double)Math.sin(radians);
		return new Vector2f((float)(x*cos-y*sin),(float)(x*sin+y*cos));
	}
	
	public Vector2f lerp(Vector2f dest, float lerpFactor)
	{
		return dest.sub(this).mul(lerpFactor).add(this);
	}
}
