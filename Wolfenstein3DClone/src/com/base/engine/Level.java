package com.base.engine;

import java.util.ArrayList;

import com.base.engine.Monster.STATE;

public class Level {

	//Map is comprised of cubes with these dimensions
	private static final float SPOT_WIDTH=1;
	private static final float SPOT_LENGTH=1;
	private static final float SPOT_HEIGHT=1;
	private static final float PLAYER_OFFSET_FROM_GROUND=0.4375f;
	
	//Using a texture sheet, with 2^NUM_TEX_EXP entries
	private static final int NUM_TEX_EXP=4;
	private static final int NUM_TEXTURES=(int)Math.pow(2,NUM_TEX_EXP);
	private static final float OPEN_DISTANCE=1.0f;
	private static final float DOOR_OPEN_MOVEMENT_AMOUNT=0.9f;
	
	private Mesh mesh;
	private Bitmap level;
	private Shader shader;
	private Material material;
	private Transform transform;
	
	private Player player;
	private ArrayList<Door> doors;
	private ArrayList<Monster> monsters;
	private ArrayList<Medkit> medkits;
	private ArrayList<Vector3f> exitPoints;
	
	private ArrayList<Medkit> medkitsToRemove;
	
	private ArrayList<Vector2f> collisionPosStart;
	private ArrayList<Vector2f> collisionPosEnd;
	
	public Level(String levelName, String textureName){
		
		level=new Bitmap(levelName).flipY();
		transform=new Transform();
		material=new Material(new Texture(textureName));
		shader=BasicShader.getInstance();
		
		//this.player=player;
		
		generateLevel();
//		Transform tempTransform=new Transform();
//		tempTransform.setTranslation(new Vector3f(7f,0.0f,4f));
//		monsters.add(new Monster(tempTransform));
	}
	
	public void input(){
		player.input();
	}
	
	public void update(){
		for(Door door:doors){door.update();}
		player.update();
		for(Medkit medkit:medkitsToRemove)
		{
			medkits.remove(medkit);
			//medkitsToRemove.remove(medkit);
		}
		for(Medkit medkit:medkits){medkit.update();}
		for(Monster monster:monsters){monster.update();}
	}
	
	public void render(){
		shader.bind();
		shader.updateUniforms(transform.getTransformation(), 
								transform.getProjectedTransformation(), 
								material);
		mesh.draw();
		for(Door door:doors){door.render();}
		for(Monster monster:monsters){monster.render();}
		for(Medkit medkit:medkits){medkit.render();}
		player.render();
	}
	
	public Shader getShader(){
		return shader;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void addDoor(int x, int y){
		//default transformation position&rotation&scale
		Transform doorTransform=new Transform();
		
		//should door be oriented along the xAxis?
		boolean xDoor=(level.getPixel(x,y-1)&0xFFFFFF)==0 && 
							(level.getPixel(x,y+1)&0xFFFFFF)==0;
		//should door be oriented along the yAxis?
		boolean yDoor=(level.getPixel(x-1,y)&0xFFFFFF)==0 && 
							(level.getPixel(x+1,y)&0xFFFFFF)==0;
		
		if(!(xDoor^yDoor)){
			System.err.println("Error: You've placed a door in an invalid location");
			new Exception().printStackTrace();
			System.exit(1);
		}
		
		Vector3f openPosition=null;
		if(yDoor){
			doorTransform.setTranslation(x, 0, y + SPOT_LENGTH / 2);
			openPosition = doorTransform.getTranslation().sub(new Vector3f(DOOR_OPEN_MOVEMENT_AMOUNT, 0.0f, 0.0f));
		}

		if(xDoor){
			doorTransform.setTranslation(x + SPOT_WIDTH / 2, 0, y);
			doorTransform.setRotation(0, 90, 0);
			openPosition = doorTransform.getTranslation().sub(new Vector3f(0.0f, 0.0f, DOOR_OPEN_MOVEMENT_AMOUNT));
		}
		
		//using level material
		doors.add(new Door(doorTransform,material,openPosition));
	}
		
	public void addSpecial(int blueValue, int x, int y){
		if(blueValue==16){addDoor(x,y);}
		if(blueValue==1){
			player=new Player(
					new Vector3f((x+0.5f)*SPOT_WIDTH,PLAYER_OFFSET_FROM_GROUND,(y+0.5f)*SPOT_LENGTH));
		}
		if(blueValue==128){
			Transform monsterTransform=new Transform();
			monsterTransform.setTranslation((x+0.5f)*SPOT_WIDTH,0,(y+0.5f)*SPOT_LENGTH);
			monsters.add(new Monster(monsterTransform));
		}
		if(blueValue==192){
			Vector3f medkitPos=new Vector3f((x+0.5f)*SPOT_WIDTH,0,(y+0.5f)*SPOT_LENGTH);
			medkits.add(new Medkit(medkitPos));
		}
		if(blueValue==97){
			exitPoints.add(new Vector3f((x+0.5f)*SPOT_WIDTH,0,(y+0.5f)*SPOT_LENGTH));
		}
	}
	
	private void addFace(ArrayList<Integer> indices, int startLocation, boolean direction){
		//Construct Square
		if(direction){
			//Triangle 1
			indices.add(startLocation+2);
			indices.add(startLocation+1);
			indices.add(startLocation+0);
			//Triangle 2
			indices.add(startLocation+3);
			indices.add(startLocation+2);
			indices.add(startLocation+0);	
		}else{
			//Triangle 1
			indices.add(startLocation+0);
			indices.add(startLocation+1);
			indices.add(startLocation+2);
			//Triangle 2
			indices.add(startLocation+0);
			indices.add(startLocation+2);
			indices.add(startLocation+3);	
		}
	}
	
	private float[] calcTextCoords(int sheetOffset){
		//color mapping, using NUM_TEXTURES
		//bottom-to-top right-to-left
		//every 16 increment corresponds to another texture
		int texX=sheetOffset/NUM_TEXTURES;
		int texY=texX % NUM_TEX_EXP; //row 0-3 bottom to top
		texX/=NUM_TEX_EXP; //column 0-3, from right to left
		
		//texture coordinates
		//1f-value, since extracting textures from right to left
		float XHigher=1f-(float)texX/(float)NUM_TEX_EXP;
		float XLower=XHigher-1f/(float)NUM_TEX_EXP;
		float YLower=1f-(float)texY/(float)NUM_TEX_EXP;
		float YHigher=YLower-1f/(float)NUM_TEX_EXP;
		
		return new float[] {XHigher,XLower,YLower,YHigher};
	}
	
	private void addVertices(ArrayList<Vertex>vertices, int i, int j,
			 float offset, boolean x, boolean y, boolean z, float[] textCoords){
		if(x && z){ //add floor
			vertices.add(new Vertex(new Vector3f(i*SPOT_WIDTH,offset,j*SPOT_LENGTH), new Vector2f(textCoords[1],textCoords[3])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,offset,j*SPOT_LENGTH), new Vector2f(textCoords[0],textCoords[3])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,offset,(j+1)*SPOT_LENGTH), new Vector2f(textCoords[0],textCoords[2])));
			vertices.add(new Vertex(new Vector3f(i*SPOT_WIDTH,offset,(j+1)*SPOT_LENGTH), new Vector2f(textCoords[1],textCoords[2])));
		}else if(x && y){
			vertices.add(new Vertex(new Vector3f(i*SPOT_WIDTH,offset,j*SPOT_LENGTH), new Vector2f(textCoords[1],textCoords[2])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,offset,j*SPOT_LENGTH), new Vector2f(textCoords[0],textCoords[2])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,SPOT_HEIGHT,j*SPOT_LENGTH), new Vector2f(textCoords[0],textCoords[3])));
			vertices.add(new Vertex(new Vector3f(i*SPOT_WIDTH,SPOT_HEIGHT,j*SPOT_LENGTH), new Vector2f(textCoords[1],textCoords[3])));
		}else if(y && z){
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,offset,(j+1)*SPOT_LENGTH), new Vector2f(textCoords[1],textCoords[2])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,offset,j*SPOT_LENGTH), new Vector2f(textCoords[0],textCoords[2])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,SPOT_HEIGHT,j*SPOT_LENGTH), new Vector2f(textCoords[0],textCoords[3])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,SPOT_HEIGHT,(j+1)*SPOT_LENGTH), new Vector2f(textCoords[1],textCoords[3])));
		}
			
	}
	
	private void generateLevel(){
		doors=new ArrayList<Door>();
		monsters=new ArrayList<Monster>();
		medkits=new ArrayList<Medkit>();
		medkitsToRemove=new ArrayList<Medkit>();
		collisionPosStart=new ArrayList<Vector2f>();
		collisionPosEnd=new ArrayList<Vector2f>();
		exitPoints=new ArrayList<Vector3f>();
		
		//Construct all walls and floors
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();
		for(int i=0; i<level.getWidth(); i++){
			for(int j=0; j<level.getHeight(); j++){
				
				//if section is a wall continue, black pixels
				if(0==(level.getPixel(i,j) & 0xFFFFFF)){continue;}
				
				//pass in blue channel
				addSpecial(level.getPixel(i,j) & 0x0000FF, i, j);
				
				float[] textCoords=calcTextCoords((level.getPixel(i,j)&0x00FF00)>>8);
				
				//Create Floor
				addFace(indices,vertices.size(),true);
				addVertices(vertices,i,j,0,true,false,true,textCoords);

				//Create Ceiling
				addFace(indices,vertices.size(),false);
				addVertices(vertices,i,j,SPOT_HEIGHT,true,false,true,textCoords);

				//Create Walls
				textCoords=calcTextCoords((level.getPixel(i,j)&0xFF0000)>>16);
				//for south face walls (black pixel below white pixel)
				if(0==(level.getPixel(i,j-1) & 0xFFFFFF)){
					collisionPosStart.add(new Vector2f(i*SPOT_WIDTH,j*SPOT_LENGTH));
					collisionPosEnd.add(new Vector2f((i+1)*SPOT_WIDTH,j*SPOT_LENGTH));
					addFace(indices,vertices.size(),false);
					addVertices(vertices,i,j,0,true,true,false,textCoords);
				}
				//for north face walls (black pixel above white pixel)
				if(0==(level.getPixel(i,j+1) & 0xFFFFFF)){
					collisionPosStart.add(new Vector2f(i*SPOT_WIDTH,(j+1)*SPOT_LENGTH));
					collisionPosEnd.add(new Vector2f((i+1)*SPOT_WIDTH,(j+1)*SPOT_LENGTH));
					addFace(indices,vertices.size(),true);
					addVertices(vertices,i,j+1,0,true,true,false,textCoords);
				}
				//for east face walls (black pixel right of white pixel)
				if(0==(level.getPixel(i+1,j) & 0xFFFFFF)){
					collisionPosStart.add(new Vector2f((i+1)*SPOT_WIDTH,j*SPOT_LENGTH));
					collisionPosEnd.add(new Vector2f((i+1)*SPOT_WIDTH,(j+1)*SPOT_LENGTH));
					addFace(indices,vertices.size(),true);
					addVertices(vertices,i,j,0,false,true,true,textCoords);
				}
				//for west face walls (black pixel left of white pixel)
				if(0==(level.getPixel(i-1,j) & 0xFFFFFF)){
					collisionPosStart.add(new Vector2f(i*SPOT_WIDTH,j*SPOT_LENGTH));
					collisionPosEnd.add(new Vector2f(i*SPOT_WIDTH,(j+1)*SPOT_LENGTH));
					addFace(indices,vertices.size(),false);
					addVertices(vertices,i-1,j,0,false,true,true,textCoords);
				}else{
//					System.err.println("Invalid plane used in level generator.");
//					new Exception().printStackTrace();System.exit(1);
				}
			}
		}
		Vertex[] vertArray=new Vertex[vertices.size()];
		Integer[] intArray=new Integer[indices.size()];
		vertices.toArray(vertArray);
		indices.toArray(intArray);
		mesh=new Mesh(vertArray,Util.toIntArray(intArray));
	}

	//player old&new positions, player width & length
	public Vector3f checkCollision(Vector3f oldPos, Vector3f newPos, 
									float objectWidth, float objectLength){
		Vector2f collisionVector=new Vector2f(1,1);
		Vector3f movementVector=newPos.sub(oldPos);
		if(movementVector.length()>0){
			Vector2f blockSize=new Vector2f(SPOT_WIDTH,SPOT_LENGTH);
			Vector2f objectSize=new Vector2f(objectWidth,objectLength);
			
			Vector2f oldPos2=new Vector2f(oldPos.getX(),oldPos.getZ());
			Vector2f newPos2=new Vector2f(newPos.getX(),newPos.getZ());
			
			//here were checking every black pixel, all walls Inefficient!!!
			for(int i=0;i<level.getWidth();i++){
				for(int j=0;j<level.getHeight();j++){
					if(0 == (level.getPixel(i,j) & 0xFFFFFF)){
						collisionVector=collisionVector.mul(rectCollide(oldPos2,newPos2,objectSize,blockSize.mul(new Vector2f(i,j)),blockSize));
					}
				}
			}
			
			for(Door door:doors){
				Vector2f doorSize=door.getDoorSize();
				Vector3f doorPos3f=door.getTransform().getTranslation();
				Vector2f doorPos2f=new Vector2f(doorPos3f.getX(),doorPos3f.getZ());
				collisionVector=collisionVector.mul(rectCollide(oldPos2,newPos2,objectSize,doorPos2f,doorSize));
			}
		}
		return new Vector3f(collisionVector.getX(),0,collisionVector.getY());
	}
	
	//old&new positions, player size, , object position, object cell size
	private Vector2f rectCollide(Vector2f oldPos, Vector2f newPos, 
						Vector2f size1, Vector2f pos2, Vector2f size2){
		Vector2f result=new Vector2f(0,0);
		if(newPos.getX()+size1.getX() < pos2.getX() || //to left of wall
			newPos.getX()-size1.getX() > pos2.getX()+size2.getX()*size2.getX() || //to the right of wall
			oldPos.getY()+size1.getY() < pos2.getY() || //south of the wall
			oldPos.getY()-size1.getY() > pos2.getY()+size2.getY()*size2.getY()) //north of the wall
		{
			result.setX(1);
		}
		if(oldPos.getX()+size1.getX() < pos2.getX() || //to left of wall
			oldPos.getX()-size1.getX() > pos2.getX()+size2.getX()*size2.getX() || //to the right of wall
			newPos.getY()+size1.getY() < pos2.getY() || //south of the wall
			newPos.getY()-size1.getY() > pos2.getY()+size2.getY()*size2.getY()) //north of the wall
			{
				result.setY(1);
			}
		
		return result;
	}
	
	private float vector2fCross(Vector2f a, Vector2f b){
		return a.getX()*b.getY()-a.getY()*b.getX();
	}
	
	//http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
	private Vector2f lineIntersect(Vector2f lineStart1, Vector2f lineEnd1, 
									Vector2f lineStart2, Vector2f lineEnd2){
		
		Vector2f line1=lineEnd1.sub(lineStart1);
		Vector2f line2=lineEnd2.sub(lineStart2);
		float cross=vector2fCross(line1,line2);
		
		//if lines are parallel return null, can't intersect
		if(cross==0){return null;}
		
		Vector2f distanceBetweenLineStarts=lineStart2.sub(lineStart1);
		float a=vector2fCross(distanceBetweenLineStarts,line2)/cross;
		float b=vector2fCross(distanceBetweenLineStarts,line1)/cross;
		if(0.0f<a && a<1.0f && 0.0f<b && b<1.0f)
			return lineStart1.add(line1.mul(a));
		
		return null;
	}
	
	public Vector2f findNearestVector2f(Vector2f a, Vector2f b, 
										Vector2f positionRelativeTo){
		if(b!=null && 
				(a==null || 
					a.sub(positionRelativeTo).length() > 
							b.sub(positionRelativeTo).length()))
		{
			return b;
		}
		return a;
	}
	
	//Check if object (pos,size) intersects with lineStart-lineEnd
	public Vector2f lineIntersectRect(Vector2f lineStart, Vector2f lineEnd, 
											Vector2f rectPos, Vector2f rectSize)
	{
		Vector2f result = null;

		Vector2f collisionVector = lineIntersect(lineStart, lineEnd, rectPos, new Vector2f(rectPos.getX() + rectSize.getX(), rectPos.getY()));
		result = findNearestVector2f(result, collisionVector, lineStart);

		collisionVector = lineIntersect(lineStart, lineEnd, rectPos, new Vector2f(rectPos.getX(), rectPos.getY() + rectSize.getY()));
		result = findNearestVector2f(result, collisionVector, lineStart);

		collisionVector = lineIntersect(lineStart, lineEnd, new Vector2f(rectPos.getX(), rectPos.getY() + rectSize.getY()), rectPos.add(rectSize));
		result = findNearestVector2f(result, collisionVector, lineStart);

		collisionVector = lineIntersect(lineStart, lineEnd, new Vector2f(rectPos.getX() + rectSize.getX(), rectPos.getY()), rectPos.add(rectSize));
		result = findNearestVector2f(result, collisionVector, lineStart);

		return result;
	}
	
	public Vector2f checkIntersections(Vector2f lineStart, Vector2f lineEnd, 
															boolean hurtMonsters)
	{
		Vector2f nearestIntersection=null;
		for(int i=0;i<collisionPosStart.size();i++)
		{
			Vector2f collisionVector=lineIntersect(lineStart,lineEnd,
								collisionPosStart.get(i),collisionPosEnd.get(i));

			//is collisionVector the nearest intersection on lineStart-linEnd?
			nearestIntersection=findNearestVector2f(nearestIntersection,collisionVector,lineStart);
		}
		
		for(Door door:doors)
		{
			Vector2f doorSize=door.getDoorSize();
			Vector3f doorPos3f=door.getTransform().getTranslation();
			Vector2f doorPos2f=new Vector2f(doorPos3f.getX(),doorPos3f.getZ());
			
			//Check if line between lineStart-lineEnd intersects with door
			Vector2f collisionVector=lineIntersectRect(lineStart,lineEnd,
													doorPos2f,doorSize);

			//is collisionVector the nearest intersection on lineStart-linEnd?
			nearestIntersection=findNearestVector2f(nearestIntersection,collisionVector,lineStart);
		}
		
		if(hurtMonsters)
		{
			Vector2f nearestMonsterIntersect=null;
			Monster nearestMonster=null;
			for(Monster monster:monsters)
			{
				Vector2f monsterSize=monster.getSize();
				Vector3f monsterPos3f=monster.getTransform().getTranslation();
				Vector2f monsterPos2f=new Vector2f(monsterPos3f.getX(),monsterPos3f.getZ());
				
				//Check if line between lineStart-lineEnd intersects with monster
				Vector2f collisionVector=lineIntersectRect(lineStart,lineEnd,
														monsterPos2f,monsterSize);
				
				//is collisionVector the nearest intersection on lineStart-linEnd?
				nearestMonsterIntersect=findNearestVector2f(nearestMonsterIntersect,collisionVector,lineStart);
			
				if(nearestMonsterIntersect==collisionVector){nearestMonster=monster;}
			}
			if(nearestMonsterIntersect!=null && 
					(nearestIntersection==null || 
							nearestMonsterIntersect.sub(lineStart).length() <
								nearestIntersection.sub(lineStart).length()))
			{
//				System.out.println("Monster was hit");
				nearestMonster.damage(player.getDamage());
			}
		}
		
		return nearestIntersection;
	}
	
	public void openDoors(Vector3f position, boolean tryExitLevel){
		for(Door door:doors){
			if(door.getTransform().getTranslation().sub(position).length()<OPEN_DISTANCE){
				door.open();
			}
		}
		if(tryExitLevel){
			for(Vector3f exitpoint:exitPoints){
				if(exitpoint.sub(position).length()<OPEN_DISTANCE){
					Game.loadNextLevel();
				}
			}
		}
	}

	public void damagePlayer(int amount)
	{
		player.damage(amount);
	}

	public void removeMedkit(Medkit medkit)
	{
		medkitsToRemove.add(medkit);
	}
}
