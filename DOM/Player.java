public class Player
{
	
	double x,y;
	double direction;//direction facing
	double radius;//for collision detection
	
	Weapon weapon;
	
	public Player()
	{
		x=y=direction=0;	
		radius = Constants.CLOSEST_POSSIBLE_TO_WALL;
		weapon = new Weapon();
		
	}
	
	public void setLocation(double x, double y)
	{
		this.x=x;
		this.y=y;
	}
	
	public void print()
	{
		System.out.println("P (x1,y1)-("+this.x+","+this.y+")");
		System.out.println("P direction="+direction);
	}

	
	public void addToDirection(double change)
	{
		direction += change;
		if(direction > Math.PI*2)
			direction-=2*Math.PI;
		else if(direction<0)
			direction+=2*Math.PI;
	}
	
	public void moveForward()
	{
		x += Math.cos(direction)*Constants.MOVEMENT_SIZE;	//The multiplier must be no greater then the diameter of the player
		y += Math.sin(direction)*Constants.MOVEMENT_SIZE;
	}
	public void moveBackward()
	{
		x -= Math.cos(direction)*Constants.MOVEMENT_SIZE;	
		y -= Math.sin(direction)*Constants.MOVEMENT_SIZE;
	}
	public void moveRight()
	{
		x += Math.cos(direction-Math.PI/2)*Constants.MOVEMENT_SIZE;	
		y += Math.sin(direction-Math.PI/2)*Constants.MOVEMENT_SIZE;
	}
	public void moveLeft()
	{
		x += Math.cos(direction+Math.PI/2)*Constants.MOVEMENT_SIZE;
		y += Math.sin(direction+Math.PI/2)*Constants.MOVEMENT_SIZE;
	}
	
}
