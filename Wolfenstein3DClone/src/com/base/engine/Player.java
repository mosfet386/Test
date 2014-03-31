package com.base.engine;

import java.util.Random;

import com.base.engine.Monster.STATE;

public class Player {

	private Camera camera;
	private static boolean mouseLocked=false;
	private Vector2f centerPosition = new Vector2f(Window.getWidth()/2, Window.getHeight()/2);
	private Vector3f movementVector;
	
	public static final float SCALE=0.0625f;
	public static final float SIZEY=SCALE;
	public static final float SIZEX=(float)((double)SIZEY/(1.037975 * 2.0));
	public static final float START=0.0f;
	public static final float OFFSETX=0.0f;
	public static final float OFFSETY=0.0f;
	public static final float OFFSET_FROM_GROUND=0.0f;
	public static final float GUN_OFFSET=-0.0875f;;
	public static final float TEX_MIN_X=-OFFSETX;
	public static final float TEX_MAX_X=-1-OFFSETX;
	public static final float TEX_MIN_Y=-OFFSETY;
	public static final float TEX_MAX_Y=1-OFFSETY;
	private static Mesh mesh;
	private static Material gunMaterial;
	private Transform gunTransform;
	
	public static final float PLAYER_SIZE=0.15f;
	private static final float MOUSE_SENSITIVITY=0.3f;
	private static final float MOVE_SPEED=5f;
	private static final float SHOOT_DISTANCE=1000.0f;
	public static final int DAMAGE_MAX=60;
	public static final int DAMAGE_MIN=20;
	public static final int MAX_HEALTH=100;
	private final Random rand;
	private static final Vector3f zeroVector=new Vector3f(0,0,0);
	
	private int health;

	public Player(Vector3f position){
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
		if(gunMaterial==null)
		{
			gunMaterial=new Material(new Texture("PISGB0.png"));
		}
		camera=new Camera(position,new Vector3f(0,0,1),new Vector3f(0,1,0));
		rand=new Random();
		health=MAX_HEALTH;
		gunTransform=new Transform();
		gunTransform.setTranslation(new Vector3f(2.7f,0.43f,3.7f));
		movementVector=zeroVector;
	}
	
	public void input(){
		//clean up, zeroVector,+,- not necessary here
		movementVector=zeroVector; //Redirect the camera forward
		if(Input.getKey(Input.KEY_W)){movementVector=movementVector.add(camera.getForward());}
		if(Input.getKey(Input.KEY_S)){movementVector=movementVector.sub(camera.getForward());;}
		if(Input.getKey(Input.KEY_A)){movementVector=movementVector.add(camera.getLeft());}
		if(Input.getKey(Input.KEY_D)){movementVector=movementVector.add(camera.getRight());}
		if(Input.getKey(Input.KEY_E)){Game.getLevel().openDoors(getCamera().getPos(),true);}
		
		if(Input.getKey(Input.KEY_ESCAPE))
		{
			Input.setCursor(true);
			mouseLocked = false;
		}
		if(Input.getMouse(0)){
			if(!mouseLocked)
			{
				Input.setMousePosition(centerPosition);
				Input.setCursor(false);
				mouseLocked=true;
			}
			else
			{
				Vector2f lineStart=new Vector2f(camera.getPos().getX(),camera.getPos().getZ());
				Vector2f castDirection=new Vector2f(camera.getForward().getX(),camera.getForward().getZ()).normalize();
				Vector2f lineEnd=lineStart.add(castDirection.mul(SHOOT_DISTANCE));
				Game.getLevel().checkIntersections(lineStart,lineEnd,true);
			}
		}
		if(mouseLocked){
			Vector2f deltaPos=Input.getMousePosition().sub(centerPosition);
			boolean rotY=deltaPos.getX()!=0;
			boolean rotX=deltaPos.getY()!=0;
			if(rotY){camera.rotateY(deltaPos.getX()*MOUSE_SENSITIVITY);}
			if(rotX){camera.rotateX(-deltaPos.getY()*MOUSE_SENSITIVITY);}
			if(rotY||rotX){Input.setMousePosition(centerPosition);}
		}
	}
	
	public void update(){
		
		//Player Movement
		float moveAmount=(float)(MOVE_SPEED*Time.getDelta()); //speed system independent
		movementVector.setY(0);
		if(movementVector.length()>0){movementVector=movementVector.normalize();}
		
		Vector3f oldPos=camera.getPos();
		Vector3f newPos=oldPos.add(movementVector.mul(moveAmount));
		Vector3f collisionVector=Game.getLevel().checkCollision(oldPos,newPos,PLAYER_SIZE,PLAYER_SIZE);
		movementVector=movementVector.mul(collisionVector);
		
		if(movementVector.length()>0)
			camera.move(movementVector,moveAmount);
		
		//Gun Movement
		gunTransform.setTranslation(camera.getPos().add(camera.getForward().normalize().mul(0.105f)));
		gunTransform.getTranslation().setY(gunTransform.getTranslation().getY()+GUN_OFFSET);
		Vector3f directionToCamera=Transform.getCamera().getPos().sub(gunTransform.getTranslation());
		//this is the angle of the camera to the gun image
		//consider angle of gun to camera, atan(getZ/getX)
		float angleToFaceTheCamera=(float)
				(Math.toDegrees(Math.atan(directionToCamera.getZ()/directionToCamera.getX())));
		if(directionToCamera.getX()<0){angleToFaceTheCamera+=180;}
		gunTransform.getRotation().setY(angleToFaceTheCamera+90);
	}

	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(gunTransform.getTransformation(), gunTransform.getProjectedTransformation(), gunMaterial);
		mesh.draw();	
	}
	
	public Camera getCamera(){return camera;}
	
	public int getHealth()
	{
		return health;
	}
	
	public int getMaxHealth()
	{
		return MAX_HEALTH;
	}
	
	public int getDamage()
	{
		return rand.nextInt(DAMAGE_MAX-DAMAGE_MIN)+DAMAGE_MIN;
	}

	public void damage(int amount)
	{
		health-=amount;
		if(health>MAX_HEALTH){health=MAX_HEALTH;}
		else if (health<=0)
		{
			System.out.println("Your Dead! GAME OVER!");
			Game.setIsRunning(false);
		}
		System.out.println("Player Health: "+health);
	}
}
