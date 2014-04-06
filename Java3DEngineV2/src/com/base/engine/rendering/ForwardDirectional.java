package com.base.engine.rendering;

import com.base.engine.components.BaseLight;
import com.base.engine.components.DirectionalLight;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Transform;

public class ForwardDirectional extends Shader
{
	private static final ForwardDirectional instance=new ForwardDirectional ();
	
	public ForwardDirectional ()
	{
		super("forward-directional");
	}
	
	public void updateUniforms(Transform transform, Material material, 
											RenderingEngine renderingEngine)
	{
		Matrix4f worldMatrix=transform.getTransformation();
		Matrix4f projectedMatrix=
				renderingEngine.getMainCamera().getViewProjection().mul(worldMatrix);
		material.getTexture("diffuse").bind();
		
		setUniform("model",worldMatrix);
		setUniform("MVP",projectedMatrix);
		
		setUniform("eyePos",
				renderingEngine.getMainCamera().getTransform().getTransformedPos());
		setUniformf("specularIntensity",material.getFloat("specularIntensity"));
		setUniformf("specularPower",material.getFloat("specularPower"));
		setUniformDirectionalLight("directionalLight",(DirectionalLight)renderingEngine.getActiveLight());
	}
	
	public void setUniformBaseLight(String uniformName,BaseLight baseLight)
	{
		setUniform(uniformName+".color",baseLight.getColor());
		setUniformf(uniformName+".intensity",baseLight.getIntensity());
	}
	
	public void setUniformDirectionalLight(String uniformName,DirectionalLight directionalLight)
	{
		setUniformBaseLight(uniformName+".base",directionalLight);
		setUniform(uniformName+".direction",directionalLight.getDirection());
	}

	public static ForwardDirectional getInstance(){return instance;}
}
