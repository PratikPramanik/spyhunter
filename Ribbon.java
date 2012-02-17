/**
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev
    @created: 05/13/06

    Period: 4
    Assignment: FinalProject, SpyHunter
    Version: Final
    
    Discription: this is the background ribbon object, essentially the road.
              
    Sources: Killer Game Programming dude
 */
 
// Ribbon.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* A ribbon manages an image which is wider than the game panel's
   width: width >= pWidth

   When a sprite is instructed to move left or right, the 
   sprite doesn't actually move, instead the ribbon moves in
   the _opposite_direction (right or left). The amount of movement 
   is specified in moveSize.

   The image is wrapped around the panel, so at a given moment
   the tail of the image, followed by its head may be visible 
   in the panel.

   A collection of ribbons are managed by a RibbonsManager object.
*/

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

public class Ribbon
{
	private BufferedImage im;		//image of the road
	private int height;      		// the height of the image (>= pHeight)
	private int pWidth, pHeight;    // dimensions of display panel

	private int moveSize;       	// size of the image move (in pixels)
	private boolean isMovingUp;  	// movement flag

	private int yImHead;   
     
     /* The y-coord in the panel where the start of the image
        (its head) should be drawn. 
        It can range between -height to height (exclusive), so can
        have a value beyond the confines of the panel (0-pHeight).

        As yImHead varies, the on-screen ribbon will usually
        be a combination of its tail followed by its head.
     */
	public Ribbon(int w, int h, BufferedImage im, int moveSz)
	{
	    pWidth = w; pHeight = h;
	    
	    this.im = im;
	    height = im.getHeight();    // no need to store the width
	    if (height < pHeight) 
	      System.out.println("Ribbon height < panel height");
	
	    moveSize = moveSz;
	    isMovingUp = false;   // no movement at start
		yImHead = 0;
	}  // end of Ribbon()
	
	/**
	 * Makes the ribbon move up
	 */	
	// move the ribbon image to the right on the next update
	public void moveUp()
	{ 
		isMovingUp = true;
	}
	
	/**
	 * Don't move the ribbon image on the next update if this method is called
	 */	
	public void stayStill()
	{ 
		isMovingUp = false;
	}
	
	/**
	 * Updates the head of the ribbon which is later used in display
	 */	
	public void update()
	/* Increment the yImHead value depending on the movement flags.
		It can range between -height to height (exclusive), which is
		the height of the image.
	*/
	{
		if (isMovingUp)
			yImHead = (yImHead + moveSize) % height;

		// System.out.println("yImHead is " + yImHead);
	} // end of update()
	
	
	
	/*  Text from Chapter 12 adapted for the specific purpose of a top to bottom scroller:
	 
		The regions are always the same width, starting at the left edge of the JPanel(x==0) 
		and extending to its right (x==pWidth). However, dy1 and dy2 will vary in the JPanel, 
		and sy1 and sy2 vary in the image.
		
		The y-coordinates are derived from the current yImHead value, which ranges between 
		height and -height as the image is shifted down through the JPanel.
		
		Also, as the image moves down, there will come a point when it will be necessary to 
		draw both the head and the tail of the image in order to cover the JPanel.
		
		These considerations lead to display consisting of three cases. However, there might 
		come a time when five will bwe needed, if it is necessary to have he image scroll both 
		up and down.
	*/
	
	/* Consider 3 cases: 
       when yImHead == 0, draw only the im head
       when yImHead > 0, draw the im tail and im head, or only the im tail.

     yImHead can range between -height to height (exclusive)
	*/
	
	/**
	 * Displays the ribbon as discribed above and below
	 *
	 * @param Graphics
	 */	
	public void display(Graphics g)
	{
	   	/*
	    3 cases
	    	1: Display image from top, with some of the bottom cropped
	    	This case occurs at very start, and every revolution, it repeats.
	    	2: Displays part of teh head and part of the tail of the image.
	    	Requires two calls to draw for both of the parts. This is the most
	    	frequent case of them all.
	    	3: Displays only the tail with some of the top of the image cut off. 
	    	Repeats every revolution. This is also a frequently called case, as it is 
	    	quite generic.
	    */
	   
	    //1
	    if (yImHead == 0)   // draw im head at (0,0)
	    {
	    	draw(g, im, 0, pHeight, 0, pHeight);
	    }
	    //2
	    else if ((yImHead > 0) && (yImHead < pHeight)) 
	    {  
	    	// draw im tail at (0,0) and im head at (0, yImHead)
			draw(g, im, 0, yImHead, height-yImHead, height);   // im tail
	    	draw(g, im, yImHead, pHeight, 0, pHeight-yImHead);  // im head
	    }
	    //3
	    else if (yImHead >= pHeight)
	    {
	   		draw(g, im, 0, pHeight, height-yImHead, height-yImHead+pHeight);  // im tail
	   	}
	} // end of display()
	
	/**
	 * Simply calls the draw method for the ribbon's image
	 *
	 * @param Graphics, the road image, and x, y coordinates for onscreen and in image file use
	 */	
	private void draw(Graphics g, BufferedImage im, 
						int screenY1, int screenY2, int imageY1, int imageY2)
	/* The x-coords of the image always starts at 0 and ends at
		pWidth (the width of the panel), so are hardcoded. */
	{ 
		//d coordinates are for the display or screen, s coordinates are the image or the source
		
		//                   dx1       dy1       dx2        dy2      sx1     sy1       sx2      sy2
		//g.drawImage(im, displayX1,    0   , displayX2,  pHeight, imageX1,   0   ,  imageX2, pHeight , null); //horizontal params
		//new params
		  g.drawImage(im,    0     ,screenY1,  pWidth  , screenY2,    0   ,imageY1,  pWidth , imageY2 , null);
	}
}  // end of Ribbon
