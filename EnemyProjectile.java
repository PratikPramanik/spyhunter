/**
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev
    @created: 05/13/06

    Period: 4
    Assignment: FinalProject, SpyHunter 
    Version: 1.0
    
    Discription:
              
    Sources: Killer Game Programming dude
 */

import java.awt.*;

public class EnemyProjectile extends Projectile
{
	private static final String IMAGE_NAME_F= "enemyBullet";
	private static final String IMAGE_NAME_B= "enemyBullet";
	private static final int IMAGE_WIDTH = 36;
    private static final int IMAGE_HEIGHT = 22;
    private Sprite e;
    private Sprite c;
    
	public EnemyProjectile(int w, int h, ImagesLoader imsLd,
                               SpyHunterPanel p, Sprite c, EnemyCarManager em, int state, Sprite e)
	{
	
		super( (w - IMAGE_WIDTH) / 2, h / 2 - IMAGE_HEIGHT, w, h, imsLd, p, c, em, IMAGE_NAME_B, state);  
    	this.c = c;
    	this.e = e;

    	initPosition(); 
  	}
  	
	/**
	 *
	 * @param
	 *
	 * @return 
	 */	
  	// adjust the fireball's position and its movement left
  	public void initPosition()
  	{
        //System.out.println("initPosition");
        //System.out.println("Sprite c: " + c);
        //System.out.println("c.getXPosn(): " + c.getXPosn());
        
        //System.out.println("e.getXPosn(): " + e.getXPosn());

        //System.out.println("xStep: " + xStep);
      
        //System.out.println("yStep: " + yStep);
  		//int xStep = (c.getXPosn()-e.getXPosn())/20;
  		//int yStep = (c.getYPosn()-e.getYPosn())/20;
  	    int xStep = (c.getXPosn()- e.getXPosn())/20;
  	    int yStep = (c.getYPosn() - (e.getYPosn() + e.getHeight()))/20; 
    	//setInitPosition();
    	
    	/*if(getMyState() == FORWARD)	//moving forward
    	{
    		setPosition(e.getXPosn() + (e.getWidth() - IMAGE_WIDTH) / 2, e.getYPosn() - IMAGE_HEIGHT);  
      		setStep(xStep, yStep);   // move up
      	}*/
        if(getMyState() == BACKWARD)
    	{
    		if(yStep < 0)	
    			setImage(IMAGE_NAME_F);
    	   	setPosition(e.getXPosn(), e.getYPosn() + e.getHeight());   
    		setStep(xStep, yStep);   // move down
    		
    		//setPosition(e.getXPosn(), e.getYPosn() + e.getHeight());   
    		//setStep(0, -getMyStep());
    	}
    	else	//standing still
    	{
      		setStep(0, 0);   // move up*/ 
      	}
  		
  		// get access to random enemycar
  		// make a new projectile that shoots from this random car 
  		
  		
    	//setInitPosition();	
  	}
  	
  	/*public void setInitPosition()
  	{
  		Sprite c = getMyCar();
  		super.setPosition(c.getXPosn() + (c.getWidth() - IMAGE_WIDTH) / 2, c.getYPosn() - IMAGE_HEIGHT);   
  	}*/
  	
	/**
	 *
	 * @param
	 *
	 * @return 
	 */	
  	public void hasHitCar()
  	/* If the ball has hit jack, tell JackPanel (which will
     	display an explosion and play a clip), and begin again.
  	*/
  	{ 
	    Rectangle carBox = getMyCar().getMyRectangle();
    	carBox.grow(-carBox.width/5, -carBox.height/5);   // make car's bounded box thinner

   	 	if (carBox.intersects( getMyRectangle() )) //hit player car?
   	 	{
      		//System.out.println("Hit");
      		
      		((PlayerCar)c).addHealth(-2);
	    	setMyState(STILL); 
      		initPosition();
      	}
    }
}  // end of FireBallSprite class

