/*
		To use.

		im = this.getToolkit().getImage("Sunset.jpg");
		consumer = new TextureArt(im);

		To paint.

		consumer.texture(100,350,200,400,800,g); to paint whole wall
		consumer.texture(500,350,200,400,800,-100,-200,-150,-200,g); to paint partial of wall



*/


import java.awt.image.*;
import java.util.Hashtable;
import java.awt.Graphics;
import java.util.Vector;
import java.awt.Color;
import java.awt.Image;

public class TextureArt implements ImageConsumer
{
	//  600x 800
	int [][] _pixels;
	boolean imageComplete;
	int h,w;
	ColorModel modelTranslator;
	//public TextureArt()
	//{
	//}
	
	public TextureArt(Image im)
	{
		_pixels=null;	
		imageComplete = false;
		im.getSource().startProduction(this);
		
	}
	
	public int[][] getPixels()
	{
		return _pixels;	
	}
	
	public void paint(Graphics g)
	{
		int color=0;

		if(imageComplete)
		{
			for(int i =0;i<h;i+=16)
				for(int j=0;j<w;j+=16)
				{
					if(color!=_pixels[i][j])
					{
						color = _pixels[i][j];
						Color c = null;
						c= new	Color(color);
						g.setColor(new Color(color));
					}
					
					//g.drawLine(j/2+30,i/2+30,j/2+31,i/2+31);
					//g.drawRect(j/2+30,i/2+30,1,1);
					g.fillRect(j+30,i+30,16,16);
					
				}
		}
	}
	
	/*public void texture(int x, int y, int w, int dh1, int dh2, Graphics g)
	{
		double dm = ((double)dh2-(double)dh1)/w;
		
		for(int i =0;i<w;i++)
		{
			int height =(int)( dh1+dm*i);
			for(int j=0;j<height;j++)
			{
				int ax,ay;
				ax = (int)(((double)i/w)*this.w);
				ay = (int)(((double)j/height)*this.h);
				int color = _pixels[ay][ax];
				Color c= new	Color(color);
				g.setColor(new Color(color));
				
				g.drawLine(x+i,y+j-height/2,x+i,y+j-height/2);
				
			}
		}
		
	}
	
	public void texture(int x, int y, int w, int dh1, int dh2,double wallLeftX, double wallRightX, double intersectLeftX, double intersectRightX, Graphics g)
	{
		double dm = ((double)dh2-(double)dh1)/w;
		double wallLength = wallRightX-wallLeftX;
		double axstart = this.w*(intersectLeftX-wallLeftX)/wallLength;
		double axend = this.w*(intersectRightX-wallLeftX)/wallLength;
		double axspan = axend-axstart;
		
		for(int i =0;i<w;i++)
		{
			int height =(int)( dh1+dm*i);
			for(int j=0;j<height;j++)
			{
				int ax,ay;
				ax = (int)(((double)i/w)*axspan+axstart);
				ay = (int)(((double)j/height)*this.h);
				int color = _pixels[ay][ax];
				Color c= new	Color(color);
				g.setColor(new Color(color));
				
				g.drawLine(x+i,y+j-height/2,x+i,y+j-height/2);
				
			}
		}
		
	}*/
	
	public void drawPlain(int x,int y, int width,int height, int[]screen)
	{
		//draw plainly:
		for(int ix=0;ix<width;ix++)
		{
			int color;
			for(int iy=0;iy<height;iy++)
			{
				//find pixel source
				//paint to screenPixelData[x+ix][y+iy]	
				
				//no darkening necesary
				//screen[(y+iy)*Constants.VIEWABLE_WIDTH+ix+x]=_pixels[(iy*h)/height][(ix*w)/width];
				
				if((color = _pixels[(iy*h)/height][(ix*w)/width])!=-16777216)
							screen[(y+iy)*Constants.VIEWABLE_WIDTH+ix+x]=color;

			}
		}
	
	}
	
	public void drawAsObject(int intersectLeftX,int intersectRightX, int xLeft,int xRight, int yCenter, int height, int[] screen)
	{
		if(intersectLeftX<0)
			intersectLeftX=0;
		if(intersectRightX>=Constants.VIEWABLE_WIDTH)
			intersectRightX=Constants.VIEWABLE_WIDTH;
		
		double length = xRight-xLeft;
		int axstart = (int)(this.w*((double)intersectLeftX-xLeft)/length);
		int axend = (int)(this.w*(intersectRightX-xLeft)/length);
		int axspan = axend-axstart;
		int length2 = intersectRightX-intersectLeftX;

		//x,y will be destination on screen
		//ax, ay is src from this image
		int ystart,ystop;
		ystart = yCenter-height/2;
		if(ystart<1)
			ystart=1;
		ystop=yCenter+height/2;
		if(ystop>=Constants.VIEWABLE_HEIGHT)
			ystop=Constants.VIEWABLE_HEIGHT-1;
			
		for(int y=ystart;y<ystop;y++)
		{
			int ay=(this.h*(y-yCenter+height/2))/height;			
			//remove this later:
			int index =y*Constants.VIEWABLE_WIDTH+intersectLeftX;
			int xstart,xstop;
			xstart = intersectLeftX;
			if(xstart<0)
				xstart=0;
			xstop = intersectRightX;
			if(xstop>=Constants.VIEWABLE_WIDTH)
				xstop=Constants.VIEWABLE_WIDTH-1;
			int color;
			try
			{
				int jubee =axspan*(xstart-intersectLeftX);
				for(int x=xstart;x<xstop;x++,index++,jubee+=axspan)
				{
						//if((color = modelTranslator.getRGB(_pixels[ay][(axstart+(axspan*(x-intersectLeftX))/length2)]))!=-16777216)
						//if((color = modelTranslator.getRGB(_pixels[ay][(axstart+jubee/length2)]))!=-16777216)
						if((color = _pixels[ay][(axstart+jubee/length2)])!=-16777216)
							screen[index]=color;
						
				}

				
				/*for(int x=xstart;x<xstop;x++,index++)
				//for(int x=intersectLeftX;x<intersectRightX;x++)
				{
					//int ax=(int)(axstart+axspan*((double)x-intersectLeftX)/length2);
					//int ax=(int)(axstart+(axspan*(x-intersectLeftX))/length2);
					//int ax=((axstart+(axspan*(x-intersectLeftX))/length2));
					//if((x>0)&&(x<Constants.VIEWABLE_WIDTH)&&(y>0)&&(y<Constants.VIEWABLE_HEIGHT))
					//if((y>0)&&(y<Constants.VIEWABLE_HEIGHT))
					
						//draw:	

						//if((ay>0)&&(ax>0)&&(ay<this.h)&&(ax<this.w))
					
							//if(_pixels[(int)ay][(int)ax]!=0)
							//{
							//int color = modelTranslator.getRGB(_pixels[ay][ax]);	
							//if((color = modelTranslator.getRGB(_pixels[ay][(axstart+(axspan*(x-intersectLeftX))/length2)]))!=-16777216)
					//if((ax>0)&&(ax<this.w))
							if((color = modelTranslator.getRGB(_pixels[ay][(axstart+(axspan*(x-intersectLeftX))/length2)]))!=-16777216)
								screen[index]=color;
								//screen[y*Constants.VIEWABLE_WIDTH+x]=color;
							//index++;
				}*/
			}
			catch(Exception e)
			{
			}
		}
	}
	
	
	public void nothing()
	{
		System.out.println("HERE");
		return;	
	}
	
	
	public int drawAsObject(int intersectLeftX,int intersectRightX, int xLeft,int xRight, int yCenter, int height, int distanceTo, boolean isGhost,int[] screen)
	{
		//Weird error when calling other method
		//System.out.println("HERE");
		//if(true)
		//{
			//call normal method
		//	drawAsObject(intersectLeftX, intersectRightX, xLeft, xRight, yCenter, height, screen);
		//if(0==0)
		//	return 0;
		//}

		
		if(intersectLeftX<0)
			intersectLeftX=0;
		if(intersectRightX>=Constants.VIEWABLE_WIDTH)
			intersectRightX=Constants.VIEWABLE_WIDTH;
		
		double length = xRight-xLeft;
		int axstart = (int)(this.w*((double)intersectLeftX-xLeft)/length);
		int axend = (int)(this.w*(intersectRightX-xLeft)/length);
		int axspan = axend-axstart;
		int length2 = intersectRightX-intersectLeftX;

		//x,y will be destination on screen
		//ax, ay is src from this image
		int ystart,ystop;
		ystart = yCenter-height/2;
		if(ystart<1)
			ystart=1;
		ystop=yCenter+height/2;
		if(ystop>=Constants.VIEWABLE_HEIGHT)
			ystop=Constants.VIEWABLE_HEIGHT-1;
			
		for(int y=ystart;y<ystop;y+=1)
		{
			int ay=(this.h*(y-yCenter+height/2))/height;			
			//remove this later:
			int index =y*Constants.VIEWABLE_WIDTH+intersectLeftX;
			int xstart,xstop;
			xstart = intersectLeftX;
			if(xstart<0)
				xstart=0;
			xstop = intersectRightX;
			if(xstop>=Constants.VIEWABLE_WIDTH)
				xstop=Constants.VIEWABLE_WIDTH-1;
			int color;
			try
			{
				int jubee =axspan*(xstart-intersectLeftX);
				//int colorDarkened;
				int []row = _pixels[ay];
				for(int x=xstart;x<xstop;x+=1,index+=1,jubee+=1*axspan)
				{
						//if((color = modelTranslator.getRGB(_pixels[ay][(axstart+(axspan*(x-intersectLeftX))/length2)]))!=-16777216)
						//if((color = modelTranslator.getRGB(_pixels[ay][(axstart+jubee/length2)]))!=-16777216)
						//if((color = _pixels[ay][(axstart+jubee/length2)])!=-16777216)
						if((color = row[(axstart+jubee/length2)])!=-16777216)
						{
							int colorDarkened =darken(color,distanceTo);
							screen[index]=colorDarkened;
							//screen[index+1+Constants.VIEWABLE_WIDTH]=colorDarkened;
							//if(!isGhost)
							//{
							//	screen[index+1]=colorDarkened;
							//	screen[index+Constants.VIEWABLE_WIDTH]=colorDarkened;
							//	
							//}
						}
				}
			}
			catch(Exception e)
			{
				//do nothing
			}

		}
		return 1;
	}

	
	
	public void texture(int x, int y, int w, int dh1, int dh2,double wallLeftX, double wallRightX, double intersectLeftX, double intersectRightX, int[] screen)
	{
		double dm = ((double)dh2-(double)dh1)/w;
		double wallLength = wallRightX-wallLeftX;
		double axstart = this.w*(intersectLeftX-wallLeftX)/wallLength;
		double axend = this.w*(intersectRightX-wallLeftX)/wallLength;
		double axspan = axend-axstart;
		
		int pixelIndex,height,aboveEnd,belowStart;
		
		//Make sure to draw whole thing
		//x--;
		//w++;
		
		int eraseColor = Constants.ERASE_COLOR;
		int color=-1;
		int translatedColor = -1;

		int jstart,jstop;
		
		
		//double ax=axstart;
		for(int i =0;i<w;i++)
		{
			height =((int)( dh1+dm*i));
			
			//draw blanks above and below at x+i
			aboveEnd = y-height/2;
			belowStart = y+height/2;

			pixelIndex =x+i;
			for(int j=0;j<aboveEnd;j++)
			{
				
				if((pixelIndex>0))
					screen[pixelIndex] = eraseColor;
				pixelIndex += Constants.VIEWABLE_WIDTH;
			}
			
			pixelIndex = x+i+belowStart*Constants.VIEWABLE_WIDTH;
			
			for(int j=belowStart;j<Constants.VIEWABLE_HEIGHT;j++)
			{
				if((pixelIndex<screen.length))
					screen[pixelIndex] = eraseColor;
				pixelIndex += Constants.VIEWABLE_WIDTH;
			}

			int ax = (int)(((double)i/w)*axspan+axstart);
			//ax+=axspan/w;
			if(ax>=this.w)
				ax=this.w-1;
			if(ax<0)
				ax=0;
			
			int xCoord = x+i;
				
//			if((xCoord>0)&&(xCoord<Constants.VIEWABLE_WIDTH))	
//			{
				//int startJ=-y+height/2;
				//int endJ=Constants.VIEWABLE_HEIGHT-y+height/2;
				
				//for(int j=startJ;j<endJ;j++)
				
				int ay;
				
				//int jstart;//=-y+height_2;
				if((jstart=-y+height/2)<0)
					jstart=0;
				
				//int jstop;//= Constants.VIEWABLE_HEIGHT-y+height_2-1;
				if((jstop= Constants.VIEWABLE_HEIGHT-y+height/2-1)>height)
					jstop=height;
				
				int yCoord =  y-height/2+jstart;//(+j)
				
				//j = -y+height/2
				
				//height = y-height/2+j
				//j = height-y+height/2;

				int startIndex=yCoord*Constants.VIEWABLE_WIDTH+xCoord;
				//double ayStart=((((double)jstart)/height)*this.h);
				//double ayInc=((this.h/height));
				
				//double h_d_h = ((double)this.h)/(height_2*2)-.01;

				//ay = ayStart;
				for(int j=jstart;j<jstop;j++)
				//for(int j=0;j<height;j++)
				{
					
					
					//ay = (int)(((double)j/(height))*this.h);
				//ay = (j*this.h)/height;
					// (255 << 24) | (red << 16) | blue
					//int rgbCOlor =(new Color(red,green,blue).getRGB());
					
					//int rgbColor = (green<<24)|(red<<16)|blue;
					
					//Color c= new	Color(color);
					//g.setColor(new Color(color));
					
					//g.drawLine(x+i,y+j-height/2,x+i,y+j-height/2);
					
					//int yCoord = y+j-height/2;
					//yCoord++;
	//				startIndex+=Constants.VIEWABLE_WIDTH;
					
					//if((yCoord>0)&&(yCoord<Constants.VIEWABLE_HEIGHT))
					//{

						//color = _pixels[ay][ax];
					
						//Change to RGB model
						//blue = modelTranslator.getBlue(color);
						//red	 = modelTranslator.getRed(color);
						//green = modelTranslator.getGreen(color);
						
						

						//pixelIndex = yCoord*Constants.VIEWABLE_WIDTH+xCoord;
						//if((pixelIndex>0)&&(pixelIndex<screen.length))
							//screen[pixelIndex] = color;
					//if(yCoord<Constants.VIEWABLE_HEIGHT)
						//screen[yCoord*Constants.VIEWABLE_WIDTH+xCoord] = modelTranslator.getRGB(_pixels[ay][ax]);//(255<<24)|(red<<16)|(green<<8)|blue;
					//screen[startIndex] = modelTranslator.getRGB(_pixels[(int)(((double)j/height)*this.h)][ax]);//(255<<24)|(red<<16)|(green<<8)|blue;
					
					//if((ay>=0)&&(ax>=0)&&(ay<this.h)&&(ax<this.w))
					//{

/*include this later for optimization
						if(color!= _pixels[ay][(int)ax])//(int)j*h_d_h)][ax])
						{
							//color = _pixels[(int)(j*h_d_h)][ax];
							translatedColor = modelTranslator.getRGB(color = _pixels[ay][(int)ax]);//(int)j*h_d_h)][ax])
						}
*/
					//}
					//else
					//{
					//	System.out.print("x"+ax);
					//}
					//screen[startIndex+=Constants.VIEWABLE_WIDTH] = modelTranslator.getRGB(_pixels[(j*this.h)/height][ax]);//(int)j*h_d_h)][ax]
					screen[startIndex+=Constants.VIEWABLE_WIDTH] =_pixels[(j*this.h)/height][ax];//(int)j*h_d_h)][ax]
					//screen[startIndex] = modelTranslator.getRGB(_pixels[(int)(j*h_d_h)][ax]);//(255<<24)|(red<<16)|(green<<8)|blue;
						
						
						
						//if(pixelIndex+Constants.VIEWABLE_WIDTH<screen.length)
						//screen[pixelIndex+Constants.VIEWABLE_WIDTH] = (255<<24)|(red<<16)|(green<<8)|blue;
					//}
					
				}//for
//			}//if
		}//for

		//for(int i=0;i<screen.length;i++)
		//	screen[i]=(255 << 24) | (120 << 16) | 154;
		
		return;
	}

	
	public void texture(int x, int y, int w, int dh1, int dh2,double wallLeftX, double wallRightX, double intersectLeftX, double intersectRightX, int[] screen,double distanceToLeft,double distanceToRight)
	{
		if(!Constants.DO_LIGHTING_EFFECTS)
		{
			//call normal method
			texture( x,  y,  w,  dh1,  dh2, wallLeftX,  wallRightX,  intersectLeftX,  intersectRightX, screen);
			return;
		}
		
		
		double dm = ((double)dh2-(double)dh1)/w;
		double wallLength = wallRightX-wallLeftX;
		double axstart = this.w*(intersectLeftX-wallLeftX)/wallLength;
		double axend = this.w*(intersectRightX-wallLeftX)/wallLength;
		double axspan = axend-axstart;
											
		double distanceSpan=distanceToRight-distanceToLeft;
		
		int pixelIndex,height,aboveEnd,belowStart;
		
		//Make sure to draw whole thing
		x--;
		w++;
		
		//int eraseColor = (255 << 24) | (123 << 16) | 150;
		int eraseColor = Constants.ERASE_COLOR;
		int color=-1;
		int translatedColor = -1;

		int jstart,jstop;
		
		//double ax=axstart;
		for(int i =0;i<w;i++)
		{
			height =((int)( dh1+dm*i));
			
			//draw blanks above and below at x+i
			aboveEnd = y-height/2;
			belowStart = y+height/2;

			pixelIndex =x+i;
			for(int j=0;j<aboveEnd;j++)
			{
				
				if((pixelIndex>0))
					screen[pixelIndex] = eraseColor;
				pixelIndex += Constants.VIEWABLE_WIDTH;
			}
			
			pixelIndex = x+i+belowStart*Constants.VIEWABLE_WIDTH;
			
			for(int j=belowStart;j<Constants.VIEWABLE_HEIGHT;j++)
			{
				if((pixelIndex<screen.length))
					screen[pixelIndex] = eraseColor;
				pixelIndex += Constants.VIEWABLE_WIDTH;
			}

				int ax = (int)(((double)i/w)*axspan+axstart);
				//ax+=axspan/w;
				if(ax>=this.w)
					ax=this.w-1;
				if(ax<0)
					ax=0;
		
				int xCoord = x+i;
				
				int ay;

				if((jstart=-y+height/2)<0)
					jstart=0;
				
				if((jstop= Constants.VIEWABLE_HEIGHT-y+height/2-1)>height)
					jstop=height;
				
				int yCoord =  y-height/2+jstart;//(+j)
				
				int startIndex=yCoord*Constants.VIEWABLE_WIDTH+xCoord;
				int distance = (int)(distanceToLeft+(distanceSpan*(ax-axstart))/axspan);
				
				for(int j=jstart;j<jstop;j++)
				{
					
					screen[startIndex+=Constants.VIEWABLE_WIDTH] = darken(_pixels[(j*this.h)/height][ax],distance);
					//screen[startIndex+=Constants.VIEWABLE_WIDTH] = darken(modelTranslator.getRGB(_pixels[(j*this.h)/height][ax]),distance);
					//screen[startIndex+=Constants.VIEWABLE_WIDTH] = modelTranslator.getRGB(_pixels[(j*this.h)/height][ax]);//(int)j*h_d_h)][ax]
				}		
		}
		return;
	}

	private int darken(int color,int distance)
	{
		//reuse variable
		//int toDarken= (distance*255)/Constants.DISTANCE_AT_WHICH_INVISIBLE;
		//distance= (distance*255)/Constants.DISTANCE_AT_WHICH_INVISIBLE;
		distance = distance/6;
		//darkens the color according to it's distance
		//Color thisColor = new Color(color);
		//int blue = thisColor.getBlue();
		//int red = thisColor.getRed();
		//int green = thisColor.getGreen();
		
		//color = (color & 0x00FFFFFF);
		int red = ((color & 0x00FFFFFF) >> 16)-distance;
		int green =( (color & 0x0000FFFF) >> 8)-distance;
		//reuse variable
		//int blue = (color & 0x000000FF)-distance;
		color = (color & 0x000000FF)-distance;
		
		//return (-16777216) | ((red << 16) | ((green << 8)|blue));		
		
		//if(blue<0)
		//	blue=0;
		//if(red<0)
		//	red=0;
		//if(green<0)
		//	green=0;
		//int left = 255<<24;
		
		return (-16777216) | (((red<0)?0:red << 16) | (((green<0)?0:green << 8)|((color<0)?0:color)));		
	}
	
	public void setPixels(int x, int y, int w, int h, ColorModel model,
						  byte pixels[], int off, int scansize)
	{
		int []copy = new int[pixels.length];
		for(int i=0;i<pixels.length;i++)
			copy[i]=model.getRGB(pixels[i]);
		_pixels[y] = copy;

	}

	public void setPixels(int x, int y, int w, int h, ColorModel model,
						  int pixels[], int off, int scansize)
	{
		modelTranslator = model;
		
		//System.out.println("x="+x+",y="+y+",w="+w+"h="+h+",scansize="+scansize+",off="+off);
		//if(_pixels==null)
		//	_pixels = new int[h];

		int []copy = new int[pixels.length];
		for(int i=0;i<pixels.length;i++)
			copy[i]=model.getRGB(pixels[i]);
		_pixels[y] = copy;
		
	}
	
	public void setDimensions(int w, int h)
	{
		this.h = h;
		this.w = w;
		_pixels = new int[h][];
	System.out.println("DImensions "+w+" "+h);
	}
	public void setProperties(Hashtable ht)
	{
		
	}
	public void imageComplete(int a)
	{
		this.imageComplete=true;
	}
	public void setHints(int a)
	{

	}

	public void setColorModel(ColorModel model)
	{
		this.modelTranslator=model;
	}

	
}
