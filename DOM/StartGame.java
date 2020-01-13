import java.awt.Image;
import java.awt.image.*;


public class StartGame
{
	/**
	 * The main entry point for the application. 
	 *
	 */
	public static void main (String[] args)
	{
		//This should load textures and starting position for now
		
		GameFrame frame = new GameFrame();
		Game game = new Game();
		
		
		//Load textures
		Image testImage= frame.getToolkit().getImage("rooster.jpg");
		TextureArt testTexture = new TextureArt(testImage);

		Image sunsetImage= frame.getToolkit().getImage("sunset.jpg");
		TextureArt sunsetTexture = new TextureArt(sunsetImage);
		Image winterImage= frame.getToolkit().getImage("winter.jpg");
		TextureArt winterTexture = new TextureArt(winterImage);
		Image woodImage= frame.getToolkit().getImage("wood.jpg");
		TextureArt woodTexture = new TextureArt(woodImage);
		Image stoneImage= frame.getToolkit().getImage("stones.jpg");
		TextureArt stoneTexture = new TextureArt(stoneImage);
	
		Image weaponImage= frame.getToolkit().getImage("gunTexture.jpg");
		TextureArt weaponTexture = new TextureArt(weaponImage);
	

		//load games
		
		//Init vars
		Player thisPlayer = new Player();
		thisPlayer.setLocation(20,105);
		
		thisPlayer.direction= Math.PI/2;
		
		ArtWork art = new ArtWork();
		art.setPerspective(thisPlayer);
		
		TextureArt[] textures = new TextureArt[10];
		textures[0]= sunsetTexture;
		textures[1]= winterTexture;
		textures[2]= testTexture;
		textures[3]= stoneTexture;
		textures[4]= woodTexture;
		textures[5]= woodTexture;
		
		textures[6]= sunsetTexture;//This is a door
		
		textures[9]=weaponTexture;//This is a gun
		
		
		art.setTextures(textures);
		
		
		/*
		//Game objects
		GameObject[] objects = new GameObject[3];
		objects[0]= new GameObject(20,150,Constants.TEST_OBJECT_TEXTURE,20,true);
		objects[1]= new GameObject(50,-100,Constants.TEST_OBJECT_TEXTURE,20,false);
		objects[2]= new GameObject(60,0,Constants.TEST_OBJECT_TEXTURE,20,true);
		art.setObjects(objects);
		
		//Wall
		Wall[] walls = new Wall[20];
		walls[0] = new Wall(-200,100,-100,100,Constants.SUNSET_TEXTURE);
		walls[1] = new Wall(-100,100,-100,200,Constants.SUNSET_TEXTURE);
		walls[2] = new Wall(-100,200,   10,200,Constants.SUNSET_TEXTURE);
		walls[3] = new Wall(10,  200 , 100,200,Constants.SUNSET_TEXTURE);
		walls[4] = new Wall(100,200 , 100,150,Constants.SUNSET_TEXTURE);
		walls[5] = new Wall(100,150 , 200,100,Constants.SUNSET_TEXTURE);
		
		walls[6] = new Wall(-200,0 , -200,100,Constants.SUNSET_TEXTURE);
		walls[7] = new Wall(-200, 0 , -220,-150,Constants.SUNSET_TEXTURE);
		walls[8] = new Wall(-220,-150 , -100,-100,Constants.SUNSET_TEXTURE);
		walls[9] = new Wall(-100,-100 , -100,-200,Constants.SUNSET_TEXTURE);
		walls[10] = new Wall(-100,-200 , 0,-200,Constants.SUNSET_TEXTURE);
		walls[11] = new Wall( 0,-200 , 100,-200,Constants.SUNSET_TEXTURE);
		walls[12] = new Wall(100,-200 , 200,-200,Constants.SUNSET_TEXTURE);
		walls[13] = new Wall(200,-200 , 200,-100,Constants.SUNSET_TEXTURE);
		walls[14] = new Wall(200,10 , 200,-100,Constants.SUNSET_TEXTURE);
		walls[15] = new Wall(200, 100 , 200,10,Constants.SUNSET_TEXTURE);
		//walls[16] = new Wall(200,100 , 200,100);
		
		walls[16] = new Wall(-20, -20 , -20,20,Constants.SUNSET_TEXTURE);
		walls[17] = new Wall(-20, 20 , 20,20,Constants.SUNSET_TEXTURE);
		walls[18] = new Wall(20, 20 , 20,-20,Constants.SUNSET_TEXTURE);
		walls[19] = new Wall(20, -20 , -20,-20,Constants.SUNSET_TEXTURE);
		
		art.setWalls(walls);
		
		game.setVars(walls,art,thisPlayer,objects);
		*/
		//frame.setVars(walls,art,thisPlayer);
		//game.setArt(art);
		game.setVars(null,art,thisPlayer,null);
		game.loadLevel("test2.lev");
		frame.setGame(game);
		
		
		game.start();
	}
	
	

}
