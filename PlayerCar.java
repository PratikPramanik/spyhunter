/**
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev
    @created: 05/13/06

    Period: 4
    Assignment: FinalProject, SpyHunter
    Version: Final
    
    Discription: This class represents the player's car on screen and provides getter and setter methods for all of its 
    attributes along with drawing and update methods.
              
    Sources: Killer Game Programming dude
 */
 
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.*;

public class PlayerCar extends Sprite
{
	//navigation and location variables
  	private int pWidth;									
  	private int moveSize;
  	private int xWorld, yWorld;
    /* the current position of the sprite in 'world' coordinates.
       The x-values may be negative. The y-values will be between
       0 and pHeight. */
  
  	private static final int LEFT = 1;		//turning left
  	private static final int RIGHT = 2;		//turning right
  	private static final int NO_TURN = 0;	//not turning
  
  	private static final int IMAGE_WIDTH = 50;
  	private static final int IMAGE_HEIGHT = 88;
  	private static final int MOVE_SIZE = 14;	//size of step when turning
  
  	public boolean steady, lefting, righting;
  	private int hrzMoveMode;
  	private int hrzStep;	
  	
  	//life variables, health variable is private in sprite
  	private boolean isKilled;	
  	private static final int MAX_HEALTH = 50;	
  	private int hits;	
  
  	private EnemyCarManager enemyMan;			//used to track enemies
  	private ProjectileManager bulletMan;
  	boolean runsteady;
  	
  	private ClipsLoader clipsLoader;
  
  	public PlayerCar(int w, int h, int brickMvSz, ImagesLoader imsLd)
  	{
	    super((w - IMAGE_WIDTH) / 2, h / 2, w, h, imsLd, "DB9final");
	    setHealth(MAX_HEALTH);
	    
	    lefting = false;
	    righting = false;
	    steady = true;
	    hrzMoveMode = NO_TURN;
	    
	    xWorld = (w - IMAGE_WIDTH) / 2;
	    yWorld = h - IMAGE_HEIGHT;
	    
	    pWidth = w;
	    moveSize = MOVE_SIZE;

		//enemyMan = em;
		//(int w, int h, ImagesLoader imsLd, SpyHunterPanel p, Sprite c, EnemyCarManager em, boolean bool)
		//bulletMan = new ProjectileManager(w, h, imsLd, shp, this, shp.getECM(), true);	//create new playerProjectileManager
  	}
  	
	/**
	 * Creates a ProjectileManager for this car
	 *
	 * @param width of screen, height of screen, image loader, spy hunter panel, the car itself and the enemy manager
	 * 
	 */	
  	public void createManager(int w, int h, ImagesLoader imsLd, SpyHunterPanel shp, Sprite c, EnemyCarManager em)
    { 
        bulletMan = new ProjectileManager(w, h, imsLd, shp, c,  em, true);
    }
    
	/**
	 * @return x position on screen
	 */	
  	public int getX()
  	{ 
  		return xWorld;
  	}
  	
	/**
	 * @return y position on screen
	 */	
  	public int getY()
  	{ 
  		return yWorld;
  	}
  	
	/**
	 * Adds to health
	 *
	 * @param health increment to add to health
	 */	
  	public void addHealth(double healthInc)
  	{
  		if(getHealth() + healthInc > 0)
  			super.addHealth(healthInc);
  		else
  			isKilled = true;	
  	}
  	
	/**
	 * Increments the hit count (how many bullets hit enemies)
	 */	
  	public void hit()
  	{
  		hits++;			
  	}
  	
	/**
	 * @return number of bullets that hit enemies
	 */	
  	public int getHits()
  	{
  		return hits;
  	}
  	
	/**
	 * Updates the car according to its move mode
	 */	
	public void updateSprite()
  	/* Although the sprite is not moving in the x-direction, we 
		must still update its (xWorld, yWorld) coordinate. Also,
		if the sprite is jumping then its y position must be
		updated with moveVertically(). updateSprite() should
		only be called after collsion checking with willHitBrick()
  	*/
  	{
		//update bullet state		
	    bulletMan.update();
	    
	    if(getHealth() < 50)
	   		addHealth(0.03);
	   			
	   	//update sprite's state		
		if(willCollide())
		{
		    steadyMe();
		}
	    if (!steady)
	    {    
	    	if (hrzMoveMode == RIGHT)
	      		updRighting();
	    	else if (hrzMoveMode == LEFT)
	      		updLefting();	
	    }
  	}
  	
	/**
	 * Updates the left turn
	 */	
	private void updLefting()
	{
		//System.out.println("xWorld ; " + getX());
		if(getX() - 5 <= 0)
      		finishTurning();	
    	else
    	{
    	 	xWorld -= moveSize; 
    		translate(-moveSize, 0);
    	}
	}
	
	/**
	 * Updates the right turn
	 */	
	private void updRighting() 
	{
	    //System.out.println("xWorld ; " + getX());
		if(getX() + IMAGE_WIDTH >= pWidth)
      		finishTurning();	
	    else
    	{
    	 	xWorld += moveSize; 
    		translate(moveSize, 0);
    	}
	}
	
	/**
	 * Draws the sprite and asks the projectile manager to display itself
	 *
	 * @param Graphics
	 */	
  	public void drawSprite(Graphics dbg)
  	{
  		super.drawSprite(dbg);
  		
  		bulletMan.display(dbg);
  	}
  	
  	public void updateECM(EnemyCarManager em)
  	{
  		enemyMan = em;
  	}
  	
	/**
	 * Translates car up
	 */	
	public void moveUp()
	{
  		translate(0,-20);
	}
	
	/**
	 * Translates car down
	 */	
	public void moveDown()
	{
  		translate(0,20);
	}  
	
	/**
	 * Calls a method to set all moving variables to false
	 */	
  	public void steadyMe()
  	{
  		finishTurning();
  	}
  	
	/**
	 * Asks projectile manager to shoot forward
	 */	
  	public void shootForward()
  	{
  	    bulletMan.shootForward();	
  	}
  	
	/**
	 * Asks projectile manager to shoot backward
	 */	
  	public void shootBackward()
	{
		bulletMan.shootBackward();
	}
	
	/**
	 * Translates car left
	 */	
  	public void turnLeft()
  	{
	  	hrzMoveMode = LEFT;
	  	//setImage("DB9shaded");	//setImage("DB9left");
	  	lefting = true; righting = false; steady = false;
	  	
	  	//translate(-10,0);
  	}
  	
	/**
	 * Translates car right
	 */	
  	public void turnRight()
  	{
	  	hrzMoveMode = RIGHT;
	  	//setImage("DB9shaded");	//setImage("DB9right");
	  	righting = true; lefting = false; steady = false;
	  	
	  	//translate(10,0);
  	}
  	
	/**
	 * Sets all moving variables to false
	 */	
	private void finishTurning()
	{
		hrzMoveMode = NO_TURN;
  	
		lefting = false;
		righting = false; 
		steady = true;
  	}
  	
	/**
	 * Tells SHP if game is over, is killed is set to true if health is about to go negative
	 */	
	public boolean isDead()
	{
		return isKilled;
	}
	
	/**
	 * Asks enemy manager if it will collide with any of the enemies
	 */	
	public boolean willCollide()
	{
		if(hrzMoveMode == RIGHT)
		{
			//System.out.println("Car moving Right");
			return enemyMan.willCrashCars(getMyRectangle(), MOVE_SIZE, true, 0);
		}
		else if(hrzMoveMode == LEFT)
		{
			//System.out.println("Car moving Left");
			return enemyMan.willCrashCars(getMyRectangle(), MOVE_SIZE, false, 0);
		}
		else
		{
			//System.out.println("Car Still");
			return false;
		}
	}
}
  
