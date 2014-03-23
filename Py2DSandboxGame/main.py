#http://www.policyalmanac.org/games/aStarTutorial.htm
import pygame, sys
from tileC import Tile
import utils
from game_objects import *
from player_input import player_input
from a_star import a_star
from time import sleep

print(sys.version)

pygame.init()
pygame.font.init()
pygame.mixer.init()
screen = pygame.display.set_mode((704,448)) #32,32

clock=pygame.time.Clock()
FPS=25
total_frames=0



for y in range(0,screen.get_height(),32):
	for x in range(0,screen.get_width(),32):
		if Tile.total_tiles in Tile.invalids:
			Tile(x,y,'solid')
		else:
			Tile(x,y,'empty')

background=pygame.image.load('res/background.png')
pygame.mixer.music.load('res/gameMusic1.mp3')
pygame.mixer.music.play(-1)
#positions must be divisible by tile dimensions (32)
# enemy1=Enemy(200,240)
player=Player(32*2,32*4)

while True:
	# screen.fill([255,255,255])
	screen.blit(background,(0,0))
	if total_frames % 6*FPS  == 0:
		print(len(Enemy.List))
	Enemy.spawn(total_frames,FPS)
	Enemy.update(screen,player)
	player.movement()
	Projectile.projectile_physics(screen)
	a_star(screen,player,total_frames,FPS)
	player_input(screen,player)
	Tile.draw_tiles(screen)
	utils.text_to_screen(screen,"Health: {0}".format(player.health),0,0)
    #utils.text_to_screen(screen,"This is your font",250,250,50)
	player.draw_player(screen)
	pygame.display.flip() #generate display
	clock.tick(FPS) #Pygame to set FPS
	total_frames += 1
	if player.health<=0:
		sleep(2.5)
		screen.blit(pygame.image.load('res/endgame.jpg'),(0,0))
		pygame.display.update()
		break

sleep(4)