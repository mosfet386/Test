package com.base.engine.rendering;

import com.base.engine.components.BaseLight;
import com.base.engine.components.PointLight;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Transform;

public class ForwardPoint extends Shader
{
	private static final ForwardPoint instance=new ForwardPoint ();
	
	public ForwardPoint ()
	{
		super("forward-point");
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
		
		setUniformPointLight("pointLight",(PointLight)renderingEngine.getActiveLight());
	}
	
	public void setUniformBaseLight(String uniformName,BaseLight baseLight)
	{
		setUniform(uniformName+".color",baseLight.getColor());
		setUniformf(uniformName+".intensity",baseLight.getIntensity());
	}
	
	public void setUniformPointLight(String uniformName,PointLight pointLight)
	{
		setUniformBaseLight(uniformName+".base",pointLight);
		setUniformf(uniformName+".atten.constant",pointLight.getConstant());
		setUniformf(uniformName+".atten.linear",pointLight.getLinear());
		setUniformf(uniformName+".atten.exponent",pointLight.getExponent());
		setUniform(uniformName+".position",pointLight.getTransform().getTransformedPos());
		setUniformf(uniformName+".range",pointLight.getRange());
	}
	
	public static ForwardPoint getInstance(){return instance;}
}
