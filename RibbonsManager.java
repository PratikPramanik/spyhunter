/**
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev
    @created: 05/13/06

    Period: 4 
    Assignment: FinalProject, SpyHunter
    Version: Final
    
    Discription: This class take care of moving, drawing and updating ribbon objects.
              
    Sources: Killer Game Programming dude
 */
 
// RibbonsManager.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* RibbonsManager manages many ribbons (wraparound images 
   used for the game's background). 

   Ribbons 'further back' move slower than ones nearer the
   foreground of the game, creating a parallax distance effect.

   When a sprite is instructed to move left or right, the 
   sprite doesn't actually move, instead the ribbons move in
   the _opposite_direction (right or left).

*/

import java.awt.*;

public class RibbonsManager
{
	private String ribImage = new String ("road");
	private Ribbon ribbon;
	private int moveSize;
     // standard distance for a ribbon to 'move' each tick

	public RibbonsManager(int w, int h, int brickMvSz, ImagesLoader imsLd)
	{
		moveSize = brickMvSz;
          // the basic move size is the same as the bricks ribbon

		ribbon = new Ribbon(w, h, imsLd.getImage(ribImage),moveSize);
  	}  // end of RibbonsManager()

  	/**
	 * Makes the ribbon scroll
	 */	
  	public void moveUp()
	{ 
		ribbon.moveUp();
	}
	
	/**
	 * Stops the ribbon
	 */	
	public void stayStill()
	{
    	ribbon.stayStill();
	}
	
	/**
	 * Updates th ribbon's state
	 */	
	public void update()
	{
    	ribbon.update();
	}
	
	/**
	 * Displays th ribbon
	 *
	 * @param graphics
	 */	
	public void display(Graphics g)
	{
      ribbon.display(g);
	}
} // end of RibbonsManager

