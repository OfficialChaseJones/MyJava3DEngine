import java.lang.Math;

/**
 * 
 * This is all the objects in the game: enemys and non-active objects
 * 
 * 
 */


public class GameObject
{
	double _diameter;//used for drawing
	double _direction;
	double _x,_y;//The center
	int _objectType;//type of actions
	int textureIndex;//texture to be drawn
	boolean drawn;
	boolean isGhost;//not fully implemented
	boolean isEnemy;//set to true in EnemyGameObject. here is false
	
	
	/*public GameObject()
	{
		_objectType= Constants.DEFAULT_OBJECT;
		drawn = false;
	}*/
	
	public GameObject(int x, int y, int textureIndex, int size, boolean isGhost)
	{
		_diameter = size;
		_x=x;
		_y=y;
		this.textureIndex = textureIndex;
		drawn = false;
		this.isGhost=isGhost;
		isEnemy = false;
	}
	
	
	public double distanceTo(double x, double y)
	{
		return Math.sqrt((_x-x)*(_x-x)+(_y-y)*(_y-y));
		
	}
	
	
	public double angleToCenter(double x,double y)
	{
		
		//Special case to avoid dividing by zero
		if(_x==x)
		{	
			//Then either the angle is 90 or 270
			if(_y>y)
				return Math.PI/2;
			else
				return 3*Math.PI/2;
		}
		
		
		double slope = (_y-y)/(_x-x);
		double angle = Math.atan(slope);
		
		if(_x<x)
		{
			//Then we have to change the angle
			//since it is on the left side of the x axis
			return angle+Math.PI;	
		}
		else
		{
			if(angle>=0)
				return angle;
			return angle+2*Math.PI;
		}
		
	}//angle to
	

	public double angleToLeft(double x,double y)
	{
		double angleToCenter= this.angleToCenter(x,y);
		double distanceToCenter = this.distanceTo(x,y);
		
		double angleDif = Math.asin((this._diameter/2)/distanceToCenter);
		
		return angleToCenter+angleDif;

	}//angle to

	public double angleToRight(double x,double y)
	{
		double angleToCenter= this.angleToCenter(x,y);
		double distanceToCenter = this.distanceTo(x,y);
		
		double angleDif = Math.asin((this._diameter/2)/distanceToCenter);
		
		return angleToCenter-angleDif;
	}//angle to
	
		
	public void mark()
	{
		drawn = true;	
	}
	
	public boolean isMarked()
	{
		return drawn;	
	}
	
	public void reset()
	{
		drawn = false;	
	}
	
	public boolean isEnemy()
	{
		return isEnemy;	
	}
	
}//end of class
