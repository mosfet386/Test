package com.base.engine.core;

public class Transform 
{
	private Transform parent;
	private Matrix4f parentMatrix;
	
	private Vector3f pos;
	private Quaternion rot;
	private Vector3f scale;
	
	private Vector3f oldPos;
	private Quaternion oldRot;
	private Vector3f oldScale;

	public Transform()
	{
		pos=new Vector3f(0,0,0);
		rot=new Quaternion(0,0,0,1);
		scale=new Vector3f(1,1,1);
		parentMatrix=new Matrix4f().initIdentitiy();
	}
	
	public void update()
	{
		if(oldPos!=null)
		{
			oldPos.set(pos);
			oldRot.set(rot);
			oldScale.set(scale);
		}
		//while recursing to check for changes update parents
		else
		{
			//1,0.5,1 are only used to guarantee old and current values are different
			//this is to make sure hasChanged is true on first call
			oldPos=new Vector3f(0,0,0).set(pos).add(1.0f);
			oldRot=new Quaternion(0,0,0,0).set(rot).mul(0.5f);
			oldScale=new Vector3f(0,0,0).set(scale).add(1.0f);
		}
	}
	
	public boolean hasChanged()
	{
		//has changed checks are recursive to back to to first parent
		if(parent!=null && parent.hasChanged())
			return true;
		if(!pos.equals(oldPos))
			return true;
		if(!rot.equals(oldRot))
			return true;
		if(!scale.equals(oldScale))
			return true;
		
		return false;
	}

	public Matrix4f getParentMatrix()
	{
		//Recursively call upon parents to transform (place) object in world
		//with respect to all successive parents
		if(parent!=null && parent.hasChanged())
			parentMatrix=parent.getTransformation();
		
		return parentMatrix;
	}
	
	public void setParent(Transform parent)
	{
		this.parent=parent;
	}
	
	public Vector3f getTransformedPos()
	{
		return getParentMatrix().transform(pos);
	}
	
	public Matrix4f getTransformation()
	{
		Matrix4f scaleMatrix=
				new Matrix4f().initScale(scale.getX(),scale.getY(),scale.getZ());
		Matrix4f translationMatrix=
				new Matrix4f().initTranslation(pos.getX(),pos.getY(),pos.getZ());
		Matrix4f rotationMatrix=rot.toRotationMatrix();

		
		//scale 0-1, 
		//rotate by degrees, 
		//and then translate (screen -1-1)
		return getParentMatrix().mul(translationMatrix.mul(rotationMatrix.mul(scaleMatrix)));
	}
	
	public Vector3f getScale(){return scale;}
	
	public void setScale(Vector3f scale){this.scale=scale;}

	public Vector3f getPos(){return pos;}
	
	public void setPos(Vector3f pos){this.pos=pos;}

	public Quaternion getRot(){return rot;}
	
	public Quaternion getTransformedRot()
	{
		Quaternion parentRotation=new Quaternion(0,0,0,1);
		
		//Recursively call upon parents to transform (place) object in world
		//with respect to all successive parents
		if(parent!=null)
			parentRotation=parent.getTransformedRot();
		
		return parentRotation.mul(rot);
	}
	
	public void setRot(Quaternion rotation){this.rot = rotation;}

	public void rotate(Vector3f axis, float angle)
	{
		rot=new Quaternion(axis,angle).mul(rot).normalize();
	}
}
