import pygame, sys
from tileC import Tile

def player_input(screen,player):
	Mpos=pygame.mouse.get_pos() #[x,y]
	Mx=Mpos[0]//Tile.width
	My=Mpos[1]//Tile.height
	#print("Mx="+str(Mx)+" My="+str(My))
    #for all events check for terminate
	for event in pygame.event.get():

		if event.type == pygame.QUIT:
			pygame.quit()
			sys.exit()

		if event.type == pygame.MOUSEBUTTONDOWN:
			#toggle tile solid/empty
			for tile in Tile.List:
				if tile.x==(Mx*Tile.width) and tile.y==(My*Tile.height):
					if tile.type=='empty':
						tile.type='solid'
						tile.walkable=False
					else:
						tile.type='empty'
						tile.walkable=True
					break

		if event.type == pygame.KEYDOWN:
			if event.key==pygame.K_w: #North
				future_tile_number=player.get_number()-Tile.V
				if Tile.get_tile(future_tile_number).walkable:
					player.y-=player.height
			if event.key==pygame.K_s: #South
				future_tile_number=player.get_number()+Tile.V
				if Tile.get_tile(future_tile_number).walkable:
					player.y+=player.height
			if event.key==pygame.K_a: #West
				future_tile_number=player.get_number()-Tile.H
				if Tile.get_tile(future_tile_number).walkable:
					player.x-=player.width
			if event.key==pygame.K_d: #East
				future_tile_number=player.get_number()+Tile.H
				if Tile.get_tile(future_tile_number).walkable:
					player.x+=player.width
