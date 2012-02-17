/**
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev
    @created: 05/13/06

    Period: 4
    Assignment: FinalProject, SpyHunter
    Version: Final
    
    Discription: This is an abstract class that contains all necessary attributes and methods for two different 
    types of projectiles. It is up to those children classes to define some of the generic methods outlined. 
    Essentially, a projectile is a bullet and can update itself, draw itself and check for interaction with cars.
              
    Sources: Killer Game Programming dude
 */

import java.awt.*; 

public abstract class Projectile extends Sprite
{
	//possible movement states
	protected static final int FORWARD = 1;
	protected static final int STILL = 0;
	protected static final int BACKWARD = -1;
	private int myState;
	
	private static final int STEP = -20;   	// moving up

	private EnemyCarManager myEnemyMan;
	private SpyHunterPanel myPanel;    		// tell JackPanel about colliding with jack
	private Sprite myCar;

	public Projectile(int x, int y, int w, int h, 
						ImagesLoader loader, SpyHunterPanel panel, 
						Sprite car, EnemyCarManager enemyMan, 
						String name, int state) 
	{ 
		//parameters in the following order: x, y position, width, height of projectile image, images loader, and a string file name
		super( x, y , w, h, loader, name);  

    	myState = state;
    	myCar = car;
    	myEnemyMan = enemyMan;
    	//initPosition();
  	} 
  	
    //getter Methods
  	
	/**
	 * Returns enemy car manager
	 *
	 * @return enemy car manager
	 */	
	public EnemyCarManager getMyEnemyMan()
	{
		return myEnemyMan;
	}
	
	/**
	 * Returns the spyHunterPanel refernce
	 *
	 * @return the spyHunterPanel refernce
	 */	
	public SpyHunterPanel getMyPanel()
	{
		return myPanel;
	}
	
	/**
	 * Returns state projectile is in
	 *
	 * @return state projectile is in
	 */	
	public int getMyState()
	{
		return myState;
	}
	
	/**
	 * Returns the projectile's step
	 *
	 * @return the projectile's step
	 */	
	public int getMyStep()
	{
		return STEP;
	}
	
	/**
	 * Returns my owner
	 *
	 * @return the Sprite that the projectile belongs to
	 */	
	public Sprite getMyCar()
	{
		return myCar;
	}
	
	/**
	 * Sets the Projectile's move state to either up, down or still 
	 *
	 * @param newState to be set
	 */	
	public void setMyState(int newState) 
	{
		myState = newState;
	}
	
	/**
	 * Adjust the fireball's position.
	 */	
  	abstract public void initPosition(); 
  	
  	/**
	 * Tests if either an enemy fit a player or vice-versa. If someone hit someone, then the appropriate 
	 * health meter goes down. Depending on the implimentation, either ask the enemy car manager if 
	 * the projectile hit an enemy, or test if player car is hit.
	 */
  	abstract public void hasHitCar(); 
  	
	/**
	 * Updates the projectile's location and if it has hit a car, decrements that car's health
	 */	
	//methods whose implimentations are shared amongst the enemies and player
  	public void updateSprite()
  	{ 
  		hasHitCar();
    	//goneOffScreen();
    	super.updateSprite();
  	}
  	
	/**
	 * Method largely replaced by a similar test in ProjectileManager. It should test if a projectile flew off screen and 
	 * report its findings
	 *
	 * @return true if bullet is offscreen
	 */	
	//the only implimentation
	private boolean goneOffScreen()
	// when the ball has gone off the lhs, start it again.
	{
	    /*if (((locy+getHeight()) <= 0) && (dy > 0)) // off left and moving left
	      initPosition();   // start the ball in a new position
	    else if (((locy-getHeight()) <= 0) && (dy > 0)) // off left and moving left
	      initPosition();   // start the ball in a new position*/
	    return false;
	}  // end of goneOffScreen()
}  // end of FireBallSprite class