/**
 *  All game logic, loading and saving.
 * 
 * 
 *  TO DO:
 *			Artwork:
 *				drawBackwards:draws the reverse image(object) drawAsObjectReverse
 * 
 *			enemys
 *				*new texture types
  *				*ticker
 *				*drawing(drawBackwards)
 *				
 *			objects
 *				*health
 *				*ammo
 * 
 *			images
 *				*camera
 *				*door
 *				*enemys(5)
 * 
 *			sounds			
 *				door open
 *				shot
 *				killed
 *				start
 *				explode
 *				
 *			effects:
 *				set offset when drawing to do illusion of walking
 *				sway up and down
 *				parabolic curve
 *				OR too much time
 * 		
 * 
 *			Game control
 *				AI
 *					enemys 
 *						1.choose position
 *						2.walk to position(like door ticker)	
 *						3.Shoot at position
 *						4.repeat
 * 
 *				shooting
 *					do fireWeapon:
 *						Make function distanceTo for wall that is correct, then replace version in Artwork
 *				
 *				status
 *					display outside viewable area
 *					power
 *					ammo
 *					level
 *					
 * 
 *			End of Level wall
 *					implement as door with particular trigger number
 * 
 * 
 *			Editor: 
 *					make scrollable
 *					
 * 
 *  Optimization:
 *			move darken to drawing functions
  *			drawPlain
 */
import java.awt.event.KeyEvent;
import java.util.Vector;
import java.io.*;
import java.util.StringTokenizer;
import java.awt.Graphics;

public class Game
{

	Player thisPlayer, otherPlayer;
	Wall[] walls;
	GameObject[] objects;
	ArtWork art;//
	
	GameTicker ticker;
	
	boolean readyToDraw;
	
	//Input information
	boolean upKeyPressed;
	boolean downKeyPressed;
	boolean leftKeyPressed;
	boolean rightKeyPressed;
	boolean ctrlKeyPressed;
	boolean spaceKeyPressed;
	boolean enterKeyPressed;
	
	
	int gameState;
	
	GameFrame gameFrame;//container
	
	public Game()
	{
		readyToDraw=false;	
		gameState = Constants.GAME_STATE_NOT_RUNNING;
		ticker = new GameTicker(this);
	}
	
	public void setGameFrame(GameFrame frame)
	{
		gameFrame = frame;	
	}
	
	public void setArt(ArtWork art)
	{
		this.art= art;	
	}
	
	public void setVars(Wall[] walls, ArtWork art, Player thisPlayer, GameObject[] objects)
	{
		this.walls =walls;
		this.art = art;
		this.thisPlayer=thisPlayer;
		this.objects=objects;
		readyToDraw= true;
		upKeyPressed=false;
		downKeyPressed=false;
		leftKeyPressed=false;
		rightKeyPressed=false;
		ctrlKeyPressed=false;
		enterKeyPressed=false;

	}
	
	public void start()
	{
		//called only once
		gameState = Constants.GAME_STATE_RUNNING;	
		ticker.setGameFrame(gameFrame);
		ticker.start();
	}
	
	
	public void render()
	{
		//this creates the image
		art.setScreenPixelData();//This trigers the creation of the image
	}
	
	public int[] getImageData()
	{
		return art.getScreenPixelData();
	}
	
	
	public void drawInfo(Graphics g)
	{
		//draws information OUTSIDE the viewable box
		g.drawString("Level 1   Health:100   Ammo:50",Constants.VIEWABLE_UPPER_LEFT_X,Constants.VIEWABLE_UPPER_LEFT_Y+Constants.VIEWABLE_HEIGHT);
		
		
		
	}
	
	//KEYBOARD IO*****************************************
	public void keyPressed(KeyEvent e)
	{ 
		//changeVarForTestOnClick();
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_UP:
			//thisPlayer.moveForward();
			upKeyPressed=true;
			break;
		case KeyEvent.VK_DOWN:
			//thisPlayer.moveBackward();
			downKeyPressed=true;
			break;
		case KeyEvent.VK_LEFT:
			//thisPlayer.addToDirection(.1);
			leftKeyPressed=true;
			break;
		case KeyEvent.VK_RIGHT:
			//thisPlayer.addToDirection(-.1);
			rightKeyPressed=true;
			break;
		case KeyEvent.VK_CONTROL:
			ctrlKeyPressed = true;
			break;
		case KeyEvent.VK_SPACE:
			spaceKeyPressed = true;
			break;
		case KeyEvent.VK_ENTER:			
			enterKeyPressed = true;
			break;
		}
	}
	public void keyReleased(KeyEvent e)
	{ 
		//changeVarForTestOnClick();
		switch(e.getKeyCode())
		{
		case KeyEvent.VK_UP:
			upKeyPressed=false;
			break;
		case KeyEvent.VK_DOWN:
			downKeyPressed=false;
			break;
		case KeyEvent.VK_LEFT:
			leftKeyPressed=false;
			break;
		case KeyEvent.VK_RIGHT:
			rightKeyPressed=false;
			break;
		case KeyEvent.VK_CONTROL:
			ctrlKeyPressed = false;
			break;
		case KeyEvent.VK_SPACE:
			spaceKeyPressed = false;
			break;
		case KeyEvent.VK_ENTER:			
			enterKeyPressed = false;
			break;
		}
	}//key pressed
	
	
	
	
	// FILE IO*******************************************
	public void loadLevel(String file)
	{
		
		//LOADING
	
		/**
		 *	File format:
		 * 
		 *	Walls then objects
		 * 
		 *	wall x1 y1 x2 y2 type
		 *  object x1 y1 type
		 * 
		 *  Ex
		 * 
		 * wall 50 75 100 150 2
		 * wall 150 75 100 250 3
		 * wall 250 75 100 450 1
		 * wall 50 575 100 650 1
		 * wall 50 175 100 250 1
		 * object 50 150 1
		 * object 50 150 4
		 * 
		 */
		DataInputStream input;
		Vector v_walls = new Vector();
		Vector v_objects=new Vector();
		
		try
		{
			input = new DataInputStream(new FileInputStream(file));	
			String line =input.readUTF();
			while(line!=null)
			{
				StringTokenizer	tokenizer= new StringTokenizer(line);
				String token = tokenizer.nextToken();
				if(token.equals("wall"))
				{
					int x1,y1,x2,y2,type;
					x1 = Integer.parseInt(tokenizer.nextToken());
					y1 = Integer.parseInt(tokenizer.nextToken());
					x2 = Integer.parseInt(tokenizer.nextToken());
					y2 = Integer.parseInt(tokenizer.nextToken());
					type = Integer.parseInt(tokenizer.nextToken());
					//Decide what vertex this coord belongs to
					Wall newWall = new Wall(x1,y1,x2,y2,type);
					
					if(type==Constants.DOOR_TEXTURE)
					{
						newWall.setAsDoor();	
					}
					
					v_walls.addElement(newWall);
				}
				else if(token.equals("object"))
				{
					int x,y,type;
					double direction;
					x = Integer.parseInt(tokenizer.nextToken());
					y = Integer.parseInt(tokenizer.nextToken());
					direction = Double.valueOf(tokenizer.nextToken()).doubleValue();
					type = Integer.parseInt(tokenizer.nextToken());
					if(type==Constants.PLAYER_STARTING_POS)
					{
						//thisPlayer= new Player();
						thisPlayer.setLocation(x,y);
						thisPlayer.direction=direction;
						
					}
					else if(type==Constants.ENEMY_1_TYPE)
					{
						GameObject enemy = (GameObject)new EnemyGameObject(x,y,type,Constants.DEFAULT_OBJECT_SIZE,false);
						enemy._direction = direction;
						v_objects.addElement(enemy);
					}
					else
					{
						GameObject newObject = new GameObject(x,y,type,Constants.DEFAULT_OBJECT_SIZE,false);
						newObject._direction=direction;
						v_objects.addElement(newObject);
					}
				}
				
				//get next line
				try
				{
					line =input.readUTF();
				}
				catch(Exception e)
				{
					line = null;
					input.close();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();	
		}
		
		//now take walls and objects and put them into arrays
		walls = new Wall[v_walls.size()];
		for (int i=0;i<v_walls.size();i++)
			walls[i]= (Wall)v_walls.elementAt(i);

		objects = new GameObject[v_objects.size()];
		for (int i=0;i<v_objects.size();i++)
			objects[i]= (GameObject)v_objects.elementAt(i);
		
		art.setObjects(objects);
		art.setWalls(walls);
		//art.setPerspective(?)

	}
	
	
	
	public int playerIsCollidingWithWall()
	{
		//cycle through walls, check collisions
	
		for(int i=0;i<this.walls.length;i++)
		{
			//c is the distance between the wall's verticies
			//a and b are the distances from the verticies to the player
			//r is the radius of the player
			double a,b,c,r;
			a = walls[i].distanceTo1(thisPlayer.x,thisPlayer.y);
			b = walls[i].distanceTo2(thisPlayer.x,thisPlayer.y);
			c = walls[i].length();
			r = thisPlayer.radius;


			if((b<r)||(a<r))
				return i;
			else if((c+r<a)||(c+r<b))
			{
				//then not intersecting
			}
			else
			{
				double f = Math.sqrt(a*a-r*r);
				double g = c-f;
				if((r*r+g*g>b*b)&&(c>f))
					return i;
			}
		}

		return -1;
	}//isColliding
	
	
	public void slideAgainstWall(int indexOfWall)
	{
		//adjusts player's x,y when colliding with wall
		
		Wall wall = walls[indexOfWall];
		
		if(wall.isVertical())
		{
			//simply adjust x value of player	
			if(thisPlayer.x>wall.x1)
			{
				thisPlayer.x=wall.x1+thisPlayer.radius*1.1;
			}
			else
			{
				thisPlayer.x=wall.x1-thisPlayer.radius*1.1;
			}
		}
		else if (wall.m==0)
		{
			//wall is horizontal, adjust y value only	
			if(thisPlayer.y>wall.y1)
			{
				thisPlayer.y=wall.y1+thisPlayer.radius*1.05;
			}
			else
			{
				thisPlayer.y=wall.y1-thisPlayer.radius*1.05;
			}

		}
		else
		{
			/**
			 * 
			 * This is wrong, must calculate intersection, move from there
			 * dx dy are actually changes from intersection NOT perspective.
			 * 
			 */
			
			double b2 = thisPlayer.y+thisPlayer.x/wall.m;
			double x,y;//intersection point
			
			//if(wall.m-1/wall.m==0)
			//{
				//x = mx + b
				//b = x - mx
				//b/(1-m)=x;
			//	x=;
			//	y=;
			//}
			//else
			{
				x = (b2-wall.b)/(wall.m+1/wall.m);
				y = wall.m*x+wall.b;
			}
			
			//move player away from wall perpendicular to 
			//the wall
			double r = thisPlayer.radius*1.05;//multiply to increase so that when checking new point, doesn't put back
			double m = wall.m;
			double dy = Math.sqrt(r*r/(1+m*m));
			double dx = -dy*m;
			
			//dx,dy is change in thisPlayer. need to add or subtract. whichever puts object on same side as original);
			if(thisPlayer.y>wall.m*thisPlayer.x+wall.b)
			{
				//new point must conform	
				if(y+dy>wall.m*(x+dx)+wall.b)
				{
					//add
					thisPlayer.y=y+dy;
					thisPlayer.x=x+dx;
				}
				else
				{
					//subtract
					thisPlayer.y=y-dy;
					thisPlayer.x=x-dx;
				}
			}
			else
			{
				//new point must conform	
				if(y+dy<wall.m*(x+dx)+wall.b)
				{
					//add
					thisPlayer.y=y+dy;
					thisPlayer.x=x+dx;
				}
				else
				{
					//subtract
					thisPlayer.y=y-dy;
					thisPlayer.x=x-dx;
				}
			}
		}//smooth non-vertical/non-horizontal
	
	}//smooth
	
	
	public void attemptToOpenDoor()
	{
		for(int i=0;i<walls.length;i++)
		{
			double distanceToDoor = walls[i].distanceTo(thisPlayer.x,thisPlayer.y);
			if(distanceToDoor<Constants.DISTANCE_TO_REACH_DOOR)
			{
				walls[i].tryToOpen();
			}
		}
		
	}//attempt to open door
	
	public void fireWeapon()
	{
		//1.makes sounds
		//2.set weapon obj so that it returns texture of guy shooting for a set time
		//3.tell artwork how close object is so it can draw a bullet explosion

		 /*		find if trajectory intersects any walls and how close
		*		find if trajectory intersects any players and how close
		*		find closest out of above 2 sets

		*					if hit person
		*						reduce power
		*						check if dead, animate dead as ticker
		*					if hit object
		*						compute same
		*/

		double distanceToImpact;
		Wall closestWall;
		GameObject closestObject;
		
		for(int i =0;i<walls.length;i++)
		{
			//find closest in trajectory
		}
		
		for(int i =0;i<objects.length;i++)
		{
			if(objects[i].isEnemy())
			{
				//find closest in trajectory	
			}
			
		}
		
	
	
	}//fire weapon
	
}//end of class
