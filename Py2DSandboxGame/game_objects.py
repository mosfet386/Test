import pygame
from tileC import Tile
from random import randint

class Character(pygame.Rect):

	width,height=32,32

	def __init__(self,x,y):
		pygame.Rect.__init__(self,x,y,Character.width,Character.height)
		self.tx,self.ty=None,None
	def __str__(self):
		return str(self.get_number())
	#return the tile number the character is on
	def get_number(self):
		return (self.x//self.width)+(self.y//self.height)*Tile.V+Tile.H
	#return the tile object the character is on
	def get_tile(self):
		return Tile.get_tile(self.get_number())
	def set_target(self,next_tile):
		if self.tx==None and self.ty==None:
			self.tx=next_tile.x
			self.ty=next_tile.y
	def rotate(self,direction,original_image):
		if direction=='n' and self.direction!='n':
			self.direction = 'n'
			#rotate image 90deg CCW then flip across y axis
			south=pygame.transform.rotate(original_image,90)
			self.image=pygame.transform.flip(south,False,True)
		if direction=='s' and self.direction!='s':
			self.direction = 's'
			#rotate image 90deg CCW
			self.image=pygame.transform.rotate(original_image,90)				
		if direction=='w' and self.direction!='w':
			self.direction = 'w'
			self.image=original_image	
		if direction=='e' and self.direction!='e':
			self.direction = 'e'
			self.image=pygame.transform.flip(original_image,True,False)

class Enemy(Character):

	List=[]
	spawn_tiles=(9,42,91,134,193,219,274)
	original_image=pygame.image.load('res/enemy.png')
	health=100

	def __init__(self,x,y):
		Character.__init__(self,x,y)
		Enemy.List.append(self)
		self.direction='w'
		self.image=Enemy.original_image
		self.health=Enemy.health
	@staticmethod
	def spawn(total_frames,FPS):
		if total_frames%(FPS*3)==0:
			r=randint(0,2)
			sounds=[pygame.mixer.Sound('res/enemySound1.wav'),
					pygame.mixer.Sound('res/enemySound2.wav'),
					pygame.mixer.Sound('res/enemySound3.wav')]
			sound=sounds[r]
			sound.play()
			r=randint(0,len(Enemy.spawn_tiles)-1)
			spawn_tile=Tile.get_tile(Enemy.spawn_tiles[r])
			Enemy(spawn_tile.x,spawn_tile.y)
	@staticmethod
	def update(screen,player):
		#Draw enemies
		for enemy in Enemy.List:
			#pygame.draw.rect(screen,[250,0,0],enemy)
			screen.blit(enemy.image,(enemy.x,enemy.y))
			if enemy.health<=0:
				Enemy.List.remove(enemy)
			#apply enemy damage to player
			if player.x%Tile.width==0 and player.y%Tile.height==0:
				if enemy.x%Tile.width==0 and enemy.y%Tile.height==0:
					tileNumber=player.get_number()
					N=tileNumber-Tile.V
					S=tileNumber+ Tile.V
					E=tileNumber+ Tile.H
					W=tileNumber-Tile.H
					NSEW=[N,S,E,W,tileNumber]
					for i in NSEW:
						pygame.draw.rect(screen,[66,134,122],Tile.get_tile(i))
					if enemy.get_number() in NSEW:
						player.health-=5
			#orientate and move enemies
			if enemy.tx!=None and enemy.ty!=None:
				vel=4 #be sure tile size is divisible by vel ie 32/4=8
				X=enemy.tx-enemy.x
				Y=enemy.ty-enemy.y
				if X<0:
					enemy.x-=vel
					enemy.rotate('w',Enemy.original_image)
				elif X>0:
					enemy.x+=vel
					enemy.rotate('e',Enemy.original_image)
				if Y<0:
					enemy.y-=vel
					enemy.rotate('n',Enemy.original_image)
				elif Y>0:
					enemy.y+=vel
					enemy.rotate('s',Enemy.original_image)
				if X==0 and Y==0:
					enemy.tx,enemy.ty=None,None
			
class Player(Character):

	guns_image=[pygame.image.load('res/pistol.png'),
				pygame.image.load('res/shotgun.png'),
				pygame.image.load('res/automatic.png')]

	def __init__(self,x,y):
		Character.__init__(self,x,y)
		self.image=pygame.image.load('res/player_w.png')
		self.direction='w'
		self.gun=0
		self.health=1000
	def __str__(self):
		return str(self.get_number())
	def draw_player(self,screen):
		half=(self.width)//4
		# r=self.width//2
		# pygame.draw.circle(screen,[0,0,255],(self.x+r,self.y+r),r)
		screen.blit(self.image,(self.x,self.y))
		image=Player.guns_image[self.gun]
		if self.direction=='w':
			screen.blit(image,(self.x,self.y+half))
		elif self.direction=='e':
			image=pygame.transform.flip(image,True,False)
			screen.blit(image,(self.x+2*half,self.y+half))
		elif self.direction=='s':
			image=pygame.transform.rotate(image,90)
			screen.blit(image,(self.x+half,self.y+2*half))
		elif self.direction=='n':
			image=pygame.transform.rotate(image,90)
			image=pygame.transform.flip(image,False,True)
			screen.blit(image,(self.x+half,self.y))
	def movement(self):
		vel=8 #be sure tile size is divisible by vel ie 32/4=8
		if self.tx!=None and self.ty!=None:
			X=self.tx-self.x
			Y=self.ty-self.y
			if X<0:
				self.x-=vel
			elif X>0:
				self.x+=vel
			if Y<0:
				self.y-=vel
			elif Y>0:
				self.y+=vel
			if X==0 and Y==0:
				self.tx,self.ty=None,None
	def rotate(self, direction):
		path = 'res/player_'
		ext = '.png'
		if direction == 'n':
			if self.direction != 'n':
				self.direction = 'n'
				self.image = pygame.image.load(path + self.direction + ext)
		if direction == 's':
			if self.direction != 's':
				self.direction = 's'
				self.image = pygame.image.load(path + self.direction + ext)
		if direction == 'e':
			if self.direction != 'e':
				self.direction = 'e'
				self.image = pygame.image.load(path + self.direction + ext)
		if direction == 'w':
			if self.direction != 'w':
				self.direction = 'w'
				self.image = pygame.image.load(path + self.direction + ext)
	def get_bullet_type(self):
		if self.gun==0:
			return 'pistol'
		elif self.gun==1:
			return 'shotgun'
		elif self.gun==2:
			return 'automatic'

class Projectile(pygame.Rect):

	List=[]
	width,height=7,10
	projectile_image={'pistol':pygame.image.load('res/pistol_projectile.png'),
					'shotgun':pygame.image.load('res/shotgun_projectile.png'),
					'automatic':pygame.image.load('res/automatic_projectile.png')}
	gun_damage={'pistol':(Enemy.health//3)+1,
				'shotgun':Enemy.health//2,
				'automatic':(Enemy.health//6)+1}

	def __init__(self,x,y,velx,vely,direction,projectileType):
		if projectileType in ('shotgun','pistol'):
			try:
				dx=abs(Projectile.List[-1].x-x)
				dy=abs(Projectile.List[-1].y-y)
				if dx<50 and dy<50 and projectileType=='shotgun':
					return
				if dx<30 and dy<30 and projectileType=='pistol':
					return
			except:
				pass
		pygame.Rect.__init__(self,x,y,Projectile.width,Projectile.height)
		self.projectileType=projectileType
		self.direction=direction
		self.velx,self.vely=velx,vely
		if direction=='n':
			south=pygame.transform.rotate(Projectile.projectile_image[projectileType],90)
			self.image=pygame.transform.flip(south,False,True)
		if direction=='s':
			self.image=pygame.transform.rotate(Projectile.projectile_image[projectileType],90)				
		if direction=='w':
			self.image=Projectile.projectile_image[projectileType]	
		if direction=='e':
			self.image=pygame.transform.flip(Projectile.projectile_image[projectileType],True,False)
		Projectile.List.append(self)
	def offscreen(self,screen):
		#examine the four edges of the screen
		if self.x<0:
			return True
		elif self.y<0:
			return True
		elif self.x+self.width>screen.get_width():
			return True
		elif self.y+self.height>screen.get_height():
			return True
		else:
			return False
	@staticmethod
	def projectile_physics(screen):
		for projectile in Projectile.List:
			projectile.x+=projectile.velx
			projectile.y+=projectile.vely
			screen.blit(projectile.image,(projectile.x,projectile.y))
			#projectile offscreen
			if projectile.offscreen(screen):
				Projectile.List.remove(projectile)
			for enemy in Enemy.List:
				if projectile.colliderect(enemy):
					enemy.health-=Projectile.gun_damage[projectile.projectileType]
					Projectile.List.remove(projectile)
					return
			for tile in Tile.List:
				if projectile.colliderect(tile) and not(tile.walkable):
					try:
						Projectile.List.remove(projectile)
					except:
						break
