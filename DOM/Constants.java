import java.lang.Math;
import java.awt.Color;

public class Constants
{
	public static final double VIEW_SPAN = 0.3*Math.PI;
	
	
	public static final boolean DO_LIGHTING_EFFECTS = false;
	public static final int ERASE_COLOR = Color.darkGray.getRGB();//black
	
	
	public static final int DISTANCE_AT_WHICH_INVISIBLE = 2000;
	
	
	public static final int GAME_TICKER_SLEEP_TIME = 25;
	
	public static final int MOVEMENT_SIZE = 10;
	public static final int DEFAULT_OBJECT_SIZE = 30;
	//public static final int DEFAULT_PLAYER_RADIUS = 30;
	public static final int DISTANCE_TO_REACH_DOOR = 100;	
	public static final double CLOSEST_POSSIBLE_TO_WALL = 30;
	
	public static final int     SIZE_OF_WALL_WHEN_CLOSE = 800;
	public static final int VIEWABLE_UPPER_LEFT_X = 20;
	public static final int VIEWABLE_UPPER_LEFT_Y = 50;
	public static final int VIEWABLE_WIDTH = 400;
	public static final int VIEWABLE_HEIGHT= 400;
	public static final double VIEWABLE_PROPORTION_DRAW_GUN = 0.8;//Will start drawing weapon this far down screen
	
	public static final String WINDOW_TITLE = "DOM 2003";
	public static final int WINDOW_WIDTH = 700;
	public static final int WINDOW_HEIGHT= 700;
	
	public static final Color FOREGROUND = Color.white;
	public static final Color BACKGROUND = Color.black;
	
	
	
	//GAME STATE
	public static final int GAME_STATE_NOT_RUNNING = 0;
	public static final int GAME_STATE_RUNNING = 1;
	
	
	
	//OBJECT AND WALL TYPES:
	
	//OBJECTS:
	public static final int PLAYER_STARTING_POS=99;
	public static final int TEST_OBJECT_TEXTURE=2;	
	
	//ENEMIES
	public static final int ENEMY_1_TEXTURE=7;
	public static final int ENEMY_2_TEXTURE=8;
	public static final int ENEMY_1_TYPE  =88;
	
	
	//WALLS:
	public static final int SUNSET_TEXTURE=0;
	public static final int WINTER_TEXTURE=1;
	public static final int STONE_TEXTURE=3;
	public static final int DEFAULT_TEXTURE=5;
	public static final int WOOD_TEXTURE=4;
	
	//DOORS:
	public static final int DOOR_TEXTURE=6;
	
	//WEAPONS:
	public static final int DEFAULT_WEAPON_TEXTURE = 9;
	
}
