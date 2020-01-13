/**
 * 
 *	This handles the rendering and drawing ONLY
 * 
 * 
 */



import java.awt.Graphics;

public class ArtWork
{
	
	Wall[] walls;//the walls for this level
	GameObject[] objects;
	Player perspective;
	TextureArt[] textures;
	
	
	int[] screenPixelData;
	
	private final double OFFSET = Math.PI;//Must be greater than span!
	
	public ArtWork()
	{
		walls=null;	
		screenPixelData = new int[Constants.VIEWABLE_HEIGHT*Constants.VIEWABLE_WIDTH];
		if(screenPixelData==null)
		{
			System.out.println("Wrong");	
		}
		//Initialize to avoid flickering
		int eraseColor = Constants.ERASE_COLOR;
		for(int i=0;i<screenPixelData.length;i++)
			screenPixelData[i]= eraseColor;

		
	}
	
	public void setTextures(TextureArt[] textures)
	{
		//eventuall put this info in to a hashmap. Each wall will contain the
		//name or number associated with a texture.
		this.textures=textures;
	}
	
	public void setWalls(Wall[] walls)
	{
		this.walls=walls;	
	}
	
	public void setObjects(GameObject[] objects)
	{
		this.objects=objects;
	}
						
	public void setPerspective(Player player)
	{
		perspective = player;	
	}
	
	public void setScreenPixelData()
	{
		//Erase image first Do this in the texture window?
//		int eraseColor = (255 << 24) | (123 << 16) | 150;
//		for(int i=0;i<screenPixelData.length;i++)
//			screenPixelData[i]= eraseColor;

		double leftMost = perspective.direction+Constants.VIEW_SPAN/2;
		double rightMost = perspective.direction-Constants.VIEW_SPAN/2;
		
		if(rightMost<0)
		{
			//rightMost is always less the leftMost!
			rightMost+=2*Math.PI;
			leftMost+=2*Math.PI;
		}
		
		//Mark all walls as undrawn
		for(int i = 0;i<walls.length;i++)
		{
			walls[i].reset();
		}
		
		if(objects!=null)
		{
			for(int i = 0;i<objects.length;i++)
			{
				objects[i].reset();
			}
		}
		
		//what about ranges? returning negatives or numbers > 2PI
		//Is either not either, or one or the other.

		if(walls!=null)
			drawRecursively(leftMost,rightMost,leftMost,rightMost);
		
		if(objects!=null)
			drawObjects(leftMost,rightMost);
		
		
		//NOW Draw weapon on screen
		drawWeapon();
		
		
		//System.out.print("Drew:");
		
		/*for(int i = 0;i<walls.length;i++)
		{
			
			if (walls[i].isMarked())
				System.out.print(","+i);
			//drawWall(walls[i],leftMost,rightMost,g);
			//System.out.println("Wall:"+i+" :"+walls[i].angleToSide1(perspective.x,perspective.y)+","+walls[i].angleToSide2(perspective.x,perspective.y));
		}*/
		//System.out.print("\n");
		
		//drawOverhead
		
		/*
		if(walls!=null)
		{
			for(int i = 0;i<walls.length;i++)
			{
				g.drawLine(300+(int)(walls[i].x1/10),300+(int)(walls[i].y1/10),300+(int)(walls[i].x2/10),300+(int)(walls[i].y2/10));
				g.drawOval(300+(int)perspective.x/10,300+(int)perspective.y/10,5,5);
				
			}
											
				
			
		}*/
		
		
	}
	
	public int []getScreenPixelData()
	{
		return this.screenPixelData;
	}
	
	private void drawWeapon()
	{
		int textureIndex = perspective.weapon.getCorrespondingTextureIndex();
		
		//now calculate the size and position based on the screen:
		int midX = Constants.VIEWABLE_WIDTH/2;
		int topY = (int)(Constants.VIEWABLE_HEIGHT*Constants.VIEWABLE_PROPORTION_DRAW_GUN);
		int height = (int)(Constants.VIEWABLE_HEIGHT*(1-Constants.VIEWABLE_PROPORTION_DRAW_GUN));
		int width  = (int)(((double)this.textures[textureIndex].w*height)/this.textures[textureIndex].h);
		int leftX = midX-width/2;
		
		//make DrawPlain Method
		textures[textureIndex].drawPlain(leftX,topY,width,height,screenPixelData);
		
	}
	
	private void drawObjects(double screenLeftAngle, double screenRightAngle)
	{
		//1.Get furthest object in span that has not yet been drawn.
		//2.Find out which parts are visible by cycling through the walls.
		// * if the perspective and the the object are on the same side of
		// * the inequality that expresses the wall, then the object can be seen in that span.
		//
		//3.Draw the visible parts
		//		Only this possibility
		// 
		//     obL	vL	 vR   objR
		//		|----|----|----|
		//
		//Can not display multiple portions of object, therefore objects
		//can be no wider than the walls in front of them.
		//
		//
		
		GameObject furthest;

		for(int i=0;i<objects.length;i++)
		{
			furthest =getFurthestObjectInSpanNotMarked(screenLeftAngle, screenRightAngle);		
			if(furthest==null)
				return;
			else
			{
				//draw furthest
				drawObject(furthest,screenLeftAngle, screenRightAngle);
				furthest.mark();
			}
		}
	
		
		
		//draw that object within span, cycle until furthest == null
	}
		
	private void drawRecursively(double screenLeftAngle, double screenRightAngle, double leftMost, double rightMost)
	{
		//System.out.println("HERE");
		
		//screenLeftAngle means that this angle shows at the leftmost part of screen
		//screenRightAngle means that this angle shows at the rightmostPart of screen
		
		//leftMost and rightMost is the span within that needs to be drawn
	
		/**
		 *	Algorithem
		 * 
		 *	1. if no walls are in the span or the span is nothing, then return 
		 *
		 *  2. if a closest wall is found, paint everything in span that is possible, recall on the rest of the span
		 *			Possibilities
		 *     a) wall falls in the middle and both end points are drawn (2 recursive calls, left and right)
		 *	   b) only the left side of span remains to be drawn
		 *     c) only the right side of span remains to be drawn
		 * 
		 *	   D!!) OBJECT IS IN FRONT OF PERSON BUT SIDES ARE NOT IN VIEW!
		 *			TEST FOR THIS FIRST!
		 *				NEED TO CHANGE  getClosestWallInSpanNotMarked
		 */
	
		Wall closest = getClosestWallInSpanNotMarked(leftMost, rightMost);
			
		if(closest==null)
		{
			//System.out.println("No walls in span");
			return;
		}
		
		//Now figure out what is left to be drawn
		double angleTo1 = closest.angleToSide1(perspective.x,perspective.y);
		double angleTo2 = closest.angleToSide2(perspective.x,perspective.y);

			//Paint the wall first
		drawWall(closest,screenLeftAngle, screenRightAngle,leftMost,rightMost);

		
		//many possibilities: angleTo1> angleTo2 or vice versa
		//					screenLeftAngle > 2*PI, screenRightAngle < 0
		//					leftMost > 2*PI, rightMost < 0
		//						18 possibilities total

		//use isBetween(double leftMost, double rightMost, double angleInQuestion)

		/*
		if(wallIsOffBothEndsOfScreenButCanStillSee)
		{
			return;
			//no need to recurse!
		}
		else */
		if((isBetween(leftMost,rightMost,angleTo1))&&(isBetween(leftMost,rightMost,angleTo2)))
		{
			//This is possibility a)	
			//System.out.println("A");
			//Maybe adjust angleTo1 and angleTo2 if span is funky (<0 or >2PI) instead of adjusting logic?
			
			angleTo1 = putInRange(leftMost, rightMost, angleTo1);
			angleTo2 = putInRange(leftMost, rightMost, angleTo2);	
			
			if(this.firstAngleIsToTheLeftOfSecond(angleTo1,angleTo2))
			{
				//leftmost>rightmost
				//leftmost>angleTo1
				//angleTo2>rightMost
				drawRecursively(screenLeftAngle, screenRightAngle, leftMost, angleTo1);
				drawRecursively(screenLeftAngle, screenRightAngle, angleTo2, rightMost);
				return;
			}
			else
			{
				drawRecursively(screenLeftAngle, screenRightAngle, leftMost, angleTo2);
				drawRecursively(screenLeftAngle, screenRightAngle, angleTo1, rightMost);
				return;
			}
		}
		else if(isBetween(leftMost,rightMost,angleTo1))
		{
			//System.out.println("BC1");
			//This is possibility b	or c depending on angleTo2
			//angleTo1 = putInRange(leftmost, rightMost, angleTo1);
			//angleTo2 = putInRange(leftmost, rightMost, angleTo2);	

			//need to call from leftMost to angleTo1 or angleTo1 to rightMost
			if(this.firstAngleIsToTheLeftOfSecond(angleTo1,angleTo2))
			{
				//then we paint the other side
				//leftmost to angleTo1
				angleTo1 = putInRange(leftMost, rightMost, angleTo1);
				drawRecursively(screenLeftAngle, screenRightAngle, leftMost, angleTo1);
				return;
			}
			else
			{
				angleTo1 = putInRange(leftMost, rightMost, angleTo1);
				drawRecursively(screenLeftAngle, screenRightAngle, angleTo1, rightMost);
				return;				
			}
			
			//Maybe adjust angleTo1 and angleTo2 if span is funky (<0 or >2PI) instead of adjusting logic?
			
			//recurse once
		}
		else if(isBetween(leftMost,rightMost,angleTo2))
		{
			//This is possibility b	or c depending on angleTo1
			//System.out.println("BC2");
			//Maybe adjust angleTo1 and angleTo2 if span is funky (<0 or >2PI) instead of adjusting logic?
			if(this.firstAngleIsToTheLeftOfSecond(angleTo2,angleTo1))
			{
				//then we paint the other side
				//leftmost to angleTo1
				angleTo2 = putInRange(leftMost, rightMost, angleTo2);
				drawRecursively(screenLeftAngle, screenRightAngle, leftMost, angleTo2);
				return;
			}
			else
			{
				angleTo2 = putInRange(leftMost, rightMost, angleTo2);
				drawRecursively(screenLeftAngle, screenRightAngle, angleTo2, rightMost);
				return;				
			}
			
			//recurse once
		}
		else
		{
			//System.out.println("Wall exceeds span");	
			return;
		}
		
	}
	
	
	private void drawObject(GameObject object,double screenLeftAngle,double screenRightAngle)
	{
		//cycle through all walls in span.
		//gradually work away at the span of the object until there
		//is no more span to draw or there are no more walls to compare with
		
		double originalScreenLeft = screenLeftAngle;
		double originalScreenRight = screenRightAngle;
		
		double angleToObjectLeft = object.angleToLeft(perspective.x,perspective.y);
		double angleToObjectRight = object.angleToRight(perspective.x,perspective.y);
		double angleToCenter = object.angleToCenter(perspective.x,perspective.y);
		
		//If angles cross boundries, add PI and normalize ALL
		//....
		if((screenLeftAngle>Math.PI*2)&&(angleToObjectLeft<Math.PI))
		{
			angleToObjectLeft+=2*Math.PI;	
			angleToObjectRight+=2*Math.PI;
		}
		
		double actualDistanceTo = object.distanceTo(perspective.x,perspective.y);
		double perpendicularDistanceTo = this.distanceToPerpendicular(actualDistanceTo,angleToCenter);
		int heightToDraw =(int)( Constants.CLOSEST_POSSIBLE_TO_WALL*Constants.SIZE_OF_WALL_WHEN_CLOSE/perpendicularDistanceTo);
		heightToDraw=heightToDraw*2;
		
		double angleToObjectLeftDraw =angleToObjectLeft;
		double angleToObjectRightDraw =angleToObjectRight;

		//First mark off boundarie of screen
		if(this.firstAngleIsToTheLeftOfSecond(angleToObjectLeftDraw,screenLeftAngle))
			angleToObjectLeftDraw=screenLeftAngle-.001;
		if(this.firstAngleIsToTheLeftOfSecond(screenRightAngle,angleToObjectRightDraw))
			angleToObjectRightDraw=screenRightAngle+.001;
		
		
		boolean markAsMiddle = false;
		for(int i=0;i<walls.length;i++)
		{
			if(wallIsInSpan(walls[i],originalScreenLeft,originalScreenRight))
			{
			
				//if object and perspective are NOT on the same
				//side of inequality described by line then
				//	if wall is in span(important!)
				//		get leftmost and rightMost of wall and hack away at object's span
				double wallM = walls[i].m;
				double wallB = walls[i].b;
				
				//inequality is x<>c
				if(((walls[i].isVertical())
					&&(((perspective.x<walls[i].x1)&&(object._x>walls[i].x2))
					  ||((perspective.x>walls[i].x1)&&(object._x<walls[i].x2))))
				   ||(!walls[i].isVertical())&&((
					  (((perspective.y<wallM*perspective.x+wallB)&&(object._y>wallM*object._x+wallB))
					||((perspective.y>wallM*perspective.x+wallB)&&(object._y<wallM*object._x+wallB)))

					  )))
				{
					//System.out.println("Wall# "+i+" is in front");
					
				//Object is on other side of wall
					double angleToSide1=walls[i].angleToSide1(perspective.x,perspective.y);
					double angleToSide2=walls[i].angleToSide2(perspective.x,perspective.y);
					if(screenLeftAngle>Math.PI*2)
					{
						angleToSide1+=2*Math.PI;	
						angleToSide2+=2*Math.PI;
					}
					double leftMostWall,rightMostWall;
					if(this.firstAngleIsToTheLeftOfSecond(angleToSide1,angleToSide2))
					{
						//NEED TO GET LEFTMOST ON SCREEN
						leftMostWall = 	angleToSide1;
						rightMostWall=  angleToSide2;
					}
					else
					{
						leftMostWall = 	angleToSide2;
						rightMostWall=  angleToSide1;		
					}		
					
					//Adjust to span of sight
	
		//If there is a problem with drawing objects that should be hidden then this
		//code will have to be modified so to handle all cases. Wall is not necesarily in span.
					if(!firstAngleIsToTheLeftOfSecond(screenLeftAngle,leftMostWall))
					{
						leftMostWall=screenLeftAngle;
					}
			
					if(firstAngleIsToTheLeftOfSecond(screenRightAngle,rightMostWall))
					{
						rightMostWall=screenRightAngle;
					}
				
		
					
					if(firstAngleIsToTheLeftOfSecond(leftMostWall,angleToObjectLeftDraw)
					   &&firstAngleIsToTheLeftOfSecond(rightMostWall,angleToObjectLeftDraw))
					{
						//do nothing
					}
					else if(firstAngleIsToTheLeftOfSecond(angleToObjectLeftDraw,leftMostWall)
							&&firstAngleIsToTheLeftOfSecond(angleToObjectRightDraw,leftMostWall))
					{
						//do nothing
					}
					else if((firstAngleIsToTheLeftOfSecond(angleToObjectLeftDraw,leftMostWall))
						&&(firstAngleIsToTheLeftOfSecond(rightMostWall,angleToObjectRightDraw)))
					{
						//wall is between man.
						//This means that there are at least 2 walls to handle the rest of the cutting
						//2 posibilities, the connecting walls are on the same side, or on different sides
						//if on different, don't draw the guy, if not, then ignore
						//find wall connecting to
						markAsMiddle = true;
					}
					else if((firstAngleIsToTheLeftOfSecond(angleToObjectLeftDraw,rightMostWall))
						&&(firstAngleIsToTheLeftOfSecond(rightMostWall,angleToObjectRightDraw)))
					{
						angleToObjectLeftDraw=rightMostWall+.001;
						//System.out.println(i+" "+ angleToObjectLeftDraw+" "+angleToObjectRightDraw +" "+ leftMostWall+" "+rightMostWall);
					}
					else if((firstAngleIsToTheLeftOfSecond(leftMostWall,angleToObjectRightDraw))
						&&(firstAngleIsToTheLeftOfSecond(angleToObjectLeftDraw,leftMostWall)))
					{
						angleToObjectRightDraw=leftMostWall+.001;
						//System.out.println(i+" "+ angleToObjectLeftDraw+" "+angleToObjectRightDraw +" "+ leftMostWall+" "+rightMostWall);
					}
					else
						return;
				}
			}//if wall is in span
		}//for loop
		
		//if wall is between object's span
		if(markAsMiddle)
		{
			//If span has been cut from both sides, then this guy
			//PROBABLY shouldn't be drawn. Better safe than sorry.
			//Don't want to draw when shouldn't
			//has
			if((angleToObjectLeftDraw !=angleToObjectLeft)
				&&(angleToObjectRightDraw !=angleToObjectRight))
				return;
		}
		
		
		double angleSpan   = screenLeftAngle-screenRightAngle;
		double angleOffset = screenLeftAngle-angleToObjectLeftDraw;
		double angleOffset2 = screenLeftAngle-angleToObjectRightDraw;
		//double angleOffsetMiddle = screenLeftAngle-angleToCenter;
		double angleOffsetWholeLeft = screenLeftAngle-angleToObjectLeft;
		double angleOffsetWholeRight = screenLeftAngle-angleToObjectRight;

		
		double px1,px2,pxCenter,pxLeft,pxRight;
		px1=(Constants.VIEWABLE_WIDTH*(angleOffsetWholeLeft)/angleSpan);
		px2=(Constants.VIEWABLE_WIDTH*(angleOffsetWholeRight)/angleSpan);
		//pxCenter=(Constants.VIEWABLE_WIDTH*(angleOffsetMiddle)/angleSpan);
		pxLeft=(Constants.VIEWABLE_WIDTH*(angleOffset)/angleSpan);
		pxRight=(Constants.VIEWABLE_WIDTH*(angleOffset2)/angleSpan);

		
		//Draw image lower in order to avoid drawing so much
		//Objects do not need to be so tall<- WRONG this distorts image
		//MUST be from middle of screen
		int middleOfImage = Constants.VIEWABLE_HEIGHT/2;
		
		//draw what's left of object
		//drawAsObject needs: 
		//   1.starting and ending x values on screen
		//   2.starting and ending as if while image was being painted
		//   3.a value indicating the height
		//   
		// DO same way

		//textures[object.textureIndex].drawAsObject((int)pxLeft,(int)pxRight,(int)px1,(int)px2,middleOfImage,(int)heightToDraw,(int)perpendicularDistanceTo,screenPixelData);
		//textures[object.textureIndex].drawAsObject((int)pxLeft,(int)pxRight,(int)px1,(int)px2,middleOfImage,(int)heightToDraw,screenPixelData);
		if(object.isEnemy)
			textures[((EnemyGameObject)object).textureToDraw(perspective)].drawAsObject((int)pxLeft,(int)pxRight,(int)px1,(int)px2,middleOfImage,(int)heightToDraw,(int)perpendicularDistanceTo,object.isGhost,screenPixelData);
		else
			textures[object.textureIndex].drawAsObject((int)pxLeft,(int)pxRight,(int)px1,(int)px2,middleOfImage,(int)heightToDraw,(int)perpendicularDistanceTo,object.isGhost,screenPixelData);

	}
	
	
	private void drawWall(Wall wall,double screenLeftAngle, double screenRightAngle, double leftMost, double rightMost)
	{
		//draw only this much on to the canvas	
		//fade colors in distance?
		//Use polygon.
		boolean flopped=false;
		boolean adjustSubtract = (leftMost>2*Math.PI);
		boolean adjustAdd      = (rightMost<0);

			
		if(adjustAdd)
		{
			screenLeftAngle+=OFFSET;
			screenRightAngle+=OFFSET;
			leftMost+=OFFSET;
			rightMost+=OFFSET;
		}
		else if(adjustSubtract)
		{
			screenLeftAngle-=OFFSET;
			screenRightAngle-=OFFSET;
			leftMost-=OFFSET;
			rightMost-=OFFSET;			
		}
		
		/*
			GARBAGE
		double leftMost2, rightMost2;//These should be used for calculating angles, NOT regular, which is used for drawing/distancing
		
		leftMost2 = leftMost;
		rightMost2 = rightMost;
		if (screenLeftAngle>2*Math.PI)
		{
			screenLeftAngle-=2*Math.PI;
			double temp;
			temp = screenRightAngle;
			screenRightAngle=screenLeftAngle;
			screenLeftAngle= temp;
		}
		if (screenRightAngle<0)
		{
			screenRightAngle+=2*Math.PI;
			double temp;
			temp = screenRightAngle;
			screenRightAngle=screenLeftAngle;
			screenLeftAngle= temp;
		}		
		if (leftMost>2*Math.PI)
		{
			leftMost-=2*Math.PI;
			double temp;
			temp = leftMost;
			leftMost=rightMost;
			rightMost= temp;
			flopped = true;	
		}
		if (rightMost<0)
		{
			rightMost+=2*Math.PI;
			double temp;
			temp = leftMost;
			leftMost=rightMost;
			rightMost= temp;
			flopped = true;	
		}*/

		
		double px1,px2;//,py1,py2;
		
		wall.mark();//mark as drawn
		
		//The y's are dependent on the height
		//The x's are dependent on the screen angles
		
		//The leftmost point of the viewable screen should show objects at an angle of 'screeLeftAngle'
		//and the rightmost screenRightAngle ect...

		double leftMostWall;
		double rightMostWall;
		double distanceToLeftMostWall;// = wall.distanceTo1(perspective.x,perspective.y);
		double distanceToRightMostWall;// = wall.distanceTo2(perspective.x,perspective.y);

		double angleToSide1=wall.angleToSide1(perspective.x,perspective.y);
		double angleToSide2=wall.angleToSide2(perspective.x,perspective.y);
		
		
		
		if(adjustAdd)
		{
			angleToSide1+=OFFSET;
			angleToSide2+=OFFSET;
			angleToSide1 = normalizeAngle(angleToSide1);
			angleToSide2 = normalizeAngle(angleToSide2);		
		}
		else if (adjustSubtract)
		{
			angleToSide1-=OFFSET;
			angleToSide2-=OFFSET;
			angleToSide1 = normalizeAngle(angleToSide1);
			angleToSide2 = normalizeAngle(angleToSide2);
		}

		boolean angle1IsLeft;
		
		if(this.firstAngleIsToTheLeftOfSecond(angleToSide1,angleToSide2))
		{
			angle1IsLeft = true;
			leftMostWall = 	angleToSide1;
			rightMostWall=  angleToSide2;
			distanceToLeftMostWall = wall.distanceTo1(perspective.x,perspective.y);
			distanceToRightMostWall = wall.distanceTo2(perspective.x,perspective.y);
		}
		else
		{
			angle1IsLeft = false;
			leftMostWall = 	angleToSide2;
			rightMostWall=  angleToSide1;			
			distanceToLeftMostWall = wall.distanceTo2(perspective.x,perspective.y);
			distanceToRightMostWall = wall.distanceTo1(perspective.x,perspective.y);
		}
		

		//heightToDraw1 =(int)( Constants.CLOSEST_POSSIBLE_TO_WALL*Constants.SIZE_OF_WALL_WHEN_CLOSE/distanceTo1);
		//heightToDraw2 =(int)( Constants.CLOSEST_POSSIBLE_TO_WALL*Constants.SIZE_OF_WALL_WHEN_CLOSE/distanceTo2);

		//need to make constants to express proportion
		//how close should object be to cover whole screen?
		
		//if distance is CLOSEST_POSSIBLE_TO_WALL, then wall should cover the height of screen
		//if distance is double than wall should be half. inversely proportional
	
		//also need to get px1

		double angleToDrawLeft,angleToDrawRight;
		///NOTE: CALCULATIONS AFTER THE NEXT TO IFS USING THESE ARE BOGUS
		double leftMostWallAdjusted,rightMostWallAdjusted;
		
		leftMostWallAdjusted = leftMostWall;
		rightMostWallAdjusted= rightMostWall;

		double visualDistanceToLeft; 
		double visualDistanceToRight;
		
		double perpendicularDistanceToLeft,perpendicularDistanceToRight;
		perpendicularDistanceToLeft = this.distanceToPerpendicular(distanceToLeftMostWall,leftMostWall);
		perpendicularDistanceToRight = this.distanceToPerpendicular(distanceToRightMostWall,rightMostWall);
			
		if(!firstAngleIsToTheLeftOfSecond(leftMost,leftMostWall))//&&(!flopped))
		{
			//p1
			//distanceToleftMostWall	= adjustedDistance(leftMostWall, rightMostWall, leftMost,distanceToleftMostWall, distanceToRightMostWall);
			
			//now must calculate the distance to the intersection
			//System.out.println("Angles before call: leftMost, rightMost, leftMostWall "+ leftMost+ " , "+rightMost + " , "+leftMostWall);
			visualDistanceToLeft = adjustDistance(wall,leftMost);
			leftMostWallAdjusted=leftMost;
		}
		else
			visualDistanceToLeft = perpendicularDistanceToLeft;
		
		if(firstAngleIsToTheLeftOfSecond(rightMost,rightMostWall))//&&(!flopped))
		{
			//distanceToRightMostWall	= adjustedDistance(leftMostWall, rightMostWall, rightMost,distanceToleftMostWall, distanceToRightMostWall);
			//System.out.println("Angles before call: leftMost, rightMost, rightMostWall "+ leftMost+ " , "+rightMost + " , "+rightMostWall);			
			visualDistanceToRight = adjustDistance(wall,rightMost);

			rightMostWallAdjusted=rightMost;
		}
		else
			visualDistanceToRight = perpendicularDistanceToRight;
		
		
		double intersectionLeft,intersectionRight;//To draw wall properly
		intersectionLeft = intersectionAt(wall,leftMostWallAdjusted );
		intersectionRight = intersectionAt(wall,rightMostWallAdjusted );
		
//		double visualDistanceToLeft =distanceToPerpendicular(distanceToleftMostWall, leftMostWallAdjusted);//wall.angleToSide1(perspective.x,perspective.y));
//		double visualDistanceToRight =distanceToPerpendicular(distanceToRightMostWall,rightMostWallAdjusted);// wall.angleToSide2(perspective.x,perspective.y));

		//double visualDistanceToLeft =distanceToPerpendicular(distanceToleftMostWall, leftMostWall,adjustSubtract);//wall.angleToSide1(perspective.x,perspective.y));
		//double visualDistanceToRight =distanceToPerpendicular(distanceToRightMostWall,rightMostWall,adjustSubtract);// wall.angleToSide2(perspective.x,perspective.y));

		int heightToDraw1,heightToDraw2;

		visualDistanceToRight = Math.abs(visualDistanceToRight);
		visualDistanceToLeft =  Math.abs(visualDistanceToLeft);
		
		heightToDraw1 =(int)( Constants.CLOSEST_POSSIBLE_TO_WALL*Constants.SIZE_OF_WALL_WHEN_CLOSE/visualDistanceToLeft);
		heightToDraw2 =(int)( Constants.CLOSEST_POSSIBLE_TO_WALL*Constants.SIZE_OF_WALL_WHEN_CLOSE/visualDistanceToRight);

		
		double angleSpan   = screenLeftAngle-screenRightAngle;
		double angleOffset = screenLeftAngle-leftMostWallAdjusted;
		double angleOffset2 = screenLeftAngle-rightMostWallAdjusted;

		px1=(Constants.VIEWABLE_WIDTH*(angleOffset)/angleSpan);//+Constants.VIEWABLE_UPPER_LEFT_X;
		px2=(Constants.VIEWABLE_WIDTH*(angleOffset2)/angleSpan);//+Constants.VIEWABLE_UPPER_LEFT_X;
		
		if(px1==px2)
		{
			//System.out.println("HELP");	
			//no need to draw
			return;
		}
		
		//g.drawOval((int)px1,100,5,5);
		//g.drawOval((int)px2,100,5,5);
		
		//middle of screen = 
		
		//int middleOfScreen =Constants.VIEWABLE_UPPER_LEFT_Y+Constants.VIEWABLE_HEIGHT/2;
		int middleOfScreen = Constants.VIEWABLE_HEIGHT/2;
		
		
		//public void texture(int x, int y, int w, int dh1, int dh2,double wallLeftX, double wallRightX, double intersectLeftX, double intersectRightX, Graphics g)					
		//consumer.texture(500,350,200,400,800,-100,-200,-150,-200,g); to paint partial of wall
		if(wall.isVertical())
		{
			//texture.texture((int)px1,middleOfScreen,(int)(px2-px1),heightToDraw1*2,heightToDraw2*2,g);
			if (angle1IsLeft)
				textures[wall.textureIndex].texture((int)px1,middleOfScreen,(int)(px2-px1),heightToDraw1*2,heightToDraw2*2,wall.y1,wall.y2,intersectionLeft,intersectionRight ,screenPixelData,visualDistanceToLeft,visualDistanceToRight);
			else
				textures[wall.textureIndex].texture((int)px1,middleOfScreen,(int)(px2-px1),heightToDraw1*2,heightToDraw2*2,wall.y2,wall.y1,intersectionLeft,intersectionRight ,screenPixelData,visualDistanceToLeft,visualDistanceToRight);
		}
		else
		{
			if (angle1IsLeft)
				textures[wall.textureIndex].texture((int)px1,middleOfScreen,(int)(px2-px1),heightToDraw1*2,heightToDraw2*2,wall.x1,wall.x2,intersectionLeft,intersectionRight ,screenPixelData,visualDistanceToLeft,visualDistanceToRight);
			else
				textures[wall.textureIndex].texture((int)px1,middleOfScreen,(int)(px2-px1),heightToDraw1*2,heightToDraw2*2,wall.x2,wall.x1,intersectionLeft,intersectionRight ,screenPixelData,visualDistanceToLeft,visualDistanceToRight);
			
		}
		
		
		/*
		g.drawLine((int)px1,middleOfScreen-heightToDraw1,(int)px1,middleOfScreen+heightToDraw1);
		g.drawLine((int)px2,middleOfScreen-heightToDraw2,(int)px2,middleOfScreen+heightToDraw2);
		g.drawLine((int)px2,middleOfScreen+heightToDraw2,(int)px1,middleOfScreen+heightToDraw1);
		g.drawLine((int)px1,middleOfScreen-heightToDraw1,(int)px2,middleOfScreen-heightToDraw2);
		*/
		
		/*for(int i = 0;i<walls.length;i++)
		{
			if(wall==walls[i])
			{
				g.drawString(""+i,(int)px1,200-i*10);
				g.drawString(""+i,(int)px2,200-i*10);
			}
		}*/
		
		
		
		
	}
	
	private GameObject getFurthestObjectInSpanNotMarked(double leftMost,double rightMost)
	{
		GameObject furthest = null;
		double distanceTo=-1;///not set
		
		for(int i=0;i<objects.length;i++)
		{
			if(!objects[i].isMarked()&&(objectIsInSpan(objects[i],leftMost,rightMost)))
			{
				if(distanceTo==-1)
				{
					furthest = objects[i];
					distanceTo = objects[i].distanceTo(perspective.x,perspective.y);
				}
				else
				{
					if(objects[i].distanceTo(perspective.x,perspective.y)>distanceTo)
					{
						furthest = objects[i];
						distanceTo = objects[i].distanceTo(perspective.x,perspective.y);				
					}
				}

			}
		}
		
		return furthest;
	}
	
	
	private Wall getClosestWallInSpanNotMarked(double leftMost, double rightMost)
	{
		Wall closest =null;
		double distanceTo=-1;
		
		for(int i=0;i<walls.length;i++)
	    {
			if((!walls[i].isMarked())&&(wallIsInSpan(walls[i],leftMost,rightMost)))
			{
				if(distanceTo==-1)
				{
					closest = walls[i];
					distanceTo = walls[i].distanceTo(perspective.x,perspective.y);
				}
				else
				{
					if(walls[i].distanceTo(perspective.x,perspective.y)<distanceTo)
					{
						closest = walls[i];
						distanceTo = walls[i].distanceTo(perspective.x,perspective.y);				
					}
				}
			}
			
		}//for				
		
		return closest;
	}//getClosestWall

	private boolean objectIsInSpan(GameObject object, double leftMost,double rightMost)
	{
		//double angleToCenter = object.angleToCenter(perspective.x,perspective.y);
		double angleToLeftSide = object.angleToLeft(perspective.x,perspective.y);
		double angleToRightSide = object.angleToRight(perspective.x,perspective.y);
		
		//if(either of the angles are in the span), return true
		if(this.firstAngleIsToTheLeftOfSecond(angleToRightSide,leftMost))
			return false;
		if(this.firstAngleIsToTheLeftOfSecond(rightMost,angleToLeftSide))
			return false;
		
		return true;
	}
	
	private boolean wallIsInSpan(Wall wall, double leftMost, double rightMost)
	{
		double angleTo1 = wall.angleToSide1(perspective.x,perspective.y);
		double angleTo2 = wall.angleToSide2(perspective.x,perspective.y);
	
		//returns true if any part of the wall is within leftMost and rightMost

				/*NEW*/
		
		if((angleIsNotBehind(angleTo1))&&(angleIsNotBehind(angleTo2)))
		{
			//System.out.println("Wall:"+this.wallNumber(wall)+" is not behind");
			
			//Decide if one side is to the left of leftMost
			//and if
			if((!this.firstAngleIsToTheLeftOfSecond(angleTo1,leftMost))&&
				(this.firstAngleIsToTheLeftOfSecond(angleTo2,rightMost)))
			{
				//System.out.println("Wall:"+this.wallNumber(wall)+" qualifies");
				return true;
			}
			if((this.firstAngleIsToTheLeftOfSecond(angleTo1,leftMost))&&
				(!this.firstAngleIsToTheLeftOfSecond(angleTo2,rightMost)))
			{
				//System.out.println("Wall:"+this.wallNumber(wall)+" qualifies");
				return true;
			}
		}
		
		/*NEW*/


		
		//new
		boolean adjustSubtract = (leftMost>2*Math.PI);
		boolean adjustAdd      = (rightMost<0);
				
		if(adjustAdd)
		{
			leftMost+=OFFSET;	
			rightMost+=OFFSET;
			angleTo1+=OFFSET;
			angleTo2+=OFFSET;

			angleTo1 = this.normalizeAngle(angleTo1);
			angleTo2 = this.normalizeAngle(angleTo2);
		}
		if(adjustSubtract)
		{
			leftMost-=OFFSET;	
			rightMost-=OFFSET;
			angleTo1-=OFFSET;
			angleTo2-=OFFSET;
			
			angleTo1 = this.normalizeAngle(angleTo1);
			angleTo2 = this.normalizeAngle(angleTo2);
		}

		if(leftMost==rightMost)
			return false;
		
		if(angleTo1==leftMost)
		{
			return this.firstAngleIsToTheLeftOfSecond(angleTo1,angleTo2);
			//return (angleTo2<angleTo1);				
		}
		if(angleTo1==rightMost)
		{
			return this.firstAngleIsToTheLeftOfSecond(angleTo2,angleTo1);
			//return (angleTo2>angleTo1);				
		}
		if(angleTo2==leftMost)
		{
			return this.firstAngleIsToTheLeftOfSecond(angleTo2,angleTo1);
			//return (angleTo2>angleTo1);				
		}
		if(angleTo2==rightMost)
		{
			return this.firstAngleIsToTheLeftOfSecond(angleTo1,angleTo2);
			//return (angleTo2<angleTo1);				
		}

		

		
		
		/*
		if(leftMost>2*Math.PI)
		{
			double adjustedLeftMost = leftMost - 2*Math.PI;
			//wall angle must be less than adjustLeftMost OR greater than rightMost
			if((angleTo1<=adjustedLeftMost)||(angleTo1>=rightMost))
			   return true;
			else
				return ((angleTo2<=adjustedLeftMost)||(angleTo2>=rightMost));

		}
  		else if(rightMost<0)
		{
			double adjustedRightMost = rightMost + 2*Math.PI;
			//wall angle must be greater than rightmost OR less than leftmost
			if((angleTo1>=adjustedRightMost)||(angleTo1<=leftMost))
			   return true;
			else
				return ((angleTo2>=adjustedRightMost)||(angleTo2<=leftMost));
		}*/
		
		//At this point all angles are between 0 and 2PI
		
		if((angleTo1<=leftMost)&&(angleTo1>=rightMost))
			return true;
		return ((angleTo2<=leftMost)&&(angleTo2>=rightMost));	
	}//boolean wallisInSpan
	
	
	public static boolean isBetween(double leftMost, double rightMost, double angleInQuestion)
	{
		//This sees if  angleInQuestion is between left and right
		//NONE can be less than 0 and leftMost must be greater than 2*PI.
		if(leftMost>2*Math.PI)
		{
			//SUBTRACT PI FROM ALL
			leftMost-=Math.PI;
			rightMost-=Math.PI;
			angleInQuestion = normalizeAngle(angleInQuestion-Math.PI);
			
			return ((angleInQuestion<=leftMost)&&(angleInQuestion>=rightMost));
			
			/*
			double adjustedLeftMost = leftMost - 2*Math.PI;
			//wall angle must be less than adjustLeftMost OR greater than rightMost
			return((angleInQuestion<=adjustedLeftMost)||(angleInQuestion>=rightMost));*/
		}
  		else if(rightMost<0)
		{
			//ERROR
			System.out.println("ERROR 123123");
			/*
			double adjustedRightMost = rightMost + 2*Math.PI;
			//wall angle must be greater than rightmost OR less than leftmost
			return ((angleInQuestion>=adjustedRightMost)||(angleInQuestion<leftMost));*/
		}
		return((angleInQuestion<=leftMost)&&(angleInQuestion>=rightMost));
	}
	
	private static double putInRange(double rangeLeft, double rangeRight, double angle)
	{
		//This adjusts angle so that it fits in the range
		
		//System.out.println("   x"+rangeLeft+ " "+angle+ " "+rangeRight);
		
		//rangeLeft > rangeRight
		if((angle<=rangeLeft)&&(angle>=rangeRight))
			return angle;
		if(rangeLeft > 2*Math.PI)
			return angle+2*Math.PI;
		else
			return angle-2*Math.PI;
		
	}
	
	private double distanceToPerpendicular(double actualDistanceTo, double angleTo)
	{
		//Cannot return 0!
		
		//returns distance from perpendicular of perspective
		//to the object at (x,y) at angleTo radians from right->
		double angleDifference = perspective.direction-angleTo;
		
		if(angleDifference==0)//Just to avoid div by zero
			angleDifference+=0.01;
		
		double value = Math.abs(Math.cos(angleDifference)*actualDistanceTo);
		
		if(!this.angleIsNotBehind(angleTo))
		{
			return	-value;
		}
		
		if(value>actualDistanceTo)
		{
			System.out.println("wrong");	
		}
		
		//if(value<actualDistanceTo)
		//	return actualDistanceTo;
		return value;
		//return Math.abs(actualDistanceTo/Math.cos(angleDifference));
	}//distanceToPerpendicular


	
	/*private double adjustedDistance(double angle1, double angle2, double actualAngle, double distance1, double distance2)
	{
		//used when drawing walls that go offscreen.
		if(angle1==angle2)
			return -1;//DON"T DRAW THIS
		
		double b,m;
		
		if(angle1==0)
		{
			b= (distance1-distance2*(angle1/angle2))/(1-angle1/angle2);
			m=(distance2-b)/angle2;			
		}
		else
		{
			b= (distance2-distance1*(angle2/angle1))/(1-angle2/angle1);
			m=(distance1-b)/angle1;
		}
		//System.out.println("A1, A2, NEW A:"+angle1+","+angle2+","+actualAngle);
		//System.out.println("D1, D2, NEW D:"+distance1+","+distance2+","+(m*actualAngle+b));
		return m*actualAngle+b;
	}*/
	public double distanceToIntersection(Wall wall, double angle)
	{
		   
		if(wall.x1==wall.x2)
		{
			double b = perspective.y-(Math.tan(angle)*perspective.x);
			double intersectionY = Math.tan(angle)*wall.x1+b;

			//RETURN DISTANCE TO (intersectionY, wall.x1)
			
			return Math.sqrt((perspective.x-wall.x1)*(perspective.x-wall.x1)+(perspective.y-intersectionY)*(perspective.y-intersectionY));
		}
		
		double wallM =wall.m;// (wall.y1-wall.y2)/(wall.x1-wall.x2);
		double wallB =wall.b;// wall.y1-wallM*wall.x1;
		
		if(Math.tan(angle)==wallM)
		{
			//don't know what to do here
			return 0;
		}
		double b = perspective.y-(Math.tan(angle)*perspective.x);
		double intersectionX = (wallB- b)/(Math.tan(angle)-wallM);
		double intersectionY = wallM*intersectionX+wallB;

		return Math.sqrt((perspective.x-intersectionX)*(perspective.x-intersectionX)+(perspective.y-intersectionY)*(perspective.y-intersectionY));
	}	

	public double intersectionAt(Wall wall, double angle)
	{
		  //This returns the intersection point between the angle and the
		//wall.  If the wall is vertical, then this is a Y value
		//If not, then it is the X intersection point.
		
		if(wall.x1==wall.x2)
		{
			double b = perspective.y-(Math.tan(angle)*perspective.x);
			double intersectionY = Math.tan(angle)*wall.x1+b;
			return intersectionY;
		}
		
		double wallM =wall.m;// (wall.y1-wall.y2)/(wall.x1-wall.x2);
		double wallB =wall.b;// wall.y1-wallM*wall.x1;
		
		if(Math.tan(angle)==wallM)
		{
			//don't know what to do here
			return 0;
		}
		double b = perspective.y-(Math.tan(angle)*perspective.x);
		double intersectionX = (wallB- b)/(Math.tan(angle)-wallM);
		return intersectionX;
	}	

	
	
	
	public double adjustDistance(Wall wall,double angle)
	{
		//if(0==0)
		//	return  (perpendicularDistanceToLeft+perpendicularDistanceToRight)/2;

		//System.out.println("Check "+wall.angleToSide1(perspective.x,perspective.y)+" "+wall.angleToSide2(perspective.x,perspective.y)+" "+angle);
		//wall.print();
		//perspective.print();
		double perpendicularDistance1=this.distanceToPerpendicular(wall.distanceTo1(perspective.x,perspective.y),wall.angleToSide1(perspective.x,perspective.y));
		double perpendicularDistance2=this.distanceToPerpendicular(wall.distanceTo2(perspective.x,perspective.y),wall.angleToSide2(perspective.x,perspective.y));

		//System.out.println("d1 d2  " + perpendicularDistance1+ " " +perpendicularDistance2);

		if(perpendicularDistance1==perpendicularDistance2)
			return perpendicularDistance2;
		   
		if(wall.x1==wall.x2)
		{
			double b = perspective.y-(Math.tan(angle)*perspective.x);
			double intersectionY = Math.tan(angle)*wall.x1+b;
			
			//System.out.println("intersectionY ,y1,y2 "+intersectionY+ " " +wall.y1+ " " +wall.y2);
			
			double distanceM = (perpendicularDistance1-perpendicularDistance2)/(wall.y1-wall.y2);
			double distanceB = perpendicularDistance1-distanceM*wall.y1;
			return distanceM*intersectionY+distanceB;
			//double value = distanceM*intersectionY+distanceB;
			//System.out.println("return "+value);
			//return value;
		}
		
		double wallM = (wall.y1-wall.y2)/(wall.x1-wall.x2);
		double wallB = wall.y1-wallM*wall.x1;
		
		if(Math.tan(angle)==wallM)
		{
			//don't know what to do here
			return 0;
		}
		double b = perspective.y-(Math.tan(angle)*perspective.x);
		double intersectionX = (wallB- b)/(Math.tan(angle)-wallM);
		//System.out.println("intersectionX "+intersectionX);
		double distanceM = (perpendicularDistance1-perpendicularDistance2)/(wall.x1-wall.x2);
		double distanceB = perpendicularDistance1-distanceM*wall.x1;
		
		return distanceM*intersectionX+distanceB;
		//double value = distanceM*intersectionX+distanceB;
		//System.out.println("return "+value);
		//return value;
	
	}//adjust distance
	
	
	public int wallNumber(Wall wall)
	{
		for(int i=0;i<walls.length;i++)
			if(walls[i]==wall)
				return i;
		return -1;
	}
	
	
	public static double normalizeAngle(double angle)
	{
		while(angle<0)
			angle+=2*Math.PI;
		while(angle>2*Math.PI)
			angle-=2*Math.PI;
		return angle;
	}
	
	public boolean firstAngleIsToTheLeftOfSecond(double first, double second)
	{
		//if first is to the left of second, return true
		//if first is to the right of second, return false
		
		first = normalizeAngle(first);
		second = normalizeAngle(second);
		
		second = second - first;
		
		second = normalizeAngle(second);
		
		return (second > Math.PI);
	
	}
	
	public boolean angleIsNotBehind(double angle1)
	{
		angle1 = normalizeAngle(angle1-perspective.direction);
		//if angle is in 1st or 4th quadrant
		return((angle1<Math.PI/2)||(angle1>3*Math.PI/2));
	
	}
}//end of class
