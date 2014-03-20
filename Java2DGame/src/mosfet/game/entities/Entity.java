package mosfet.game.entities;
import mosfet.game.gfx.Screen;
import mosfet.game.level.Level;

public abstract class Entity {

		public int x,y;
		protected Level level;
		
		public abstract void render(Screen screen);
		public abstract void tick();
		public final void init(Level level){this.level=level;}
		public Entity(Level level){init(level);}
}
