/**
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev
    @created: 05/13/06

    Period: 4
    Assignment: FinalProject, SpyHunter
    Version: Final
    
    Discription: This class takes care of shooting, updating and drawing Projectile objects
              
    Sources: Killer Game Programming dude
 */

//libraries to import
import java.awt.*;
import javax.swing.*;
import java.awt.image.*;
import java.util.*;

public class ProjectileManager
{
	private Projectile[] shots;				//projectile aray
		
	private EnemyCarManager enemyMan;		//enemy manager to pass on to Projectile
	private SpyHunterPanel shp;   			//spy hunter panel, also for Projectiles
	
	//player and enemy cars, depending on projectile created are either the hunters or the hunted
	//this manager could either belong to either car or be after it
	private Sprite car;						
	private Sprite enemy;				
	
	private int width;						//width of window
	private int height;						//height of window
	private ImagesLoader imsLoader;			//image loader
	private boolean isPlayer; 				//boolean used to create either player or enemy projectiles
	
	private int level;						//current level
	
	//constructor for player mostly and for other universal use
	public ProjectileManager(int w, int h, ImagesLoader imsLd, SpyHunterPanel p, Sprite c, EnemyCarManager em, boolean bool)
	{
	  isPlayer = bool;
	  
	  shots = new Projectile[5];
	  
      width = w;
	  height = h;
	  imsLoader = imsLd;
	  shp = p;
	  car = c;
	  enemyMan = em;
	}
	
	//constructor for enemy
	public ProjectileManager(int w, int h, ImagesLoader imsLd, SpyHunterPanel p, Sprite c, Sprite e)
	{
	  isPlayer = false;
	  
	  shots = new Projectile[5];
	  
	  width = w;
	  height = h; 
	  imsLoader = imsLd;
	  shp = p;
	  car = c;
	  enemy = e; 
	}
	
	/**
	 * Makes projectiles (either player or enemy ones) that point forward
	 */	
	public void shootForward()
	{
		//System.out.println("--Shooting forward");
		
		//System.out.println(shots.length); 
		
		if(isPlayer)
		{
			for(int i=0; i<shots.length; i++)
			{ 
				//System.out.println(shots[i]);
			  	if(shots[i] == null)
				{
					//System.out.println("---Shooting forward");
					shots[i] = new PlayerProjectile(width, height, imsLoader, shp, car, enemyMan, 1);
					break; 
				}
		    }
		}
		///**
		else
		{
			for(int i=0; i<level; i++)
			{ 
				//System.out.println(shots[i]);
			  	if(shots[i] == null)
				{  
					//System.out.println("---Shooting forward");	
					shots[i] = new EnemyProjectile(width, height, imsLoader, shp, car, enemyMan, -1, enemy);
					break; 
				}
		    }
		}//*/
	}
	
	/**
	 * makes only PlayerProjectiles since EnemyProjectiles aim themselves and do not need to be fired from the 
	 * front or the back.
	 */	
	public void shootBackward()
	{
	    for(int i=0; i<shots.length; i++)
		{
			if(shots[i] == null)
			{
				shots[i] = new PlayerProjectile(width, height, imsLoader, shp, car, enemyMan, -1);
				break;
			}
		}
	}
	
	/**
	 * Updates all projectiles and removes those that are either STILL or off screen
	 */	
	public void update()
	{
		for(int i=0; i<shots.length; i++)
		{
			if(shots[i] != null)
			{
				//System.out.println("shot " + i + ":" + shots[i]);
				 
				// move shot
				((Sprite)shots[i]).updateSprite();


				// test if shot is out
				if(((Sprite)shots[i]).getYPosn() < 0 || ((Sprite)shots[i]).getYPosn() > height ||
				     ((Projectile)shots[i]).getMyState() == 0) 
				{	
				    shots[i] = null;	
	            }
	        }
	    }     
	}
	
	/**
	 * Draws all projectiles
	 *
	 * @param Graphics
	 */	
	public void display(Graphics g)
	{
	    for(int i=0; i<shots.length; i++)
		{
			if(shots[i] != null)	
			// move shot
			{
				((Sprite)shots[i]).drawSprite(g);   
		    	//System.out.println("*** " + (Sprite)shots[i]);
			}
		}    
	}
	
	public void updateLevel()
	{
		level = shp.getLevel();
		if(level > 5)
		{
			level = 5;
		}
	}
}