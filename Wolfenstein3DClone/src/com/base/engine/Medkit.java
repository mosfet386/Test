package com.base.engine;

public class Medkit {

	public static final float SCALE=0.4f;
	public static final float SIZEY=SCALE;
	public static final float SIZEX=(float)((double)SIZEY/(0.678571 * 4.0));
	public static final float START=0.0f;
	
	public static final float OFFSETX=0.0f;
	public static final float OFFSETY=0.0f;
	
	public static final float TEX_MIN_X=-OFFSETX;
	public static final float TEX_MAX_X=-1-OFFSETX;
	public static final float TEX_MIN_Y=-OFFSETY;
	public static final float TEX_MAX_Y=1-OFFSETY;
	
	public static final float PICKUP_DISTANCE=0.75f;
	public static final int HEAL_AMOUNT=25;
	
	private Transform transform;
	private static Mesh mesh;
	private static Material material;
	
	public Medkit(Vector3f position)
	{
		if(mesh==null)
		{
			//Note: add additional faces for top&bottom
			//remove redundant vertices
			Vertex[] vertices=new Vertex[]{	new Vertex(new Vector3f(-SIZEX,START,START),new Vector2f(TEX_MAX_X,TEX_MAX_Y)),
											new Vertex(new Vector3f(-SIZEX,SIZEY,START),new Vector2f(TEX_MAX_X,TEX_MIN_Y)),
											new Vertex(new Vector3f(SIZEX,SIZEY,START),new Vector2f(TEX_MIN_X,TEX_MIN_Y)),
											new Vertex(new Vector3f(SIZEX,START,START),new Vector2f(TEX_MIN_X,TEX_MAX_Y))};
			int[] indices=new int[]{0,1,2,
									3,0,2};
			mesh=new Mesh(vertices,indices);
		}
		if(material==null)
		{
			material=new Material(new Texture("MEDIA0.png"));
		}
		transform=new Transform();
		transform.setTranslation(position);
	}

	public void update()
	{
		Vector3f directionToCamera=Transform.getCamera().getPos().sub(transform.getTranslation());
		//this is the angle of the camera to the gun image
		//consider angle of gun to camera, atan(getZ/getX)
		float angleToFaceTheCamera=(float)
				(Math.toDegrees(Math.atan(directionToCamera.getZ()/directionToCamera.getX())));
		if(directionToCamera.getX()<0){angleToFaceTheCamera+=180;}
		transform.getRotation().setY(angleToFaceTheCamera+90);
		
		if(directionToCamera.length()<PICKUP_DISTANCE)
		{
			Player player=Game.getLevel().getPlayer();
			if(player.getHealth()<player.getMaxHealth())
			{
				player.damage(-HEAL_AMOUNT);
				Game.getLevel().removeMedkit(this);
			}
		}
	}
	
	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();	
	}

}