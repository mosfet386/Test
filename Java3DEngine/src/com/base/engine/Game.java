package com.base.engine;

import org.lwjgl.input.Keyboard;

public class Game {
	
	private Mesh mesh;
	private Shader shader;
	private Transform transform;
	private Camera camera;
	float temp=0.0f;
	
	public Game(){
		mesh=ResourceLoader.loadMesh("cube2.obj");
		shader=new Shader();
		camera=new Camera();
//		mesh=new Mesh();
//		shader=new Shader();
//		Vertex[] vertices=new Vertex[] {new Vertex(new Vector3f(-1,-1,0)),
//									new Vertex(new Vector3f(0,1,0)),
//									new Vertex(new Vector3f(1,-1,0)),
//									new Vertex(new Vector3f(0,-1,1))};
//		//these indices will draw the vertices in the above order
//		int[] indices=new int[] {0,1,3,
//								3,1,2,
//								2,1,0,
//								0,2,3};
//		
//		mesh.addVertices(vertices,indices);
		Transform.setProjection(70f,Window.getWidth(),Window.getHeight(), 0.1f,1000);
		Transform.setCamera(camera);
		transform=new Transform();
		
		shader.addVertexShader(ResourceLoader.loadShader("basicVertex.vsh"));
		shader.addFragmentShader(ResourceLoader.loadShader("basicFragment.fsh"));
		shader.compileShader();
		shader.addUniform("transform");
		System.out.println(RenderUtility.getOpenGLVersion());
	}
	public void input(){
		camera.input();
//		if(Input.getKeyDown(Keyboard.KEY_UP)){
//			System.out.println("Up has been pressed");
//		}
//		if(Input.getKeyUp(Keyboard.KEY_UP)){
//			System.out.println("Up has been released");
//		}
//		if(Input.getMouseDown(1)){
//			System.out.println("Mouse button pressed at "+Input.getMousePosition());
//		}
//		if(Input.getMouseUp(1)){
//			System.out.println("Mouse button released "+Input.getMousePosition());
//		}
	}
	public void update(){
		temp+=Time.getDelta();
		float sinTemp=(float)Math.sin(temp);
		transform.setTranslation(sinTemp,0,5);
		transform.setRotation(0,sinTemp*180,0);
		//transform.setScale(0.7f*sinTemp,0.7f*sinTemp,0.7f*sinTemp);
	}
	public void render(){
		shader.bind();
		shader.setUniform("transform",transform.getProjectedTransformation());
		mesh.draw();
	}

}
