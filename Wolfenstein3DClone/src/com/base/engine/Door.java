package com.base.engine;

public class Door {

	public static final float LENGTH=1;
	public static final float HEIGHT=1;
	public static final float WIDTH=0.125f;
	public static final float START=0;
	public static final double TIME_TO_OPEN=0.25;
	public static final double CLOSE_DELAY=2.0;
	
	private static Mesh mesh;
	private Transform transform;
	private Material material;
	
	private boolean isOpening;
	private double openingStartTime;
	private double openTime;
	private double closingStartTime;
	private double closeTime;
	
	private Vector3f openPosition;
	private Vector3f closePosition;
	
	public Door(Transform transform, Material material, Vector3f openPosition){
		this.transform=transform;
		this.material=material;
		this.isOpening=false;
		this.closePosition=transform.getTranslation().mul(1); //trick to return copy
		this.openPosition=openPosition;
		if(mesh==null){
			
			//Note: add additional faces for top&bottom
			//remove redundant vertices
			Vertex[] vertices=new Vertex[]{	new Vertex(new Vector3f(START,START,START),new Vector2f(0.5f,1)),
											new Vertex(new Vector3f(START,HEIGHT,START),new Vector2f(0.5f,0.75f)),
											new Vertex(new Vector3f(LENGTH,HEIGHT,START),new Vector2f(0.75f,0.75f)),
											new Vertex(new Vector3f(LENGTH,START,START),new Vector2f(0.75f,1)),
											
											new Vertex(new Vector3f(START,START,START),new Vector2f(0.73f,1)),
											new Vertex(new Vector3f(START,HEIGHT,START),new Vector2f(0.73f,0.75f)),
											new Vertex(new Vector3f(START,HEIGHT,WIDTH),new Vector2f(0.75f,0.75f)),
											new Vertex(new Vector3f(START,START,WIDTH),new Vector2f(0.75f,1)),
											
											new Vertex(new Vector3f(START,START,WIDTH),new Vector2f(0.5f,1)),
											new Vertex(new Vector3f(START,HEIGHT,WIDTH),new Vector2f(0.5f,0.75f)),
											new Vertex(new Vector3f(LENGTH,HEIGHT,WIDTH),new Vector2f(0.75f,0.75f)),
											new Vertex(new Vector3f(LENGTH,START,WIDTH),new Vector2f(0.75f,1)),
											
											new Vertex(new Vector3f(LENGTH,START,START),new Vector2f(0.73f,1)),
											new Vertex(new Vector3f(LENGTH,HEIGHT,START),new Vector2f(0.73f,0.75f)),
											new Vertex(new Vector3f(LENGTH,HEIGHT,WIDTH),new Vector2f(0.75f,0.75f)),
											new Vertex(new Vector3f(LENGTH,START,WIDTH),new Vector2f(0.75f,1))};
			int[] indices=new int[]{0,1,2,
									3,0,2,
									
									6,5,4,
									7,6,4,
									
									10,9,8,
									11,10,8,
									
									12,13,14,
									12,14,15};
			mesh=new Mesh(vertices,indices);
		}
	}
	
	public void update(){
		if(isOpening){
			double time=(double)Time.getTime()/(double)Time.SECOND;
			if(time<openTime){
				float lerpFactor=(float)((time - openingStartTime)/TIME_TO_OPEN);
				getTransform().setTranslation(
						vectorLerp(closePosition,openPosition,lerpFactor));
			}else if(time<closingStartTime){
				getTransform().setTranslation(openPosition);
			}else if(time<closeTime){
				float lerpFactor=(float)((time - closingStartTime)/TIME_TO_OPEN);
				getTransform().setTranslation(
						vectorLerp(openPosition,closePosition,lerpFactor));
			}else{
				getTransform().setTranslation(closePosition);
				isOpening=false;
			}
		}
	}
	
	public void render(){
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}
	
	public Transform getTransform(){
		return transform;
	}

	//LERP stands for linear interpolation
	public Vector3f vectorLerp(Vector3f startPos, Vector3f endPos, float lerpFactor){
		return startPos.add(endPos.sub(startPos).mul(lerpFactor));
	}
	
	public void open()
	{
		if(isOpening)return;
		openingStartTime = (double)Time.getTime()/(double)Time.SECOND;
		openTime = openingStartTime + TIME_TO_OPEN;
		closingStartTime = openTime + CLOSE_DELAY;
		closeTime = closingStartTime + TIME_TO_OPEN;
		isOpening = true;
	}
	
	public Vector2f getDoorSize(){
		if(getTransform().getRotation().getY()==90){
			return new Vector2f(Door.WIDTH,Door.LENGTH);
		}
		else{return new Vector2f(Door.LENGTH,Door.WIDTH);}
	}
}
