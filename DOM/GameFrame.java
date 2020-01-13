import java.awt.Frame;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.awt.image.*;
import java.awt.Color;

public class GameFrame extends Frame implements MouseMotionListener, WindowListener, MouseListener, KeyListener
{

	Image screenImage;
	MemoryImageSource mem;
	Game _game;
	
	public GameFrame()
	{
		super(Constants.WINDOW_TITLE);
		_game=null;
		
		//Window/event related
		this.setVisible(true);
		this.setSize(Constants.WINDOW_WIDTH,Constants.WINDOW_HEIGHT);
		this.setBackground(Constants.BACKGROUND);
		
		this.addWindowListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.setResizable(false);
		this.addKeyListener(this);
		
	
	}

	public void update(Graphics g)
	{
		//Needs to be overridden to avoid flashing
	}
	
	public void setGame(Game game)
	{
		_game = game;
		_game.setGameFrame(this);
		//Add repaint thread
		//(new RepaintRequestThread(this)).start();
	}
	
	public void setScreenImage()
	{
		synchronized(this)
		{
			_game.render();
		
			int [] pixels =  _game.getImageData();//art.getScreenPixelData();//this retrieves that image

		
			if(pixels!=null)
			{
				if(mem==null)//Using same memory, no need to recreate object
				{
					mem = new MemoryImageSource(Constants.VIEWABLE_WIDTH, Constants.VIEWABLE_HEIGHT,ColorModel.getRGBdefault(),pixels, 0, Constants.VIEWABLE_WIDTH);
					mem.setAnimated(true);
					//mem.setFullBufferUpdates(false);
				}
				if(screenImage==null)				
					screenImage = createImage(mem);
				else
					mem.newPixels(pixels,ColorModel.getRGBdefault(),0,Constants.VIEWABLE_WIDTH);
					screenImage.flush();//no need to recopy memory
			}		
		}
	}
	
	public void paint(Graphics g)
	{
		synchronized(this)
		{
			g.setColor(Color.white);
			g.drawRect(Constants.VIEWABLE_UPPER_LEFT_X-1,Constants.VIEWABLE_UPPER_LEFT_Y-1,Constants.VIEWABLE_WIDTH+2,Constants.VIEWABLE_HEIGHT+2);
		
			if((_game!=null)&&(_game.readyToDraw))//art!=null)
			{
				if(screenImage!=null)
				{
					g.drawImage(screenImage,Constants.VIEWABLE_UPPER_LEFT_X,Constants.VIEWABLE_UPPER_LEFT_Y,this);
				}
			}
			
			_game.drawInfo(g);
			
		}
	}
	
	
	public void mouseDragged(MouseEvent e){}
	public void mouseMoved(MouseEvent e)
	{}
	public void windowOpened(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowClosing(WindowEvent e){System.exit(0); }
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e)
	{/*changeVarForTestOnClick();*/}
	
	public void keyPressed(KeyEvent e)
	{ 
		_game.keyPressed(e);
		//this.setScreenImage();
		//paint(this.getGraphics());
	}
	public void keyReleased(KeyEvent e)
	{
		_game.keyReleased(e);
	}
	public void keyTyped(KeyEvent e){}

}
