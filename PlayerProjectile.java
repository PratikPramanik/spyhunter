/**
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev
    @created: 05/13/06

    Period: 4
    Assignment: FinalProject, SpyHunter
    Version: Final
    
    Discription: This class represents the bullets a player car fires. It overrides some methods from projectile 
    to achieve desired behavior.
              
    Sources: Killer Game Programming dude
 */

import java.awt.*;

public class PlayerProjectile extends Projectile
{
	private static final String IMAGE_NAME= "bullets-big";
	private static final int IMAGE_WIDTH = 30;
    private static final int IMAGE_HEIGHT = 12;
    
	public PlayerProjectile(int w, int h, ImagesLoader imsLd,
                               SpyHunterPanel p, Sprite c, EnemyCarManager em, int state)
	{ 
		super( (w - IMAGE_WIDTH) / 2, h / 2 - IMAGE_HEIGHT, w, h, imsLd, p, c, em, IMAGE_NAME, state);  
  
    	//   the ball is positioned in the middle at the panel's rhs
    	initPosition();
  	} 

	/**
	 * Puts projectile in starting position
	 */	
  	// adjust the fireball's position and its movement left
  	public void initPosition()
  	{
  		Sprite c = getMyCar();
    	//setInitPosition();
    	
    	if(getMyState() == FORWARD)	//moving forward
    	{
    		setPosition(c.getXPosn() + (c.getWidth() - IMAGE_WIDTH) / 2, c.getYPosn() - IMAGE_HEIGHT);  
      		setStep(0, getMyStep());   // move up
      	}
    	else if(getMyState() == BACKWARD)	//moving back
    	{
    		setPosition(c.getXPosn() + (c.getWidth() - IMAGE_WIDTH) / 2, c.getYPosn() + c.getHeight());   
    		setStep(0, -getMyStep());
    	}
    	else	//standing still
    	{
      		setStep(0, 0);   // move up*/
      	}
  	}
  	
  	/*public void setInitPosition()
  	{
  		Sprite c = getMyCar();
  		super.setPosition(c.getXPosn() + (c.getWidth() - IMAGE_WIDTH) / 2, c.getYPosn() - IMAGE_HEIGHT);   
  	}*/
  	
	/**
	 * Asks enemy manager if the projectile has hit something, its state gets set to STILL
	 */	
  	public void hasHitCar()
  	/* If the ball has hit jack, tell JackPanel (which will
     	display an explosion and play a clip), and begin again.
  	*/
  	{ 
	    boolean hit = getMyEnemyMan().hasHitCars(getMyRectangle(), 5);
	    
	    if(hit)
	    {
	    	((PlayerCar)getMyCar()).hit();	//increments players killCount
	    	setMyState(STILL); 
	    	initPosition();
	    }
	}
}  // end of FireBallSprite class

