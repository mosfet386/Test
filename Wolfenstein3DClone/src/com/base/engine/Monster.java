package com.base.engine;

import java.util.Random;

public class Monster {

	public static final float SCALE=0.7f;
	public static final float SIZEY=SCALE;
	public static final float SIZEX=(float)((double)SIZEY/(1.9310344827586206896551724137931 * 2.0));
	public static final float START=0.0f;
	
	public static final float OFFSETX=0.0f;
	public static final float OFFSETY=0.0f;
	public static final float OFFSET_FROM_GROUND=0.0f;
	
	public static final float TEX_MIN_X=-OFFSETX;
	public static final float TEX_MAX_X=-1-OFFSETX;
	public static final float TEX_MIN_Y=-OFFSETY;
	public static final float TEX_MAX_Y=1-OFFSETY;

	public static enum STATE{IDLE,CHASE,ATTACK,DYING,DEAD};
	
	public static final float MOVE_SPEED=1.0f;
	public static final float MOVEMENT_STOP_DISTANCE=1.0f;
	public static final float MONSTER_WIDTH=0.15f;
	public static final float MONSTER_LENGTH=0.15f;
	public static final float SHOOT_DISTANCE=1000.0f;
	public static final float SHOT_ANGLE=10;
	public static final float ATTACK_CHANCE=0.5f;
	public static final int MAX_HEALTH=100;
	public static final boolean FRIENDLY_FIRE=false;
	private final Random rand;
	
	private static Mesh mesh;
	private Transform transform;
	private Material material;
	private STATE state;
	private boolean canLook;
	private boolean canAttack;
	private int health;
	
	public Monster(Transform transform)
	{
		this.transform=transform;
		material=new Material(new Texture("SSWVA1.png"));
		this.rand=new Random();
		this.state=STATE.IDLE;
		this.canLook=false;
		this.canAttack=false;
		this.health=MAX_HEALTH;
		if(mesh==null){
			
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
	}
	
	public void update()
	{
		//Vector3f directionToCamera=transform.getTranslation().sub(Transform.getCamera().getPos());
		Vector3f directionToCamera=Transform.getCamera().getPos().sub(transform.getTranslation());
		float distance=directionToCamera.length();
		Vector3f orientation=directionToCamera.div(distance);
		
		alignedWithGround();
		faceTheCamera(orientation);
		
		switch(state)
		{
			case IDLE: idleUpdate(orientation,distance); break;
			case CHASE: chaseUpdate(orientation,distance); break;
			case ATTACK: attackUpdate(orientation,distance); break;
			case DYING: dyingUpdate(orientation,distance); break;
			case DEAD: deadUpdate(orientation,distance); break;
		}
	}
	
	public void render()
	{
		Shader shader = Game.getLevel().getShader();
		shader.updateUniforms(transform.getTransformation(), transform.getProjectedTransformation(), material);
		mesh.draw();
	}
	
	public Transform getTransform()
	{
		return transform;
	}
	
	public Vector2f getSize()
	{
		return new Vector2f(MONSTER_WIDTH,MONSTER_LENGTH);
	}
	
	public void damage(int amount)
	{
		//Chase if attacked
		if(state==STATE.IDLE){state=STATE.CHASE;}
		health-=amount;
		if(health<=0){state=STATE.DYING;}
	}

	private void alignedWithGround()
	{
		transform.getTranslation().setY(OFFSET_FROM_GROUND);
	}
	
	private void faceTheCamera(Vector3f orientation)
	{		
		//this is the angle of the camera to the monster
		//consider angle of monster to camera, atan(getZ/getX)
		float angleToFaceTheCamera=(float)
				(Math.toDegrees(Math.atan(orientation.getZ()/orientation.getX())));
		if(orientation.getX()<0){angleToFaceTheCamera+=180;}
		transform.getRotation().setY(angleToFaceTheCamera+90);
	}
	
	private void idleUpdate(Vector3f orientation, float distance)
	{
		//fractional portion of current time in seconds
		double time=(double)Time.getTime()/(double)Time.SECOND;
		double timeDecimals=time-(double)((int)time);
		
		//Can only detect player once each second
		if(timeDecimals<0.5){
			canLook=true;
		} else if(canLook)
		{
			Vector2f lineStart=new Vector2f(transform.getTranslation().getX(),transform.getTranslation().getZ());
			Vector2f castDirection=new Vector2f(orientation.getX(),orientation.getZ());
			Vector2f lineEnd=lineStart.add(castDirection.mul(SHOOT_DISTANCE));
			
			Vector2f collisionVector=Game.getLevel().checkIntersections(lineStart,lineEnd,FRIENDLY_FIRE);
			Vector2f playerIntersectVector = Game.getLevel().lineIntersectRect(lineStart, lineEnd,
					new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ()),
					new Vector2f(Player.PLAYER_SIZE, Player.PLAYER_SIZE));

			if(playerIntersectVector!=null && 
					(collisionVector==null || 
						playerIntersectVector.sub(lineStart).length() <
							collisionVector.sub(lineStart).length()))
			{
				System.out.println("We've seen the player");
				state=STATE.CHASE;
			}
			canLook=false;
		}
	}
	
	private void chaseUpdate(Vector3f orientation, float distance)
	{
		//if chasing of course
		//randomly attack after some time, will defiantly attack after 2sec
		if(rand.nextDouble()<ATTACK_CHANCE*Time.getDelta()){
			state=STATE.ATTACK;
		}
		//Attack if within range, otherwise chase
		if(distance>MOVEMENT_STOP_DISTANCE)
		{
			float moveAmount=MOVE_SPEED*(float)Time.getDelta();
			
			Vector3f oldPos=transform.getTranslation();
			Vector3f newPos=transform.getTranslation().add(orientation.mul(moveAmount));
			Vector3f collisionVector=Game.getLevel().checkCollision(oldPos,newPos,MONSTER_WIDTH,MONSTER_LENGTH);
			
			//Zero out directions involving collision
			Vector3f movementVector=collisionVector.mul(orientation);
						
			if(movementVector.length()>0)
			{
				transform.setTranslation(transform.getTranslation().add(movementVector.mul(moveAmount)));
			}
			
			if(movementVector.sub(orientation).length()!=0)
			{
				Game.getLevel().openDoors(transform.getTranslation());
			}
		} 
		else 
		{
			state=STATE.ATTACK;
		}
	}
	
	private void attackUpdate(Vector3f orientation, float distance)
	{
		//fractional portion of current time in seconds
		double time=(double)Time.getTime()/(double)Time.SECOND;
		double timeDecimals=time-(double)((int)time);
		
		//Can attack only once each second
		if(timeDecimals<0.5){
			canAttack=true;
		} else if(canAttack) {
			Vector2f lineStart=new Vector2f(transform.getTranslation().getX(),transform.getTranslation().getZ());
			Vector2f castDirection=new Vector2f(orientation.getX(),orientation.getZ()).rotate((rand.nextFloat()-0.5f)*SHOT_ANGLE);
			Vector2f lineEnd=lineStart.add(castDirection.mul(SHOOT_DISTANCE));
			
			Vector2f collisionVector=Game.getLevel().checkIntersections(lineStart,lineEnd,FRIENDLY_FIRE);
			Vector2f playerIntersectVector = Game.getLevel().lineIntersectRect(lineStart, lineEnd,
					new Vector2f(Transform.getCamera().getPos().getX(), Transform.getCamera().getPos().getZ()),
					new Vector2f(Player.PLAYER_SIZE, Player.PLAYER_SIZE));
	
			if(playerIntersectVector!=null && 
					(collisionVector==null || 
						playerIntersectVector.sub(lineStart).length() <
							collisionVector.sub(lineStart).length()))
			{
				System.out.println("We've just shot the player");
			}
			
			if(collisionVector==null)
			{
				System.out.println("shot missed everything");
			} else
			{
				System.out.println("Shot hit something");
			}
			state=STATE.CHASE;
			canAttack=false;
		}
	}
	
	private void dyingUpdate(Vector3f orientation, float distance)
	{
		state=STATE.DEAD;
	}
	
	private void deadUpdate(Vector3f orientation, float distance)
	{
		System.out.println("we're dead bro");
	}
	
}
