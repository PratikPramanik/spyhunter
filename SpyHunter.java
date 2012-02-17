
/**
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev 
    @created: 05/13/06
 
    Period: 4
    Assignment: FinalProject, SpyHunter
    Version: Final
    
    Discription: This the future tester class, so far completely undeveloped
              
    Sources: Killer Game Programming dude
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class SpyHunter extends JFrame implements WindowListener
{
  private static int DEFAULT_FPS = 25;      // 40 is too fast! 

  private SpyHunterPanel shp;        // where the game is drawn
  private MidisLoader midisLoader;


  public SpyHunter(long period)
  { super("SpyHunter");

    // load the background MIDI sequence
    midisLoader = new MidisLoader();
    midisLoader.load("jjf", "crocket.mid");
    midisLoader.play("jjf", true);   // repeatedly play it
    
    Container c = getContentPane();    // default BorderLayout used
    shp = new SpyHunterPanel(this, period);
    c.add(shp, "Center");

    addWindowListener( this );
    pack();
    setResizable(false);
    setVisible(true);
  }  // end of JumpingJack() constructor


  // ----------------- window listener methods -------------

  public void windowActivated(WindowEvent e) 
  { shp.resumeGame();  }

  public void windowDeactivated(WindowEvent e) 
  { shp.pauseGame();  }


  public void windowDeiconified(WindowEvent e)  
  {  shp.resumeGame();  }

  public void windowIconified(WindowEvent e) 
  {  shp.pauseGame(); }


  public void windowClosing(WindowEvent e)
  {  shp.stopGame();  
     //midisLoader.close();  // not really required
  }


  public void windowClosed(WindowEvent e) {}
  public void windowOpened(WindowEvent e) {}

  // ----------------------------------------------------

  public static void main(String args[])
  { 
    long period = (long) 1000.0/DEFAULT_FPS;
    // System.out.println("fps: " + DEFAULT_FPS + "; period: " + period + " ms");
    new SpyHunter(period*1000000L);
  }

} // end of JumpingJack class


