#Guided greedy algorithm
#Can find a solution, net necessarily optimal
import pygame
from game_objects import *
from tileC import Tile

#convert to class
def a_star(screen,player,total_frames,FPS):

	N=-18
	S=18
	W=-1
	E=1
	NW=-19
	NE=-17
	SW=17
	SE=19

	half=Tile.width//2

	#eliminate this function
	def blocky(tiles,diagonals,surrounding_tile):
		if surrounding_tile.number not in diagonals:
			tiles.append(surrounding_tile)
		return tiles
	def get_surrounding_tiles(parent_tile):
		surrounding_tile_numbers=(
		parent_tile.number+N,
		parent_tile.number+S,
		parent_tile.number+E,
		parent_tile.number+W,
		parent_tile.number+NW,
		parent_tile.number+NE,
		parent_tile.number+SW,
		parent_tile.number+SE)
		#print(surrounding_tile_numbers)
		tiles=[]
		pnum=parent_tile.number
		diagonals=[pnum+NE,pnum+NW,pnum+SE,pnum+SW]
		for tile_number in surrounding_tile_numbers:
			surrounding_tile=Tile.get_tile(tile_number)
			if surrounding_tile.walkable and surrounding_tile not in closed_list:
				#tiles.append(surrounding_tile) #consider diagonal directions
				blocky(tiles,diagonals,surrounding_tile)
		return tiles
	#eclidian distance from enemy to tile
	def G(tile):
		diff=tile.number-tile.parent.number
		if diff in (N,S,E,W): #H&V cost of 10
			tile.G=tile.parent.G+10
		elif diff in (NW,NE,SW,SE): #diagonal cost of 14
			tile.G=tile.parent.G+14
	#Manhattan Distance Heuristic, H+V distance from player
	def H():
		for tile in Tile.List:
			tile.H=10*(abs(tile.x-player.x)+abs(tile.y-player.y))//Tile.width
	#A Star value
	def F(tile):
		tile.F=tile.G+tile.H
	def discard(tile):
		open_list.remove(tile)
		closed_list.append(tile)
	def get_minFT():
		#note: use more efficient method
		F_Values=[]
		for tile in open_list:
			F_Values.append(tile.F)
		o=open_list[::-1] #reverse order, newest in front
		for tile in o:
			if tile.F==min(F_Values):
				return tile
	def calc_new_G(minFT,tile):
		newG=0
		diff=minFT.number-tile.number
		if diff in (N,S,E,W):
			newG=minFT.G+10
		elif diff in (NW,NE,SW,SE):
			newG=minFT.G+14
		return newG
	def loop(): #recursively solve for AStar
		minFT=get_minFT() #next min Astar value
		discard(minFT) #place in closed list, advance to next tile
		surrounding_tiles=get_surrounding_tiles(minFT)
		#Note: replace with set
		for tile in surrounding_tiles:
			if tile not in open_list:
				open_list.append(tile)
				tile.parent=minFT
			elif tile in open_list: #update G cost
				newG=calc_new_G(minFT,tile)
				if newG<tile.G:
					tile.parent=minFT
					G(tile) #G may change, H won't
					F(tile)
		#either no solution or found a solution then return
		#if no solution case, backtrack to position with
		#non empty open list
		if open_list==[] or player.get_tile() in closed_list:
			return
		for tile in open_list:
			G(tile)
			F(tile)
			pygame.draw.line(screen,[150,150,0],
				[tile.parent.x+half,tile.parent.y+half],
				[tile.x+half,tile.y+half])
		loop()

	for enemy in Enemy.List:
		open_list=[] #tiles to expand
		closed_list=[] #tiles already expanded
		enemy_tile=enemy.get_tile()
		open_list.append(enemy_tile)
		surrounding_tiles=get_surrounding_tiles(enemy_tile)
		for tile in surrounding_tiles:
			tile.parent=enemy_tile
			open_list.append(tile)
		discard(enemy_tile)
		H() #only ever needs to be calculated once, set board size
		for tile in surrounding_tiles:
			G(tile)
			F(tile)
		loop()
		#Find the return tile path, player->enemy
		return_tile_path=[]
		parent=player.get_tile()
		while True:
			return_tile_path.append(parent)
			parent=parent.parent
			if parent==None:
				break
			if parent.number==enemy.get_number():
				break
		for tile in return_tile_path:
			pygame.draw.circle(screen,[30,90,180],
			[tile.x+half,tile.y+half],5)
		if len(return_tile_path)>1:
			if total_frames%(FPS//4)==0:
				next_tile=return_tile_path[-1]
				enemy.x=next_tile.x
				enemy.y=next_tile.y