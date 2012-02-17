/**
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev
    @created: 05/13/06 

    Period: 4
    Assignment: FinalProject, SpyHunter
    Version: Final
    
    Discription: This manages all of the enemies, adds and removes them. All universal movements and actions 
    are handled by this class. 
              
    Sources: Killer Game Programming dude
 */

//import these libraries 
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;
 
public class EnemyCarManager
{
	//dimention and display related variables
	private int pWidth;					//width of window
	private int pHeight;				//height of window
	private int enemyHeight;			//height of enemyImage
	private int enemyWidth;				//width of enemyImage
	private ImagesLoader imageLd;		//image loader class
		
	//data structure and quantative analysis variables
	private LinkedList enemies;			//list of active cars
 	private int size;					//currently active
 	private int total;					//how many this manager has had overall
 	private int killed;					//total amt killed from this manager
 	private int numRamming;
 	
 	private SpyHunterPanel SHP;			//reference to SpyHunterPanel
 	
 	//gameplay varaibles
 	private int level;					//current level determined by killCount
 	
 	public EnemyCarManager(int w, int h, int brickMvSz, ImagesLoader imsLd, SpyHunterPanel sPanel)
 	{
 		SHP = sPanel;
 		 
 		pWidth = w;
 		pHeight = h;	
 		enemyHeight = 90;
 		enemyWidth = 50;
 		imageLd = imsLd;
 		
 		enemies = new LinkedList();
 		killed = 0;
 		//add(new EnemyCar(0,0, w, h, brickMvSz, imsLd, this));
 		//add(new EnemyCar(100,0, w, h, brickMvSz, imsLd, this));
 		//add(new EnemyCar(200,0, w, h, brickMvSz, imsLd, this));
 		size = enemies.size();
 		total = size;
 		
 		level = SHP.getLevel();
 	}
 	
	/**
	 * Adds an enemy 
	 *
	 * @param EnemyCar to be added to the data structure
	 */	
 	public void add(EnemyCar baddie)
 	{
 		enemies.add(baddie); //add to end of list
 		size++;
 		total++;
 	}
 	
	/**
	 * Removes an enemy
	 *
	 * @param EnemyCar to be removed from the data structure
	 */	
 	public void remove(EnemyCar trash)
 	{
 		if(enemies.remove(trash))
 		{
 			//only way to remove is to kill pretty much
 			killed++;
 			size--; 
 		}
 		else
 		{
 			System.out.println("remove failed");
 			//debug
 		}
 	}
 	
	/**
	 * Updates all of the enemies and thus their BulletManagers. Also is responsible for adding and removing enemies 
	 * if necessary and calling a method to show an explosion once an enemy dies.
	 */	
 	public void update()
 	{
 		Random rand = new Random();
 		size = enemies.size();				//keep size updated 
 		
 		//killed = rand.nextInt(20);			//test
 		
 		//if there are no more enemies, add some
 		if(size == 0)
 		{
	 		int numToMake = rand.nextInt(3) + 1;	
	 		makeRandEnemies(numToMake);
 		}
 		//or make one enemy if fate says so 
 		//else if(rand.nextInt(100) == 0)
 		//	makeRandEnemies(1);
 		
 		Iterator iter = enemies.iterator();		//iterator
 		
 		//runs update of all cars in sequence order;
 		while(iter.hasNext())
 		{
 			//System.out.println("while");
 			EnemyCar currEnemy = (EnemyCar)iter.next();
 			
 			if(currEnemy.getHealth() <= 0) 
 			{
 				SHP.showExplosion(currEnemy.getX() + enemyWidth / 2, currEnemy.getY() + enemyHeight / 2);
 				//SHP.showExplosion(locx, locy+getHeight()/2);
 				iter.remove();
 				killed++;
 			}
 			else
 				currEnemy.update();
 		}
 		
 		if(killed < 10)
 		{
 			level = 1;
 		}
 		else if(killed < 20)
 		{
 			level = 2;
 		}
 		else if(killed < 40)
 		{
 			level = 3;
 		}
 		else if(killed < 50)
 		{
 			level = 4;
 		}
 		else if(killed < 80)
 		{
 			level =5;
 		}
 		
 		SHP.setLevel(level);
 	}
 	
 	/**
	 * Makes a specified number of enemies in random locations and adds them to the data structure
	 *
	 * @param number of enemies to make in random locations
	 */	
 	private void makeRandEnemies(int numToMake)
 	{
 		int numMade = 0;
 		
	 	while(numMade < numToMake)
	 	{
	 		Point newLocation = randomLoc();
	 		int x = (int)newLocation.getX();
	 		int y = (int)newLocation.getY();
	 		if(isEmpty(x,y))
	 		{
	 			add(new EnemyCar(x, y, pWidth, pHeight, imageLd, this));	
	 			numMade++;
	 		}
	 	}
 		
 	}
	/**
	 * Generates a random location for enemies to spawn in slightly outside the window.
	 *
	 * @return a Point object containing a random location of x and y off the screen.
	 */	
 	private Point randomLoc()
 	{
 		Point loc;
 		Random gen = new Random();
 		
 		if(gen.nextInt(3) == 0)//if random is 1, put car on bottom
 		{
 			loc = new Point(gen.nextInt(pWidth) - enemyWidth, pHeight);
 		}
 		else //if random is 0, put car on top
 		{
 			loc = new Point(gen.nextInt(pWidth) - enemyWidth, -enemyHeight);
 		}
 		
 		return loc;
 	}
 	
 	/**
	 * Debug method that calls the first enemy's shoot method
	 */	
 	public void shoot()
 	{   
 		//System.out.println("ENEMY MAN AT 0: " + (EnemyCar)enemies.get(0));
 		EnemyCar en = (EnemyCar)enemies.get(0);
 		en.shootForward();
 	}
 
	/**
	 * This method is used by PlayerProjectile to determine if it hit an enemy. The metod cycles through the 
	 * list of enemies and sees is a bullet overlaps with any of them.
	 *
	 * @param a Rectangle representing the dimentions of the bullet and a factor by which the player's 
	 * rectangle would shrink. This is used to show the bullet actually hitting the target
	 *
	 * @return a boolean of whether or not the bullet hit an enemy
	 */	
 	public boolean hasHitCars(Rectangle bulletRect, int shrinkFactor)	//rectangle will be shrunk 1/shrinkFactor
 	{
 		Iterator iter = enemies.iterator();		//iterator
 		while(iter.hasNext())
 		{
 			EnemyCar currEnemy = (EnemyCar)iter.next();
			Rectangle enemyRect = currEnemy.getMyRectangle();
			
			//if we need to show bullets going into the car
			if(shrinkFactor > 0)
				enemyRect.grow(-enemyRect.width/shrinkFactor, -enemyRect.height/shrinkFactor);   // make car's bounded box thinner
	
			//if a hit is scored, return true
	    	if (enemyRect.intersects(bulletRect))
	    	{    
				currEnemy.addHealth(-10);
				return true;
			}
 		}
 		return false;  
	}
	
	/** 
	 * This method is used as collision detectin by PlayerCar and tests if the player car's intended move is blocked by an 
	 * enemy.
	 *
	 * @param carRecatangle represents the dimentions of the player car, moveSize is the amount player is about to move, 
	 * goingRight tells which way the player is about to move and the shrink factor allows cars to overlap is desired.
	 *
	 * @return true if any enemies are about to be crushed, false otherwise
	 */	
	public boolean willCrashCars(Rectangle carRect, int moveSize, boolean goingRight, int shrinkFactor)	//rectangle will be shrunk 1/shrinkFactor
 	{
 		Iterator iter = enemies.iterator();		//iterator
 		//System.out.println("Size of enemies "+ enemies.size());
 		while(iter.hasNext())
 		{
 			EnemyCar currEnemy = (EnemyCar)iter.next();
			Rectangle enemyRect = currEnemy.getMyRectangle();
			if(shrinkFactor > 0)	
				enemyRect.grow(-enemyRect.width/shrinkFactor, -enemyRect.height/shrinkFactor);   // make car's bounded box thinner
				
 			//variables to represent dimentions of the carRectangle
 			int x = (int)carRect.getX();
 			int y = (int)carRect.getY();
 			int w = (int)carRect.getWidth();
 			int h = (int)carRect.getHeight();
 			
 			//the rectangle will be shifted depending upon the car's expected move
 			if(goingRight)
 				carRect.setBounds(x + moveSize, y, w, h);
 			else
 				carRect.setBounds(x - moveSize, y, w, h); 
 			
 			//if the car's expected silhouette crosses the enemy's, return true
 			if (enemyRect.intersects(carRect))
 			{
 				currEnemy.addHealth(-10);
				return true;
			}
 		}
 		return false;
 	}
 	
 	/**
 	 * This method is used by enemies to restrict their movement, it cycles throught all of the enemies and compares 
 	 * their silhouette with the enemy's that is passed as a parameter
 	 *
	 * @param reference to enemy car which is the one moving currently, it's rectangle is compared to the rest of the 
	 * enemies' rectangles
	 *
	 * @return true if an enemy will collide with another enemy
	 */	
 	public boolean willCrashWithEn(EnemyCar myCar)
 	{
 		Iterator iter = enemies.iterator();
 		Rectangle myRect = myCar.getMyRectangle();
 		int myHMode = myCar.getHMode();
 		
 		//variables to represent dimentions of the carRectangle
 		int x = (int)myRect.getX();
 		int y = (int)myRect.getY();
 		int w = (int)myRect.getWidth();
 		int h = (int)myRect.getHeight();
 		
 		if(myHMode == 2)
 			myRect.setBounds(x + 10, y, w, h);	//10 is the magic movesize
 		else
 			myRect.setBounds(x - 10, y, w, h); //10 is the magic movesize
 		
 		while(iter.hasNext())
 		{
 			EnemyCar currEnemy = (EnemyCar)iter.next();
 			
 			//don't compare the myCar to itself
 			if(currEnemy != myCar)
 			{
 				Rectangle enemyRect = currEnemy.getMyRectangle();
	 			
	 			//if the two rectangles intersect
	 			if( enemyRect.intersects(myRect) )
	 			{ return true; }
 			}
 			
 		}
 		
 		return false;
 	}
 	
 	
 	public int distanceAway(EnemyCar myCar)
 	//WORKS IN CONJUCTION WITH ABOVE METHOD willCrashWithEn()
 	//returns int distance away.
 	{
 		Iterator iter = enemies.iterator();
 		Rectangle myRect = myCar.getMyRectangle();
 		int myHMode = myCar.getHMode();
 		
 		//variables to represent dimentions of the carRectangle
 		int x = (int)myRect.getX();
 		int y = (int)myRect.getY();
 		int w = (int)myRect.getWidth();
 		int h = (int)myRect.getHeight();
 		
 		if(myHMode == 2)
 			myRect.setBounds(x + 10, y, w, h);	//10 is the magic movesize
 		else
 			myRect.setBounds(x - 10, y, w, h); //10 is the magic movesize
 		
 		while(iter.hasNext())
 		{
 			EnemyCar currEnemy = (EnemyCar)iter.next();
 			
 			//don't compare the myCar to itself
 			if(currEnemy != myCar)
 			{
 				Rectangle enemyRect = currEnemy.getMyRectangle();
	 			
	 			if( enemyRect.intersects(myRect) )
	 			{
	 				return (int)enemyRect.getX() - x;
	 			}
 			}
 			
 		}
 		return 0;
 	}
 	
 	/**
	 * This method tests if a certain location is emty and is used in random enemy genaration
	 *
	 * @param x and y coordinates of the enemy whish is about to appear in a certain location and needs to know if it is empty
	 *
	 * @return whether or not the x y is an empty location
	 */	
 	public boolean isEmpty(int x, int y)
 	{
 		Rectangle myRect = new Rectangle(x, y, enemyWidth, enemyHeight);  
 		
 		Iterator iter = enemies.iterator();		//iterator
 		
 		while(iter.hasNext())
 		{
 			EnemyCar currEnemy = (EnemyCar)iter.next();
			Rectangle enemyRect = currEnemy.getMyRectangle();
			
	    	if (enemyRect.intersects(myRect))
	    	{    
				return false;
			}
 		}
 		return true;  
 	}
	/**
	 * This method calls of the enemies' draw methods and thus draws all of the enemies
	 *
	 * @param Graphics class that will actually do the drawing 
	 */	
 	public void display(Graphics g)
 	{
 		Iterator iter = enemies.iterator();
 		
 		//cycle through enemy list
 		while(iter.hasNext())
 		{
 			((EnemyCar)iter.next()).drawSprite(g);
 		}
 	}
 	
	/**
	 * Returns the number of active enemies
	 *
	 * @return size of List
	 */	
 	public int getSize()
 	{
 		return size;
 	}
 	
	/**
	 * Returns the number of cars killed from this manager
	 *
	 * @return number of enemies killed
	 */	
 	public int getKillCount()
 	{
 		return killed;
 	}
 	
 	
 	/**
	 * Sets the number of cars killed from this manager
	 *
	 * @return number of enemies killed
	 */
 	public int setKC(int num)
 	{
 		killed+= num;
 		return killed;
 	}
 	
	/**
	 * Returns the total number of enemies ths manager has had
	 *
	 * @return the total number of enemies, active and killed
	 */	
 	public int total()
 	{
 		return total;
 	}
 	
	/**
	 * Returns a reference to the player car.
	 *
	 * @return a reference to the player car
	 */	
 	public PlayerCar getPC()
 	{
 		return SHP.getPC();
 	}
 	
	/**
	 * Returns a reference to the SpyHunterPanel
	 *
	 * @return reference to the SpyHunterPanel
	 */	
 	public SpyHunterPanel getPanel()
 	{
 	    return SHP;
 	}
 	
 	public int getLevel()
 	{
 		return level;
 	}
}
