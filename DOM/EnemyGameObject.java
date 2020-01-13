/**
 *	Notes:
 *		state is either 1,2, or shooting
 *					texture to draw determined by textureToDraw based on state
 * 
 * 
 */

public class EnemyGameObject extends GameObject
{
	int[] textures;
	
	//only 5 totalimages
	
	public final int TEXTURE_FACE_LEFT   =1;
	public final int TEXTURE_FACE_LEFT2  =2;
	public final int TEXTURE_FACE_RIGHT  =3;//This is the inverse of LEFT
	public final int TEXTURE_FACE_RIGHT2 =4;//This is the inverse of LEFT2
	public final int TEXTURE_FACE_FORWARD=5;
	public final int TEXTURE_FACE_FORWARD2=6;//This is the inverse of FORWARD
	public final int TEXTURE_FACE_BACKWARD=7;
	public final int TEXTURE_FACE_BACKWARD2=8;//This is the inverse of BACKWARD
	public final int TEXTURE_FACE_SHOOT_FORWARD=9;
	public final int TEXTURE_DEAD=10;
	
	public final int STATE_1 = 12;
	public final int STATE_2 = 34;
	public final int STATE_ATTACK = 45;
	public final int STATE_DEAD = 234;
	
	private int state;//indicates the visual state from the list above
	private int ticksAtCurrentState;//monitors how long this enemy has been in a particular state

	Player perspective;//need to get player in order to tell what to draw
	
	public EnemyGameObject(int x, int y, int textureIndex, int size, boolean isGhost)
	{
		super(x,y,-1,size,isGhost);	
		state = -1;//no set state
		isEnemy = true;
	}
	
	
	public int textureToDraw(Player perspective)
	{
		//there is only 1 attack texture:
		if(state == STATE_ATTACK)
			return TEXTURE_FACE_SHOOT_FORWARD;
		else if(state==STATE_DEAD)
			return TEXTURE_DEAD;
	
		//this decides which image to draw
		//dependent on perspective AND state
		
		//params are
		//this's direction
		//And both's positions
		//+state

		double angleTo = this.angleToCenter(perspective.x,perspective.y);

		double bound1 =5*Math.PI/4;
		double bound2 =7*Math.PI/4;
		double bound3 =  Math.PI/4;
		double bound4 =3*Math.PI/4;	
		
		bound1= ArtWork.normalizeAngle(bound1+angleTo);
		bound2= ArtWork.normalizeAngle(bound2+angleTo);
		bound3= ArtWork.normalizeAngle(bound3+angleTo);
		bound4= ArtWork.normalizeAngle(bound4+angleTo);
		
		//now we use this's direction to determine where it goes
		//between bound1 and bound2 is right
		//between bound2 and bound3 is back
		//between bound3 and bound4 is left
		//between bound4 and bound1 is right
		
		//check if facing right:
		if(bound1<bound2)
		{
			//the span does not cross over zero	
			if((this._direction>bound1)&&(this._direction<bound2))
			{
				if(state == STATE_1)
					return TEXTURE_FACE_RIGHT;
				else
					return TEXTURE_FACE_RIGHT2;
			}
		}
		else
		{
			//the span does cross over zero
			if((this._direction>bound1)||(this._direction<bound2))
			{
				if(state == STATE_1)
					return TEXTURE_FACE_RIGHT;
				else
					return TEXTURE_FACE_RIGHT2;				
			}			
		}
		
		//check if facing back:
		if(bound2<bound3)
		{
			//the span does not cross over zero	
			if((this._direction>bound2)&&(this._direction<bound3))
			{
				if(state == STATE_1)
					return TEXTURE_FACE_BACKWARD;
				else
					return TEXTURE_FACE_BACKWARD2;
			}
		}
		else
		{
			//the span does cross over zero
			if((this._direction>bound2)||(this._direction<bound3))
			{
				if(state == STATE_1)
					return TEXTURE_FACE_BACKWARD;
				else
					return TEXTURE_FACE_BACKWARD2;				
			}			
		}
		
		//check if facing left:
		if(bound3<bound4)
		{
			//the span does not cross over zero	
			if((this._direction>bound3)&&(this._direction<bound4))
			{
				if(state == STATE_1)
					return TEXTURE_FACE_LEFT;
				else
					return TEXTURE_FACE_LEFT2;
			}
		}
		else
		{
			//the span does cross over zero
			if((this._direction>bound3)||(this._direction<bound4))
			{
				if(state == STATE_1)
					return TEXTURE_FACE_LEFT;
				else
					return TEXTURE_FACE_LEFT2;				
			}			
		}
		
		//check if facing back:
		if(bound4<bound1)
		{
			//the span does not cross over zero	
			if((this._direction>bound4)&&(this._direction<bound1))
			{
				if(state == STATE_1)
					return TEXTURE_FACE_FORWARD;
				else
					return TEXTURE_FACE_FORWARD2;
			}
		}
		else
		{
			//the span does cross over zero
			if((this._direction>bound4)||(this._direction<bound1))
			{
				if(state == STATE_1)
					return TEXTURE_FACE_FORWARD;
				else
					return TEXTURE_FACE_FORWARD2;				
			}			
		}

		return -1;//error
	}//texture to draw
	
	public void ticker()
	{
		ticksAtCurrentState++;	
		
	}//ticker
	
}//end of class
