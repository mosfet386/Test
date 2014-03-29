package com.base.engine;

import java.util.ArrayList;

public class Level {

	//Map is comprised of cubes with these dimensions
	private static final float SPOT_WIDTH=1;
	private static final float SPOT_LENGHT=1;
	private static final float SPOT_HEIGHT=1;
	
	//Using a texture sheet, with 2^NUM_TEX_EXP entries
	private static final int NUM_TEX_EXP=4;
	private static final int NUM_TEXTURES=(int)Math.pow(2,NUM_TEX_EXP);
	
	private Mesh mesh;
	private Bitmap level;
	private Shader shader;
	private Material material;
	private Transform transform;
	
	public Level(String levelName, String textureName){
		
		level=new Bitmap(levelName).flipY();
		transform=new Transform();
		material=new Material(new Texture(textureName));
		shader=BasicShader.getInstance();
		generateLevel();
	}
	
	public void input(){
		
	}
	
	public void update(){
		
	}
	
	public void render(){
		shader.bind();
		shader.updateUniforms(transform.getTransformation(), 
								transform.getProjectedTransformation(), 
								material);
		mesh.draw();
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
			vertices.add(new Vertex(new Vector3f(i*SPOT_WIDTH,offset,j*SPOT_LENGHT), new Vector2f(textCoords[1],textCoords[3])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,offset,j*SPOT_LENGHT), new Vector2f(textCoords[0],textCoords[3])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,offset,(j+1)*SPOT_LENGHT), new Vector2f(textCoords[0],textCoords[2])));
			vertices.add(new Vertex(new Vector3f(i*SPOT_WIDTH,offset,(j+1)*SPOT_LENGHT), new Vector2f(textCoords[1],textCoords[2])));
		}else if(x && y){
			vertices.add(new Vertex(new Vector3f(i*SPOT_WIDTH,offset,j*SPOT_LENGHT), new Vector2f(textCoords[1],textCoords[2])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,offset,j*SPOT_LENGHT), new Vector2f(textCoords[0],textCoords[2])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,SPOT_HEIGHT,j*SPOT_LENGHT), new Vector2f(textCoords[0],textCoords[3])));
			vertices.add(new Vertex(new Vector3f(i*SPOT_WIDTH,SPOT_HEIGHT,j*SPOT_LENGHT), new Vector2f(textCoords[1],textCoords[3])));
		}else if(y && z){
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,offset,(j+1)*SPOT_LENGHT), new Vector2f(textCoords[1],textCoords[2])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,offset,j*SPOT_LENGHT), new Vector2f(textCoords[0],textCoords[2])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,SPOT_HEIGHT,j*SPOT_LENGHT), new Vector2f(textCoords[0],textCoords[3])));
			vertices.add(new Vertex(new Vector3f((i+1)*SPOT_WIDTH,SPOT_HEIGHT,(j+1)*SPOT_LENGHT), new Vector2f(textCoords[1],textCoords[3])));
		}
			
	}
	
	private void generateLevel(){
		//Construct all walls and floors
		ArrayList<Vertex> vertices=new ArrayList<Vertex>();
		ArrayList<Integer> indices=new ArrayList<Integer>();
		for(int i=0; i<level.getWidth(); i++){
			for(int j=0; j<level.getHeight(); j++){
				
				//if section is a wall continue, black pixels
				if(0==(level.getPixel(i,j) & 0xFFFFFF)){continue;}
				
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
					addFace(indices,vertices.size(),false);
					addVertices(vertices,i,j,0,true,true,false,textCoords);
				}
				//for north face walls (black pixel above white pixel)
				if(0==(level.getPixel(i,j+1) & 0xFFFFFF)){
					addFace(indices,vertices.size(),true);
					addVertices(vertices,i,j+1,0,true,true,false,textCoords);
				}
				//for east face walls (black pixel right of white pixel)
				if(0==(level.getPixel(i+1,j) & 0xFFFFFF)){
					addFace(indices,vertices.size(),true);
					addVertices(vertices,i,j,0,false,true,true,textCoords);
				}
				//for west face walls (black pixel left of white pixel)
				if(0==(level.getPixel(i-1,j) & 0xFFFFFF)){
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
			Vector2f blockSize=new Vector2f(SPOT_WIDTH,SPOT_LENGHT);
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
}
