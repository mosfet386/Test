import pygame
from tileC import Tile

class Character(pygame.Rect):

	width,height=40,40

	def __init__(self,x,y):
		pygame.Rect.__init__(self,x,y,Character.width,Character.height)
	def __str__(self):
		return str(self.get_number())
	#return the tile number the character is on
	def get_number(self):
		return (self.x/self.width)+(self.y/self.height)*Tile.V+Tile.H
	#return the tile object the character is on
	def get_tile(self):
		return Tile.get_tile(self.get_number)

class Enemy(Character):

	List=[]

	def __init__(self,x,y):
		Character.__init__(self,x,y)
		Enemy.List.append(self)
	@staticmethod
	def draw_enemies(screen):
		for enemy in Enemy.List:
			pygame.draw.rect(screen,[255,0,0],enemy)

class Player(Character):

	def __init__(self,x,y):
		Character.__init__(self,x,y)
	def __str__(self):
		return str(self.get_number())
	def draw_player(self,screen):
		r=self.width//2
		pygame.draw.circle(screen,[0,0,255],(self.x+r,self.y+r),r)