package com.base.engine;

public class BasicShader extends Shader{

	private static final BasicShader instance=new BasicShader();
	
	public BasicShader(){
		super();
		addVertexShaderFromFile("basicVertex.vsh");
		addFragmentShaderFromFile("basicFragment.fsh");
		compileShader();
		addUniform("transform");
		addUniform("color");
	}
	public void updateUniforms(Matrix4f worldMatrix,Matrix4f projectedMatrix,Material material){
		setUniform("transform",projectedMatrix);
		setUniform("color",material.getColor());
		if(material.getTexture()!=null){material.getTexture().bind();}
		else{RenderUtility.unbindTextures();}
	}
	public static BasicShader getInstance(){return instance;}
	
}
