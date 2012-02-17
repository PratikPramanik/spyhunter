/** 
    @author: Pratik Pramanik, Melody Chung, Aleksey Shepelev
    @created: 05/13/06

    Period: 4
    Assignment: FinalProject, SpyHunter
    Version: 
    
    Discription:
              
    Sources: Killer Game Programming dude
 */

import javax.swing.*; 
import java.awt.image.*;
import java.awt.event.*;
import java.awt.*;
//import com.sun.j3d.utils.timer.J3DTimer;
import java.lang.*;


public class SpyHunterPanel extends JPanel 
                     implements Runnable, ImagesPlayerWatcher
{
  	//technically, the road is 800 by 1600
  	private static final int PWIDTH = 800;   // size of panel
  	private static final int PHEIGHT = 600; 

  	private static final int NO_DELAYS_PER_YIELD = 16;
  	/* Number of frames with a delay of 0 ms before the animation thread yields
     to other running threads. */
  	private static final int MAX_FRAME_SKIPS = 5;
    // no. of frames that can be skipped in any one animation loop
    // i.e the games state is updated but not rendered

  	// image, bricks map, clips loader information files
  	private static final String IMS_INFO = "imsInfo.txt";
  	private static final String SNDS_FILE = "clipsInfo.txt";
  	private static final String SHOTS_FILE = "shotsInfo.txt";
  
  	private Thread animator;           // the thread that performs the animation
  	private volatile boolean running = false;   // used to stop the animation thread
  	private volatile boolean isPaused = false;

  	// used at game termination
  	private volatile boolean gameOver = false;
  	private int score = 0;
  
  	private long gameStartTime;   // when the game started
  	private int timeSpentInGame;

  	private long period;
  	private SpyHunter jackTop;
  
  	//for displaying messages
  	private Font msgsFont;
  	private FontMetrics metrics;

  	// to display the title/help screen
  	private boolean showHelp;
  	private BufferedImage helpIm, gameOverIm;
  
  	// object managers
  	private RibbonsManager ribsMan;     // the ribbons manager
  	private EnemyCarManager enemyMan;
  
  	// sprite objects
  	private PlayerCar db9;
  
  	private int numHits = 0;

  	// off-screen rendering
	private Graphics dbg; 
	private Image dbImage = null;
	ImagesLoader imsLoader;
	
	/*//EXPLOSION variables
	private ImagesPlayer[] explosionPlayers = new ImagesPlayer[10]; //max of ten explosions
	private int explWidth, explHeight;   // image dimensions
  	private int xExpl, yExpl;   // coords where image is drawn*/
  	
  	// names of the explosion clips
  	private static final String[] exploNames ={"explode1", "explode2", "explode3"};
  	
  	// explosion-related
  	private ImagesPlayer explosionPlayer = null;
  	private boolean showExplosion = false;
  	private int explWidth, explHeight;   // image dimensions
  	private int xExpl, yExpl;   // coords where image is drawn
  	
  	//SoundLoader
  	private ClipsLoader clipsLoader;	//for everything but gunshots
  	private ClipsLoader shotsOnly;		//for gunshots
  	private ClipsLoader playShots;		//for player shots
  	
  	//Gameplay Variables
  	private int level;					//determines what level ur on

  	public SpyHunterPanel(SpyHunter jj, long p)
  	{
	    //initialize panel dimentions
	    setDoubleBuffered(false);
	    setBackground(Color.white);
	    setPreferredSize( new Dimension(PWIDTH, PHEIGHT));
	
	    setFocusable(true);
	    requestFocus();    // the JPanel now has focus, so receives key events
	     
	    period = p;
	  	
	    jackTop = jj;
	
		//make new keyListener class
		addKeyListener( new KeyAdapter() 
		{
			public void keyPressed(KeyEvent e)
			{ 
	       		processKey(e);   
			}
	    });
	
		gameStartTime = System.nanoTime(); 
		
	    // initialise the loaders
	    imsLoader = new ImagesLoader(IMS_INFO); 
	    
	    // prepare title/help screen
	    helpIm = imsLoader.getImage("title");
	    gameOverIm = imsLoader.getImage("gameover");
	    showHelp = true;    // show at start-up
	    isPaused = true;
	    
	    // set up message font
	    msgsFont = new Font("Verdana", Font.PLAIN, 20);
	    metrics = this.getFontMetrics(msgsFont);
	
		int brickMoveSize = 40;
		
		clipsLoader = new ClipsLoader(SNDS_FILE);
		shotsOnly = new ClipsLoader(SHOTS_FILE);
		playShots = new ClipsLoader(SHOTS_FILE);
	    ribsMan = new RibbonsManager(PWIDTH, PHEIGHT, brickMoveSize, imsLoader);
	    db9 = new PlayerCar(PWIDTH, PHEIGHT, brickMoveSize, imsLoader);
	    enemyMan = new EnemyCarManager(PWIDTH, PHEIGHT, brickMoveSize, imsLoader, this);
	    db9.updateECM(enemyMan);
	    db9.createManager(PWIDTH, PHEIGHT, imsLoader, this, db9, enemyMan);
	    
	    // prepare the explosion animation
    	explosionPlayer =  new ImagesPlayer("explosion", (int)(period/1000000L), 
                                                0.5, false, imsLoader);
    	BufferedImage explosionIm = imsLoader.getImage("explosion");
    	explWidth = explosionIm.getWidth();
    	explHeight = explosionIm.getHeight();
    	explosionPlayer.setWatcher(this);     // report animation's end back here
	    
	    level = 1;
	    /*
	    //EXPLOSIONS!
	    for(int i=0; i<explosionPlayers.length; i++)
		{
			explosionPlayers[i] = new ImagesPlayer("explosion", (int)(period/1000000L), 
													0.5, false, imsLoader);
			explosionPlayers[i].setWatcher(this);
		}
	    BufferedImage explosionIm = imsLoader.getImage("explosion");
	    explWidth = explosionIm.getWidth();
    	explHeight = explosionIm.getHeight();*/
	    
  	}  // end of JackPanel()

	private void processKey(KeyEvent e)
	// handles termination, help, and game-play keys
	{
		int keyCode = e.getKeyCode();

    	// termination keys
		// listen for esc, q, end, ctrl-c on the canvas to
		// allow a convenient exit from the full screen configuration
    	if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
        	(keyCode == KeyEvent.VK_END) ||
        	((keyCode == KeyEvent.VK_C) && e.isControlDown()) )
      		running = false;

    	// help controls
    	if (keyCode == KeyEvent.VK_H) 
    	{
	      	if (showHelp) 
	      	{  // help being shown
	        	showHelp = false;  // switch off
	        	isPaused = false;
	      	}
	      	else 
	      	{  // help not being shown
	      		clipsLoader.play( "menu", false);
	       		showHelp = true;    // show it
	       		isPaused = true;    // isPaused may already be true
	      	}
    	}
    
	    if(!isPaused && !gameOver)
		{
		    ribsMan.moveUp();
		    if (keyCode == KeyEvent.VK_DOWN) 
		    {
		    	db9.steadyMe();
		    	//ribsMan.stayStill();
		    }
		    else if(keyCode == KeyEvent.VK_LEFT)
		    	db9.turnLeft();
		    else if(keyCode == KeyEvent.VK_RIGHT)
		    	db9.turnRight();
		    else if(keyCode == KeyEvent.VK_ENTER)
		    	db9.moveUp();
		    else if(keyCode == KeyEvent.VK_SHIFT)
		    	db9.moveDown();
		    
		    
		    else if (keyCode == KeyEvent.VK_SPACE)  // shoot forwards
		    { 
		        playShots.play("pfire", false);
		        db9.shootForward(); 
		    }
		    else if (keyCode == KeyEvent.VK_C)
		    {
		    	playShots.play("pfire", false);
		    	db9.shootBackward();
		    }

		}
	}  // end of processKey()


	public void showExplosion(int x, int y)
  	// called by any car
  	{
      	if (!showExplosion) 
      	{  // only allow a single explosion at a time
	     	showExplosion = true;
	      	xExpl = x - explWidth/2;   //\ (x,y) is the center of the explosion
	      	yExpl = y - explHeight/2;
	
	      	/* Play an explosion clip, but cycle through them.
	         This adds variety, and gets round not being able to 
	         play multiple instances of a clip at the same time. */
	      	clipsLoader.play( exploNames[numHits%exploNames.length], false);
	      	numHits++;
		}
      	
      	/*
      	int i = 0;
      	
      	while( explosionPlayers[i].xplod() )
      	//max ten explosions
      	{
      		i++;
      		if(i >= explosionPlayers.length )
      		{
      			return;
      		}
      	}
      	explosionPlayers[i].setXplod(true);
      	
      	xExpl = x - explWidth/2;   //\ (x,y) is the center of the explosion
      	yExpl = y - explHeight/2;
      	
      	explosionPlayers[i].xplosLoc(xExpl, yExpl);
      	/* Play an explosion clip, but cycle through them.
         This adds variety, and gets round not being able to 
         play multiple instances of a clip at the same time. */
      	//clipsLoader.play( exploNames[numHits%exploNames.length], false);*/
      	
  	} // end of showExplosion()

	public void sequenceEnded(String imageName)
	// called by ImagesPlayer when the explosion animation finishes
	{  
  		showExplosion = false;
     	explosionPlayer.restartAt(0);   // reset animation for next time

  		/*// reset animation for next time
  		for(int i=0; i<explosionPlayers.length; i++)
		{
			if( explosionPlayers[i].xplod() )
			{
				explosionPlayers[i].restartAt(0);
			}
		}*/
	}

  	public void addNotify()
  	// wait for the JPanel to be added to the JFrame before starting
  	{ 
  		super.addNotify();   // creates the peer
    	startGame();         // start the thread
  	}

  	private void startGame()
  	// initialise and start the thread 
  	{ 
    	if (animator == null || !running) 
    	{
	      	animator = new Thread(this);
		  	animator.start();
    	}
  	} // end of startGame()
    

  	// ------------- game life cycle methods ------------
  	// called by the JFrame's window listener methods


	public void resumeGame()
	// called when the JFrame is activated / deiconified
	{ 
  		if (!showHelp)    // CHANGED
			isPaused = false;  
  	} 


  	public void pauseGame()
  	// called when the JFrame is deactivated / iconified
  	{ 
  		isPaused = true;  
  	} 


  	public void stopGame() 
  	// called when the JFrame is closing
  	{  
  		running = false;  
  	}

  // ----------------------------------------------

  	public void run()
	/* The frames of the animation are drawn inside the while loop. */
	{
	    long beforeTime, afterTime, timeDiff, sleepTime;
	    long overSleepTime = 0L;
	    int noDelays = 0;
	    long excess = 0L;
	
	    gameStartTime = System.nanoTime();
	    beforeTime = gameStartTime;
	
		running = true;
	
		while(running) 
		{
			gameUpdate();
			gameRender();
			paintScreen();
		
			afterTime = System.nanoTime();
			timeDiff = afterTime - beforeTime;
			sleepTime = (period - timeDiff) - overSleepTime;  
		
			if (sleepTime > 0) 
			{   // some time left in this cycle
				try 
				{
					Thread.sleep(sleepTime/1000000L);  // nano -> ms
				}
				catch(InterruptedException ex){}
				overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
			}
			else 
			{    // sleepTime <= 0; the frame took longer than the period
				excess -= sleepTime;  // store excess time value
				overSleepTime = 0L;
			
				if (++noDelays >= NO_DELAYS_PER_YIELD) 
				{
					Thread.yield();   // give another thread a chance to run
					noDelays = 0;
				}
			} 
		
			beforeTime = System.nanoTime();
		
			/* If frame animation is taking too long, update the game state 
			without rendering it, to get the updates/sec nearer to
			the required FPS. */
			int skips = 0;
			while((excess > period) && (skips < MAX_FRAME_SKIPS)) 
			{
				excess -= period;
				gameUpdate();    // update state but don't render
				skips++;
			}
		}
		System.exit(0);   // so window disappears
  	} // end of run()


	private void gameUpdate() 
	{
		if(db9.isDead())
		{
			gameOver = true;
			score = db9.getHits();
			//stopGame();
		}
		
		if(!isPaused && !gameOver)
		{
			ribsMan.update();   // update background and sprites
			enemyMan.update();
			setLevel(enemyMan.getLevel());
			db9.updateSprite();
		}
		
		
      	if (showExplosion)
        	explosionPlayer.updateTick(); // update the animation
		
		/*for(int i=0; i<explosionPlayers.length; i++)
		{
			ImagesPlayer temp = explosionPlayers[i];
				
			if(temp != null && temp.xplod())
			{
				temp.updateTick();
			}
		}*/
	}  // end of gameUpdate()


  	private void gameRender()
  	{
		if (dbImage == null)
		{
			dbImage = createImage(PWIDTH, PHEIGHT);
			if (dbImage == null) 
			{
				System.out.println("dbImage is null");
				return;
			}
			else
			dbg = dbImage.getGraphics();
		}
	
		// draw a white background
		dbg.setColor(Color.white);
		dbg.fillRect(0, 0, PWIDTH, PHEIGHT);
		
		// draw the game elements: order is important
		ribsMan.display(dbg);       // the background ribbons
		//bricksMan.display(dbg);     // the bricks
		db9.drawSprite(dbg);       // the sprites
		enemyMan.display(dbg);
		
		if (showExplosion)      // draw the explosion (in front of jack)
      		dbg.drawImage(explosionPlayer.getCurrentImage(), xExpl, yExpl, null);
      		
		reportStats(dbg);
		
		/*for(int i=0; i<explosionPlayers.length; i++)
		{
			ImagesPlayer temp = explosionPlayers[i];
			
			if(temp != null && temp.xplod())
			{
				dbg.drawImage(temp.getCurrentImage(), temp.getX(), temp.getY(), null);
			}
		}*/
			
		if(gameOver)
		{
			dbg.drawImage(gameOverIm, (PWIDTH-helpIm.getWidth())/2, 
		                          (PHEIGHT-helpIm.getHeight())/2, null);
		    gameOverMessage(dbg); 
		}

		if (showHelp)    // draw the help at the very front (if switched on)
		    dbg.drawImage(helpIm, (PWIDTH-helpIm.getWidth())/2, 
		                          (PHEIGHT-helpIm.getHeight())/2, null);
  	}  // end of gameRender()

  	private void reportStats(Graphics g)
  	// Report the number of hits, and time spent playing
  	{
		if(!isPaused)
		{
			int redComp;
			int greenComp;
			
		    if (!gameOver)    // stop incrementing the timer once the game is over
		      	timeSpentInGame = (int) ((System.nanoTime() - gameStartTime)/1000000000L);  // ns --> secs
			
			//draw health meter outline
			g.setColor(Color.gray);
			g.fillRect(13,13, 254, 14);
		
			//color ramges from green to red
			//g.setColor(0, 255 , 0);
			//g.setColor(255, 0 , 0);
			numHits = db9.getHits();
			int health;
			
			//if game is over, display must show no health
			if(gameOver)
				health = 0;
			else
				health = db9.getHealth();
			int healthInRange = (int)(health * 5.1);
		
			redComp = 255 - healthInRange;
			greenComp = 0 + healthInRange;
		
			Color rectColor = new Color(redComp, greenComp, 0);
			g.setColor(rectColor);
		
			g.fillRect(15,15, health * 5, 10);
			
			//report stats in word form
			g.setColor(Color.red);
		    g.setFont(msgsFont);
			g.drawString("Killed: " + enemyMan.getKillCount(), 15, 55);
			g.drawString("Time: " + timeSpentInGame + " secs", 15, 80); 
			g.setColor(Color.black);
		}
  	}  // end of reportStats()


  	private void gameOverMessage(Graphics g)
  	// Center the game-over message in the panel.
  	{
	    //msgsFont = new Font("Verdana", Font.PLAIN, 30);
	    
	    String msg = "Your score: " + score;
	
		int x = (PWIDTH - metrics.stringWidth(msg))/2; 
		int y = (PHEIGHT - metrics.getHeight())/2;
		g.setColor(Color.red);
	    g.setFont(msgsFont);
		g.drawString(msg, x, y);
  	}  // end of gameOverMessage()


  	private void paintScreen()
  	// use active rendering to put the buffered image on-screen
  	{ 
	    Graphics g;
	    try 
	    {
			g = this.getGraphics();
			if ((g != null) && (dbImage != null))
			g.drawImage(dbImage, 0, 0, null);
			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
	    }
	    catch (Exception e)
	    { 
	    	System.out.println("Graphics context error: " + e);  
	    }
	} // end of paintScreen()
  
    public EnemyCarManager getECM()
    //returns ref of ECM
	{
  		return enemyMan;
	}
  
  	public PlayerCar getPC()
  	//returns ref of player car
  	{
  		return db9;
  	}
  	
  	public ClipsLoader getClips()
  	//returns ref of non gunshot sounds
  	{
  		return clipsLoader;
  	}
  	
  	public ClipsLoader getShots()
  	//returns the sounds ref for non player shots
  	{
  		return shotsOnly;
  	}
  	
  	public ClipsLoader getPShots()
  	//returns the sounds ref for player shots
  	{
  		return playShots;
  	}
  	
  	public int getLevel()
  	//returns the current level
  	{
  		return level;
  	}
  	
  	public int setLevel(int newLevel)
  	//sets the level to the next level
  	{
  		level = newLevel;
  		return level;
  	}
}  // end of JackPanel class

