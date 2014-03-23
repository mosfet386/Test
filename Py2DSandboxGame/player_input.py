import pygame, sys
from tileC import Tile
from game_objects import Projectile

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

		#Wall Creation & Removal
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

		#Player Weapon Selection
		if event.type==pygame.KEYDOWN:
			if event.key==pygame.K_e:
				player.gun=(player.gun+1)%3 #gun 0,1,2,0,1,2

	#Player Movement
	# if event.type == pygame.KEYDOWN:
	keys=pygame.key.get_pressed()
	if keys[pygame.K_w]: #North
		future_tile_number=player.get_number()-Tile.V
		if future_tile_number in range(1,Tile.total_tiles+1):
			future_tile=Tile.get_tile(future_tile_number)
			if future_tile.walkable:
				player.set_target(future_tile)
				player.rotate('n')
				# player.y-=player.height
	if keys[pygame.K_s]: #South
		future_tile_number=player.get_number()+Tile.V
		if future_tile_number in range(1,Tile.total_tiles+1):
			future_tile=Tile.get_tile(future_tile_number)
			if future_tile.walkable:
				player.set_target(future_tile)
				player.rotate('s')
				# player.y+=player.height
	if keys[pygame.K_a]: #West
		future_tile_number=player.get_number()-Tile.H
		if future_tile_number in range(1,Tile.total_tiles+1):
			future_tile=Tile.get_tile(future_tile_number)
			if future_tile.walkable:
				player.set_target(future_tile)
				player.rotate('w')
				# player.x-=player.width
	if keys[pygame.K_d]: #East
		future_tile_number=player.get_number()+Tile.H
		if future_tile_number in range(1,Tile.total_tiles+1):
			# print("Out of bounds",future_tile_number,Tile.get_tile(future_tile_number))
			future_tile=Tile.get_tile(future_tile_number)
			if future_tile.walkable:
				player.set_target(future_tile)
				player.rotate('e')
				# player.x+=player.width
	
	#Player Rotation
	if keys[pygame.K_LEFT]:
		player.rotate('w')
		Projectile(player.centerx,player.centery,-10,0,'w',player.get_bullet_type())
	elif keys[pygame.K_RIGHT]:
		player.rotate('e')
		Projectile(player.centerx,player.centery,10,0,'e',player.get_bullet_type())
	elif keys[pygame.K_UP]:
		player.rotate('n')
		Projectile(player.centerx,player.centery,0,-10,'n',player.get_bullet_type())
	elif keys[pygame.K_DOWN]:
		player.rotate('s')
		Projectile(player.centerx,player.centery,0,10,'s',player.get_bullet_type())