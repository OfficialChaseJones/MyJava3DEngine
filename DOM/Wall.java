import java.lang.Math;
import java.awt.Color;
import java.awt.Graphics;

public class Wall
{
	double x1,x2,y1,y2,mx,my;//2points (mx,my) is the midpoint
	Color color;
	boolean drawn;
	double b,m;//if line
	int textureIndex;
	
	
	/**
	 *	If this is a door, then isDoor= true
	 *	When space is pressed:
	 *  if close enough
	 *  call tryToOpenDoor() here
	 *  check if this is actually a door
	 *  then change the state to opening
	 *	The gameTicker will be in charge of incrementing the wall's position
	 *  When it is all the way open, for now just leave it open forever
	 * 
	 * 
	 *	ALSO NOTE:
	 *	When doing collision testing when moving, ignore this wall if:
	 *	   it is a door AND it is not in a closed state, thus one can walk through an open door as well as doorway
	 * 
	 */
	
	//INFO for using the wall as a door:
	public final int DOOR_STATE_CLOSED = 1;
	public final int DOOR_STATE_OPENING= 2;
	public final int DOOR_STATE_OPEN   = 3;
	public final int DOOR_STATE_CLOSING= 4;
	
	public final double ANGLE_CHANGE = Math.PI/60;
	
	boolean isDoor;
	int stateOfDoor;//This is a timer
	double originalX,originalY;
	int ticks;
	
	private boolean swingingWay1;
	
	
	public Wall()
	{
		x1=x2=y1=y2=0;	
		mx=my=0;
		color= Color.blue;
		drawn = false;
		b=0;
		m=0;
		isDoor = false;
		swingingWay1=true;
		ticks = 0;
	}
	
	
	public Wall(int x1,int y1,int x2,int y2, int textureIndex)
	{
		isDoor = false;
		this.textureIndex=textureIndex;
		
		this.x1 = (double)x1;
		this.x2 = (double)x2;
		this.y1 = (double)y1;
		this.y2 = (double)y2;
		mx = (x1+x2)/2;
		my = (y1+y2)/2;
		color= Color.blue;
		drawn = false;
		if(x1!=x2)
		{
			m = (this.y1-this.y2)/(this.x1-this.x2);
			b = y1-m*this.x1;
		}
		else
		{
			m = 0;
			b = 0;
		}
	}

	public void setAsDoor()
	{
		//Determine how door swings here:
		originalX = this.x2;
		originalY = this.y2;
		isDoor = true;	
		stateOfDoor=DOOR_STATE_CLOSED;
		
		swingingWay1=true;//May change directions if there is an obstacle
		ticks = 0;
	}
				
	public void tryToOpen()
	{
		if(!isDoor)
			return;
		if(stateOfDoor==this.DOOR_STATE_OPEN)//all done
			return;
		if(stateOfDoor==this.DOOR_STATE_CLOSED)
			stateOfDoor=this.DOOR_STATE_OPENING;
	}

	public  void revert()
	{
		//this 'unticks' the wall, moving it back one step
		//OR it sends the wall in the opposite direction
		swingingWay1 = !swingingWay1;
	}
	
	public boolean doorTicker()
	{
		//returns true if the wall has moved signaling a need to redraw screen
		
		//swing open just a little bit more each time this is called until
		//the door is completely open 90 degrees
		if(!isDoor)
			return false;
		if(stateOfDoor!=this.DOOR_STATE_OPENING)
			return false;
		
		//Now, find angle to (x2,y2) (which is the moving end of the wall)
		//to (x1,y2) relative to (originalX,originalY) which is a copy of x2,y2
		//set x2,y2 so that the angle is now a little larger, if that angle is now
		//greater than PI/2, set the stateOfDoor as OPEN
		
		double angleTo2 = this.angleToSide2(x1,y1);
		
		double newAngle;
		
		if(swingingWay1)
		{
			newAngle= angleTo2 + ANGLE_CHANGE;
			ticks++;
		}
		else
		{
			newAngle= angleTo2 - ANGLE_CHANGE;
			ticks--;
		}
		
		//distance from pivot
		double dx = this.length()*Math.cos(newAngle);
		double dy = this.length()*Math.sin(newAngle);
		
		x2 = x1+dx;
		y2 = y1+dy;

		//BECAUSE X2 and Y2 have changed, certain values need to be recalculated so that it paints ok
		mx = (x1+x2)/2;
		my = (y1+y2)/2;
		if(x1!=x2)
		{
			m = (this.y1-this.y2)/(this.x1-this.x2);
			b = y1-m*this.x1;
		}
		else
		{
			m = 0;
			b = 0;
		}
		
		
		//now check if the door is all the way open
		double angleToOriginal = this.angleToOriginal(x1,y1);
		
		//if((newAngle-angleToOriginal>Math.PI/2)||(newAngle-angleToOriginal<-Math.PI/2))
		//if(ArtWork.isBetween(angleToOriginal-3*Math.PI/4,  ArtWork.normalizeAngle(angleToOriginal+3*Math.PI/4),  newAngle))
		if((ticks*ANGLE_CHANGE>Math.PI/2)||(ticks*ANGLE_CHANGE<-Math.PI/2))
			stateOfDoor=this.DOOR_STATE_OPEN;//stop!
		
		return true;
	}
	
	boolean isVertical()
	{
		return (x1==x2);	
	}
	
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
	
	public void print()
	{
		System.out.println("W (x1,y1)-("+this.x1+","+this.y1+")");
		System.out.println("W (x2,y2)-("+this.x2+","+this.y2+")");			
	}
	
	public double angleToSide1(double x,double y)
	{
		/*******
		 *          |       .(x1,y1)
		 *          |  .(x,y)       angle from ->
		 *  --------------------
		 *          |
 		 */
		
		//Special case to avoid dividing by zero
		if(x1==x)
		{	
			//Then either the angle is 90 or 270
			if(y1>y)
				return Math.PI/2;
			else
				return 3*Math.PI/2;
		}
		
		
		double slope = (y1-y)/(x1-x);
		double angle = Math.atan(slope);
		
		if(x1<x)
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
		
	}

	public double angleToSide2(double x,double y)
	{
		/*******
		 *          |       .(x2,y2)
		 *          |  .(x,y)       angle from ->
		 *  --------------------
		 *          |
 		 */
		
		//Special case to avoid dividing by zero
		if(x2==x)
		{	
			//Then either the angle is 90 or 270
			if(y2>y)
				return Math.PI/2;
			else
				return 3*Math.PI/2;
		}
		
		
		double slope = (y2-y)/(x2-x);
		double angle = Math.atan(slope);
		
		if(x2<x)
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
	}

	public double angleToOriginal(double x,double y)
	{
	
		//Special case to avoid dividing by zero
		if(originalX==x)
		{	
			//Then either the angle is 90 or 270
			if(originalY>y)
				return Math.PI/2;
			else
				return 3*Math.PI/2;
		}
		
		
		double slope = (originalY-y)/(originalX-x);
		double angle = Math.atan(slope);
		
		if(originalX<x)
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
	}

	
	public double distanceTo(double x, double y)
	{
		//There is an issue with ordering the walls that requires the use of midpoints
		//to determine distance to
		
		return (Math.sqrt((x-mx)*(x-mx)+(y-my)*(y-my))+distanceTo1(x,y)+distanceTo2(x,y))/3;
		
		/*
			//It is sufficient to return the closest point, rather than
			//the true distance since there will never be a conflict sorting
		
			double distanceTo1= Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
			double distanceTo2= Math.sqrt((x-x2)*(x-x2)+(y-y2)*(y-y2));
		
			if(distanceTo1>distanceTo2)
				return distanceTo2;
			else
				return distanceTo1;
		*/
	}
	
	public double distanceTo1(double x, double y)
	{
		return Math.sqrt((x-x1)*(x-x1)+(y-y1)*(y-y1));
	}

	public double distanceTo2(double x, double y)
	{
		return Math.sqrt((x-x2)*(x-x2)+(y-y2)*(y-y2));
	}
	
	public double length()
	{
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}
	
}
