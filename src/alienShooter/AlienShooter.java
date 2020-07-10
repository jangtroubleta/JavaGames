package alienShooter;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class AlienShooter extends Applet implements Runnable, MouseMotionListener {
	private static final long serialVersionUID = 1L;

Thread clock;
	
	Image off;	// virtual screen
	Graphics offG;
	
	Image bgImg;	// background image
	Image alien;	// alien image
	int x, y;		// alien x, y coordinate
	
	int mouseX, mouseY;	// mouse x, y coordinate
	
	int delayTime;
	
	@Override
	public void init() {
		System.out.println("init routine");
		off = createImage(500, 500);
		offG = off.getGraphics();
		
		MediaTracker myTracker = new MediaTracker(this);
		
		bgImg = getImage(getCodeBase(), "..\\images\\alienShooter\\map.gif");
		myTracker.addImage(bgImg, 0);
		alien = getImage(getCodeBase(), "..\\\\images\\\\alienShooter\\\\shooter.gif");
		myTracker.addImage(alien, 0);
		
		try {
			myTracker.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		x = 225; y = 225;
		mouseX = 225; mouseY = 225;
		delayTime = 50;
		
		addMouseMotionListener(this);
		
	}
	
	@Override
	public void start() {
		System.out.println("start routine");
		if(clock == null) {
			clock = new Thread(this);
			clock.start();
		}
		
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(off, 0, 0, this);
		
		
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
		
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void run() {
		while(true) {
			try {
				clock.sleep(delayTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(x < mouseX) {
				x++;
			}else if(x > mouseX) {
				x--;
			}
			
			if(y < mouseY) {
				y++;
			}else if(y > mouseY) {
				y--;
			}
			
			offG.setColor(Color.white);
			offG.fillRect(0, 0, 500, 500);
			offG.drawImage(bgImg, 0, 0, this);
			offG.drawImage(alien, x, y, this);
			
			repaint();
		}
		
	}
	
	@Override
	public void stop() {
		System.out.println("stop routine");
		if((clock!=null) && (clock.isAlive())) {
			clock = null;
		}
		
	}
	
	@Override
	public void destroy() {
		System.out.println("destroy routine");
		
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		delayTime = 25;
		if(e.getX() <= 500 && e.getX() >= 0) {
			mouseX = e.getX() - 12;
		}
		if(e.getY() <= 500 && e.getY() >= 0) {
			mouseY = e.getY() - 12;
		}
		
		repaint();
		
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		delayTime = 5;
		if(e.getX() <= 500 && e.getX() >= 0) {
			mouseX = e.getX() - 12;
		}
		if(e.getY() <= 500 && e.getY() >= 0) {
			mouseY = e.getY() - 12;
		}
		
	}
	
}
