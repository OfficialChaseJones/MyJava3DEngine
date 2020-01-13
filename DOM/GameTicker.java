import java.awt.*;

/***
 * This class has a procedure that runs every few milleseconds that
 * 
 * 1. Takes the current keyboard state and moves character
 * 2. Moves non-player characters
 * 3. Collision detection
 * 4. image generation
 * 
 */

public class GameTicker extends Thread
{
	Game _game;
	GameFrame _gameFrame;
	
	public GameTicker(Game game)
	{
		_game = game;
	}
	
	
	//MAIN LOOP
	public void run()
	{
		System.out.println("Running");	
		
		while(true)
		{
			//start timer
			long time = System.currentTimeMillis();
			
			boolean isDirty	=false;
			
			isDirty =((pollKeyBoard())||(isDirty));
			
			// OR isDirty with each successive function
			// so that any changes mark it
			// NOTE: isDirty must come second or else if it is already true, then second expression won't be computed!
			
			//Signal any existing doors
			isDirty = ((pollDoors())||(isDirty));
			
			isDirty = ((pollEnemies())||(isDirty));			
			
			if(isDirty)
			{
				_gameFrame.setScreenImage();
				_gameFrame.paint(_gameFrame.getGraphics());
				
			}			
			
			//stop timer
			time =  System.currentTimeMillis()-time;
			try
			{
				int timeToSleep = Constants.GAME_TICKER_SLEEP_TIME - (int)time;
				if((timeToSleep>0)&&(time >= 0))
					this.sleep(timeToSleep);	//subtract timer
				//else 
				//	System.out.println("TIME:"+time);
			}
			catch(Exception e)
			{
			}
		}//loop
	}//run
	
	private boolean pollEnemies()
	{
		//returns true if there is an enemy that is visible that has moved;
		boolean enemyInView;
		
		//go through enemies
		for(int i=0;i<_game.objects.length;i++)
		{
			if(_game.objects[i].isEnemy())
			{
				EnemyGameObject enemy = (EnemyGameObject)_game.objects[i];
				enemy.ticker();
				
				//if colliding
				//revert
			}
			
		}
	
		return false;
	}
	
	private boolean pollDoors()
	{
			boolean isDirty = false;
			for(int i =0;i<_game.walls.length;i++)
			{
				if(_game.walls[i].isDoor)
				{
					boolean wallMoved =_game.walls[i].doorTicker();
					
					if (wallMoved)
					{
						//if collisions, then move back	
						int wallCollision =_game.playerIsCollidingWithWall();
						
						if(wallCollision!=-1)
						{
							_game.walls[i].revert();
						}
					}
					
					isDirty = ((wallMoved)||(isDirty));
				}
			}
			return isDirty;
	}
	
	
	public void setGameFrame(GameFrame gameFrame)
	{
		_gameFrame = gameFrame;
	}
	public boolean pollKeyBoard()
	{
		//All controlled movements here
		double original_X = _game.thisPlayer.x;
		double original_Y = _game.thisPlayer.y;
		
		boolean isDirty=false;
		if(_game.downKeyPressed)
		{
			isDirty = true;
			_game.thisPlayer.moveBackward();
		}
		if(_game.upKeyPressed)
		{
			isDirty = true;
			_game.thisPlayer.moveForward();
		}
		if(_game.leftKeyPressed)
		{
			isDirty = true;
			if(_game.ctrlKeyPressed)
				_game.thisPlayer.moveLeft();
			else
				_game.thisPlayer.addToDirection(0.05);
			
		}
		if(_game.rightKeyPressed)
		{
			isDirty = true;
			if(_game.ctrlKeyPressed)
				_game.thisPlayer.moveRight();
			else
				_game.thisPlayer.addToDirection(-0.05);
		}
		if(_game.spaceKeyPressed)
		{
			//SHOOT
			_game.fireWeapon();
			
		}
		if(_game.enterKeyPressed)
		{
			//Attemp to open door
			_game.attemptToOpenDoor();
			
		}
		
		
		int wallCollision =_game.playerIsCollidingWithWall();
		
		if((isDirty)&&(wallCollision!=-1))
		{
			//Attempt to smooth movement:
			_game.slideAgainstWall(wallCollision);//pass the index of the wall

			
			//If player is still colliding with wall after smoothing, then give up and 
			//move back to original position
			if(_game.playerIsCollidingWithWall()!=-1)
			{
				//move back
				_game.thisPlayer.x=	original_X;
				_game.thisPlayer.y=	original_Y;
			}
		}
		
		return isDirty;
	}
	
}
