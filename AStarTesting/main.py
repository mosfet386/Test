#http://www.policyalmanac.org/games/aStarTutorial.htm
import pygame, sys
from tileC import Tile
import utils
from game_objects import *
from player_input import player_input
from a_star import a_star

print(sys.version)

pygame.init()
pygame.font.init()
screen = pygame.display.set_mode((720,440))

clock=pygame.time.Clock()
FPS=24
total_frames=0

#border tiles
invalids=(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,
          19,37,55,73,91,109,127,145,163,181,
          182,183,184,185,186,187,188,189,190,
          191,192,193,194,195,196,197,198,
          36,54,72,90,108,126,144,162,180,198)

for y in range(0,screen.get_height(),40):
	for x in range(0,screen.get_width(),40):
		if Tile.total_tiles in invalids:
			Tile(x,y,'solid')
		else:
			Tile(x,y,'empty')

#positions must be divisible by tile dimensions (40)
enemy1=Enemy(200,240)
player=Player(400,120)

while True:
	a_star(screen,player)
	player_input(screen,player)
	screen.fill([0,0,0])
	Tile.draw_tiles(screen)
    #screen.fill([0,0,0])
    #utils.text_to_screen(screen,"This is your font",250,250,50)
	Enemy.draw_enemies(screen)
	player.draw_player(screen)
	#print(player)
	#print(enemy1)
	pygame.display.flip() #generate display
	clock.tick(FPS) #Pygame to set FPS
	total_frames += 1