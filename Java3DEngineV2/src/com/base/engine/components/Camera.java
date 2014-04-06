package com.base.engine.components;

import com.base.engine.core.Input;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Window;

public class Camera extends GameComponent
{
	//absolute up in  the world for convenience
	public static final Vector3f yAxis=new Vector3f(0,1,0);
	private Matrix4f projection;
	
	boolean mouseLocked = false;
	Vector2f centerPosition = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);

	public Camera(float fov, float aspect, float zNear, float zFar)
	{
		this.projection=new Matrix4f().initPerspective(fov,aspect,zNear,zFar);
	}
	
	public Matrix4f getViewProjection()
	{
		Matrix4f cameraRotation=getTransform().getTransformedRot().conjagate().toRotationMatrix();
		Vector3f cameraPos=getTransform().getTransformedPos().mul(-1);
		Matrix4f cameraTranslation=new Matrix4f().initTranslation(
					cameraPos.getX(),cameraPos.getY(),cameraPos.getZ());
	
		//move camera (cameraTranslation)
		//rotate camera (cameraRotation)
		//normalize world view (projection)
		return projection.mul(cameraRotation.mul(cameraTranslation));
	}
	
	public void move(Vector3f dir,float amount)
	{
		getTransform().setPos(getTransform().getPos().add(dir.mul(amount)));
	}
	
	@Override
	public void input(float delta)
	{
		float moveAmount=(float)(10*delta);

		if(Input.getKey(Input.KEY_W))
			move(getTransform().getRot().getForward(),moveAmount);
		if(Input.getKey(Input.KEY_S))
			move(getTransform().getRot().getForward(),-moveAmount);
		if(Input.getKey(Input.KEY_A))
			move(getTransform().getRot().getLeft(),moveAmount);
		if(Input.getKey(Input.KEY_D))
			move(getTransform().getRot().getRight(),moveAmount);

		
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

			if(rotY)
				getTransform().rotate(yAxis,(float)Math.toRadians(deltaPos.getX()*sensitivity));
			if(rotX)
				getTransform().rotate(getTransform().getRot().getRight(),(float)Math.toRadians(-deltaPos.getY()*sensitivity));
			if(rotY||rotX)
				Input.setMousePosition(
						new Vector2f(Window.getWidth()/2, Window.getHeight()/2));
		}
	}

	@Override
	public void addToRenderingEngine(RenderingEngine renderingEngine) 
	{
		renderingEngine.addCamera(this);
	}
}
