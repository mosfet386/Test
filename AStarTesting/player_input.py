import pygame, sys
from tileC import Tile

def player_input(screen,player):
    #for all events check for terminate
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            sys.exit()
        if event.type==pygame.KEYDOWN:
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

