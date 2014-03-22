import pygame
import utils

class Tile(pygame.Rect):
    
    #for new object add it to the static list
    List=[]
    width,height=40,40
    total_tiles=1
    H,V=1,18 #Horizontal and verticle offsets
    
    def __init__(self,x,y,Type):
        self.parent=None
        self.H,self.G,self.F=0,0,0
        self.type=Type
        #each tile has unique id
        #applicable if tiles added/removed as a stack
        self.number=Tile.total_tiles
        Tile.total_tiles+=1
        if Type=='empty':
            self.walkable=True
        else:
            self.walkable=False
        pygame.Rect.__init__(self,(x,y),(Tile.width,Tile.height))
        Tile.List.append(self)
	
    @staticmethod
    def get_tile(number):
        for tile in Tile.List:
            if tile.number == number:
                return tile

    @staticmethod
    def draw_tiles(screen):
        half_width=Tile.width//2
        for tile in Tile.List:
            if not(tile.type=='empty'):
                pygame.draw.rect(screen,[150,150,150],tile)
            if tile.G!=0:
                utils.text_to_screen(screen,tile.G,tile.x,tile.y+half_width,color=[120,150,50])
            if tile.H!=0:
                utils.text_to_screen(screen,tile.H,tile.x+half_width,tile.y+half_width,color=[20,60,150])
            if tile.F!=0:
                utils.text_to_screen(screen,tile.F,tile.x+half_width,tile.y,color=[50,150,180])               
            #utils.text_to_screen(screen,tile.number,tile.x,tile.y)
