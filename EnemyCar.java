/**
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev
    @created: 05/13/06 

    Period: 4
    Assignment: FinalProject, SpyHunter
    Version: 1.0
    
    Discription:
              
    Sources: Killer Game Programming dude
 */
 
/*	ecarSprite will have extensions of it
 *	so each enemy varies (intial build will only have one)
 *
 *	ram, collide
 *	AI method
 *	^ helper specifics:
 *	trackPlayer (detects player distance, acts accordingly)
 *	attackFar (if far, then stay and fire projectile)
 *	attackClose (if close, then move in to side of car and ram)
 *	
 *	conditions which enemy changes behavior
 *	-randomly chooses to move close and ram
 *	-when health it low, kamikazee (move in close and ram)
 *	-when player shoots (maybe, as this is pretty advanced AI)
 *		
 *	
 *	controlled by EnemyCarManager
 *
 */

import java.util.Random;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.*;
 
public class EnemyCar extends Sprite
{
 	//private long period;    // in ms; the game's animation period
 	private int xWorld, yWorld;
 	
 	//ATTRIBUTES
 	private static final int MAX_HEALTH = 50;
 	private ProjectileManager bulletMan;
	private int strg;	//strength class
	
	//COMMUNICATION VARIABLES
	private EnemyCarManager ECM;		//this Enemy's family
 	private PlayerCar player;
 	
	//MOVEMENT VARIABLES
	private static final int LEFT = 1; 		//turning left
	private static final int RIGHT = 2;		//turning right
	private static final int NO_TURN = 0;		//not turning
	  
	private static final int MAX_TURN_STEPS = 8;	//left/right distance
	public boolean steady, lefting, righting;
	private int hrzMoveMode;
	private int hrzStep;						
	private int turnCount; 					//how far turned so far
	//private int moveSize;   
	  
	private int attackMode;
 	private boolean AIturn; //when true, calc new Attackmode: when false don't
 	private int AIwait;
 	private Random AIroll;
 	
	//MOOD VARIABLES
	private static final int SHOOT = 1;
	private static final int RAM = 2;
	private static final int PASSIVE = 0;
 	
 	//TRACKING VARIABLES
 	private boolean movingToDown; 	//true when moving to player's side from top
 	private boolean movingToUp;		//true when moving to player's side from bottom
 	private boolean onRight;		//on the right of player
 	private boolean onLeft;			//on the left of player
 	private boolean tooclose;		//whether the car is too close to the player
 	
 	//RAMMING VARIABLES
 	private long startTime;
 	private boolean paralyzed;
 	private int paratime;
 	private boolean angry;
 	private int ramprob;			//out of ten, probability of ramming [ramprob/10]
 	
 	//Environmental variables/ Animation varaibles
 	private boolean onScreen;		//if in visible area
 	private boolean onRoad;			//if you are on the visible road
 	//private int deathAnimtime = 0; 	//time used up on death animation
 	private boolean blowingUp;		//if the anim is blowingup!
 	private boolean leftsway;		//during passive swaying left
 	private int swaytime;			//time to sway in one direction
 	private boolean willCrash;		//will crash into another enemy
 	
 	//Sound
 	ClipsLoader clips;	//gets the shots clipLoaders
 	
 	
 	public EnemyCar(int x, int y, int w, int h, /*int brickMvSz,*/ ImagesLoader imsLd, EnemyCarManager enemyMan)
    {
    	super(x, y, w, h, imsLd, "BMWfinal");
    	setHealth(MAX_HEALTH);
    	//regardless stuff
    	//moveSize = brickMvSz;
    	//period = p;
    	lefting = false; righting = false; steady = true; 
    	willCrash = false;
    	strg = 2;
    	
    	hrzMoveMode = NO_TURN;
    	turnCount = 0;
    	
    	AIroll = new Random();
    	startTime = System.nanoTime();
    	
    	ECM = enemyMan;
    	player = ECM.getPC();
    	//(int w, int h, ImagesLoader imsLd, SpyHunterPanel p, Sprite c, EnemyCarManager em, boolean bool)
    	//bulletMan = new ProjectileManager(w, h, imsLd, ECM.getPanel(), this, ECM, false);
    	bulletMan = new ProjectileManager(w, h, imsLd, enemyMan.getPanel(), player, this);
    	
    	clips = ECM.getPanel().getShots();
    }
    
    
    //inserts the new enemy beyond the screen and
    //summons it to move onto the screen ready for battle
    //loc is random and offscreen.
    /*private void initialize(int w, int h, int brickMvSz, ImagesLoader imsLd, long p)
    {
    	
    }*/
    
    public void drawSprite(Graphics dbg)
  	{
  		super.drawSprite(dbg);
  		
  		bulletMan.display(dbg);
  		
  	}
    
    /**
	 *
	 * @param
	 *
	 * @return 
	 */
    public void update()
    //handles every main action of the Enemy
    {
    	//moveDown();
    	//turnRight();
    	//turnLeft();
    	//moveUp();
    	
    	bulletMan.updateLevel();
        bulletMan.update();
        
        //reset crash test
    	willCrash = false;
    	
    	int yPlayer = player.getYPosn();
    	int xPlayer = player.getXPosn();
    	
    	if( getHealth() <= 0)
    	{
    		die();
    		return;
    	}
    	
    	
    	if(paralyzed)
    	{
    		//setImage("BMWparalyzed");
    		attack(); 			//will invoke paralyze count down
    		return;
    	}
    	
    	willCrash = ECM.willCrashWithEn(this);
    	
    	if( willCrash )
    	{
    		finishTurning();
    		//translate(-ECM.distanceAway(this), 0);
    	}
    	else
    	{
	    	//check y location 
	    	int ypos = locy;
	    	if(ypos < 30 || ypos > getPHeight()-100) //compensate for car size
	    	{
	    		onScreen = false;
	    	}
	    	else
	    	{
	    		onScreen = true;
	    	}
	    	
	    	//check x location
	    	int xpos = locx;
	    	if(xpos < 100 || xpos > 700-50)		//compensate for car size
	    	{
	    		onRoad = false;
	    	}
	    	else
	    	{
	    		onRoad = true;
	    	}
	    	
	    	if(!onRoad)
	    	{
	    		moveOntoRoad();
	    	}
	    	//if offScreen, move into world
	    	if(!onScreen)
	    	{
	    		moveIntoWorld();
	    	}
	    }
	    
    	//ram procedure
    	if( attackMode == RAM && !paralyzed )
		{	
			//System.out.println("RAM PROCEDURE");
			int ydistaway = yPlayer - getYPosn();
			int xdistaway = xPlayer - getXPosn();
			
			moveToward();
			if( movingToDown )	//from bottom
			{
				if( ydistaway < 20 )
				{
					//translate(0, ydistaway); 	//so not to move to far down
				}
				else
				{
					moveDown();
				}
				
			}
			else if( movingToUp )	//from bottom
			{
				if( ydistaway > 10 )
				{
					//translate(0, ydistaway);	//so not to move to far up
				}
				else 
				{
					moveUp();
				}
			}
			
			
			if(xdistaway > 50) //on the left
			{
				turnRight();
			}
			else if(xdistaway < -50) //on the right
			{
				turnLeft();
			}
			else if(xdistaway >= 20)
			{
				//turnLeft();
				//translate(xdistaway-100,0);
			}
			else if(xdistaway <= -20)
			{
				//turnRight();
				//translate(-xdistaway,0);
			}
			//return;
    	}
    	
    	//creates sort of a wait period for new AI.
    	if( AIturn )
    	//recalculate decisions then carry out actions
    	{
    		setAMode(AI());
    		AIturn = false;
    		AIwait = 0;
    		attack();
    	}
    	else if( AIwait >= 12 )
    	//after 12 turns it gets to use its brains
    	{
    		attack();
    		AIturn = true;
    	}
    	else
    	//steady action
    	{
    		attack();
    		AIwait++;
    	}
    }
	
	/**
	 *
	 *	move the cars from the top or bottom of the screen
	 *	
	 */	
    private void moveIntoWorld()
    {
    	int ypos = locy;
    	
    	if(ypos < 30)
    	{
    		moveDown();
    	}
    	if(ypos > getPHeight()-100)
    	{
    		moveUp();
    	}
    }
    
	/**
	 * makes sure the car stays on the road and doesnt swerve onto the grass
	 * moves it away basically
	 *
	 *	the numbers are based on the image used
	 * 
	 */	
    private void moveOntoRoad()
    {
    	int xpos = locx;
    	
    	if(xpos < 100)
    	{
    		turnRight();
    	}
    	if(xpos > 700-50) //compensate for car size
    	{
    		turnLeft();
    	}
    }
    
	/**
	 *
	 *	changes the mode of attack
	 *
	 * @param
	 *a static variable of mod type
	 *
	 * @return 
	 *the int attackmode
	 */	
    public int setAMode(int mode)
    {
    	attackMode = mode;
    	return attackMode;
    }
    
	/**
	 *paralyzes the car for a certain amount of time
	 *
	 *
	 * @param
	 *int value of time
	 * @return 
	 *the time left paralyzed
	 */	
    public int setPara(int time)
    {
    	if(paratime > 0)
    	{
    		paratime = time;
    	}
    	return paratime;
    }
    
	/**
	 *
	 * @return 
	 *location x
	 */	
    public int getX()
  	{ 
  		return super.getXPosn();
  	}
  	
	/**
	 *
	 * @return 
	 *returns location Y
	 */	
  	public int getY()
  	{ 
  		return super.getYPosn();
  	}
  	
  	/**
	 *
	 * @return 
	 * returns health left
	 */	
    //** return condition methods
    public int getHealth()
    // returns the health value
    { 
    	return super.getHealth(); 
    }
    
	/**
	 *
	 * @returns
	 *strength class, used for modifiers 
	 */	
    public int getStrg()
    // returns strength class
    { 
    	return strg; 
    }
    
	/**
	 *not used in final version
	 *returns the that smount of time left to be paralyzed
	 *
	 * @return 
	 * int value of how much time left to be paralyzed
	 */	
    public int getParaTime()
    // returns time paralyzed so far
    { 
    	return paratime; 
    }
    
	/**
	 *
	 * @return 
	 *returns the mode its attacking in in INT form
	 */	
    public int getAMode()
    { 
    	return attackMode; 
    }

	/**
	 * @return 
	 * the horizontal movement direction in INT form
	 */	
	 public int getHMode()
	 {
	 	return hrzMoveMode;
	 }    
    
    
	/**
	 *Main shoot method
	 *called all the time for shooting
	 *
	 *outsoruces the code to the appropriate class projectileManager
	 */	
    public void shootForward()
  	{
  	    bulletMan.shootForward();	
  	}
  	
	/**
	 *Never used. just a testmethod
	 *
	 *calls projectileManager and shoots backwards
	 */	
  	public void shootBackward()
	{
		bulletMan.shootBackward();
	}
	
	
    //**Movement methods. Set to private as we dont want the client
    //messing with it?
    public void turnLeft()
    /**
	 *moves the car left, changes move modes
	 *
	 *
	 * @param
	 * noine
	 * @return 
	 * none
	 */
    {
    	hrzMoveMode = LEFT;
    	lefting = true; righting = false; steady = false;
    	
    	translate(-10,0);
    }
    
	/**
	 *
	 *moves car right, changes all move modes
	 *
	 * @param
	 *none
	 * @return 
	 *none
	 */	
    public void turnRight()
    {
    	hrzMoveMode = RIGHT;
    	lefting = false; righting = true; steady = false;
    	
    	translate(10,0);
    }
    
    
	/**
	 *moves the car up 10 pixels
	 *
	 * @param
	 *noine
	 * @return 
	 *none
	 */	
    public void moveUp()
    {
    	translate(0, -10); //its harder to go faster, so it lower
    }
    
	/**
	 *moves the car down 10 pixels
	 *
	 * @param
	 *none
	 * @return 
	 *none
	 */	
    public void moveDown()
    {
    	translate(0, 15); 
    }
    
	/**
	 *Finish turning sets all turn values to true
	 *
	 *
	 * @return
	 *no return 
	 */	
    public void finishTurning()
    {
    	hrzMoveMode = NO_TURN;
    	lefting = false; righting = false; steady = true;
    }
    
    //**End move methods.
    
	/**
	 *
	 * @param
	 *
	 * @return 
	 */	
    public void attack()
    //Judges which attack to use, fire bullet or ram.
    //creates new AimedBullet, or calls ram method, or nothing
    {
    	//all attackModes are set in AI.
    	
    	//if paralyzed, cant attack
		if(paratime > 0 || paralyzed)
		{
			//**reduce time to paralyze
			paratime--;
			paralyzed = true;
			if(paratime == 0)
			{
				paralyzed = false; //paralyze time is over
				//setImage("BMWfinal");
			}
			return;
		}
    	
    	if(attackMode == PASSIVE)
    	{
	    	if(!willCrash)
	    	{
	    		if(leftsway)
	    		{
	    			turnLeft(); //turnLeft
	    		}
	    		else
	    		{
	    			turnRight(); //turnRight
	    		}
	    		
	    		swaytime++;
	    		if(swaytime > 8)
	    		{
	    			if(leftsway == false)
	    			{
	    				leftsway = true;
	    			}
	    			else
	    			{
	    				leftsway = false;
	    			}
	    			swaytime = 0;
	    		}
    		}
    		else
    		{
    			translate(50-ECM.distanceAway(this), 0);
    		}
    	}
    	
    	if(attackMode == SHOOT)
    	{
    		int shootroll = AIroll.nextInt(10); // get random roll
    		
    		if(shootroll < 5)
    		{
    			shootForward(); //**BANG BANG
    			//System.out.println("shooting");
    			clips.play("efire", false);
    		}
    	}
    	
    	if(attackMode == RAM)
    	{
    		ram();
    	}
    }
    
	/**
	 *
	 * @param
	 *
	 * @return 
	 */	
    //** private classes that are called under certain conditions
    private void moveToward()
    //called when RAM attackMode is active
    //this is the most complicated...
    //the Enemy runs through series of actions
    //to move toward the player's side
    //has to always compensate
    {
    	int yPlayer = player.getYPosn(); //get Player's Y cood
    	int xPlayer = player.getXPosn(); //get Player's X cood
    	
    	if(getYPosn() < yPlayer)
    	{
    		movingToDown = true; movingToUp = false;
    		
    		//while(getYPos() < yPlayer)
	    	//{
	    	//	moveDown();
	    	//}
    	}
    	
    	else if(getYPosn() >= yPlayer)
    	{
    		movingToDown = false; movingToUp = true;
    		
    		//while(getYPos() > yPlayer)
	    	//{
	    	//	moveUp();
	    	//}
    	}
    	
    	if( !willCrash )
    	{
	    	if(getXPosn() > xPlayer)
	    	{
	    		onRight = true; onLeft = false;
	    		//keep distance down here for more immediate response
	    		//having it up in update() is not good enough
	    		if(getXPosn() - xPlayer < 50)
	    		{
	    			turnRight();
	    		}
	    	}
	    	else if(getXPosn() <= xPlayer)
	    	{
	    		onRight = false; onLeft = true;
	    		if(getXPosn() - xPlayer > -50)
	    		{
	    			turnLeft();
	    		}
	    	}
	    }
	    else
	    {
	    	translate(-ECM.distanceAway(this), 0);
	    }
    	
    }
    
	/**
	 *
	 * @param
	 *
	 * @return 
	 */	
    private int AI()
    //called *every 4th* step
    //this is complicated... NOT REALLY
    //deteremines action to take... sets attackMode.
    //calls upon the setAMode() method to change attackMode.
    //sets the attackMode at random based on probability
    //if bring rammed, the enemy will always ram back (coded later)
    //if hit by bullet(or health really low)
    //once in ram, it keeps on ramming
    {
    	if( attackMode == RAM ) //|| getHealth() < 10 )
    	{
    		return RAM; //return RAM if already RAMMING.
    	}
    	
    	int roll = AIroll.nextInt(20); // get random roll
    	
    	if(roll >= 0 && roll < 10)
    	//5 times out of 10, Enemy with shoot
    	{
    		//System.out.println("SHOOTING");
    		setAMode(SHOOT);
    		return SHOOT;
    	}
    	else if(roll >= 10 && roll < 19
    	)
    	//3 times out of 10, it will be passive
    	{
    		//System.out.println("NOTHING");
    		setAMode(PASSIVE);
    		return PASSIVE;
    	}
    	else if(roll >= 19 && roll < 20)
    	//2 times out of 10, it'll go kamikazee and ram you
    	{
    		//System.out.println("RAMMING");
    		setAMode(RAM);
    		return RAM;
    	}
    	else
    	{return PASSIVE;} //if none are true SOMEHOW, return PASSIVE
    }
    
	/**
	 *
	 * @param
	 *
	 * @return 
	 */	
    private void ram()
    // when either called, or used in close proximity to player
    // basically moves into the player and lowers their health.
    // however, the dmg done is modified by a mathematical equation
    // taking into account both car's strength classes.
    //the visible action is for the car to move to the side a little,
    //then move into Player.
    //future ideas: add particle effects to smash
    {
    	//crash test
    	//System.out.println("RAMMING");
    	
    	//visual stuff
    	long currTime = System.nanoTime();
    	if((currTime - startTime)/1000000000L > 5)
    	{
    		//System.out.println((System.nanoTime() - startTime)/1000000000L);
    		for(int n = 0; n < 10; n++)
    			{translate(1,0);} //actually hit
    		//System.out.println("TRANSLATING");
    		startTime = System.nanoTime();
    	}
    	else if((currTime - startTime)/1000000000L > 6)
    	{
    		for(int n = 0; n < 10; n++)
    			translate(-1,0);
    	}
    	player.addHealth(-0.06);
    }
    
	/**
	 *
	 * @param
	 *
	 * @return 
	 */	
    private void die()
    // when health is below 0.
    // blow this baby up.
    // physical changes image to looping explosion.
    {
    	/*(ECM.getPanel()).showExplosion(locx+25, locy+45);*/
    	
    	//ECM.remove(this);
    	//ECM.setKC(1);
    }
}




    	/*
    	int dmg = 10;
    	
    	if(onLeft)
    	{
    		//loopImage("BMWram_fromLeft");
    		//player.setHealth(dmg);			//CHANGE TO MATH ALGORITH
    		//player.paralyze();
    	}
    	else if(onRight)
    	{
    		//loopImage("BMWram_fromRight");
    		//player.setHealth(dmg);			//CHANGE TO MATH ALGORITH
    		//player.paralyze();
    	}*/