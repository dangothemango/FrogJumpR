package com.neilanddan.app;

import java.util.Random;

import com.neilanddan.app.R;
import com.neilanddan.drawing.GameBoard;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.graphics.Point;

public class Main extends Activity implements OnClickListener{

	private Handler frame = new Handler();
//	private Point asteroidVelocity;
	private Point frogVelocity;
//	private Point sprite3Velocity; //a new rock!
	private int asteroidMaxX;
	private int asteroidMaxY;
	private int frogMaxX;
	private int frogMaxY;
//	private int sprite3MaxX;
//	private int sprite3MaxY;
	//acceleration flag
	private boolean rising = false;
	private static final int FRAME_RATE = 20; //50 frames per second
	
	//Method for getting touch state--requires android 2.1 or greater
	@Override
	synchronized public boolean onTouchEvent(MotionEvent ev) {
       final int action = ev.getAction();
       switch (action & MotionEvent.ACTION_MASK) {
	      case MotionEvent.ACTION_DOWN:
	      case MotionEvent.ACTION_POINTER_DOWN:
	   	   	rising = true;
    	   	break;
    	  case MotionEvent.ACTION_UP: 
	      case MotionEvent.ACTION_POINTER_UP:
	   		rising=false;
     		break;
     	}
	    return true;
	}
	
	//Increase the velocity towards five or decrease
	//back to one depending on state
	private void updateVelocity() {
		int xDir = (frogVelocity.x > 0) ? 1 : -1;
		int yDir = 1;
		int speed = 5;
		if (rising) {
			yDir = -1;
		} else {
			yDir = 1;
		}
		if (speed>5) speed =5;
		if (speed<1) speed =1;
		frogVelocity.x=speed*xDir;	
		frogVelocity.y=speed*yDir;
	}

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Handler h = new Handler();
        ((Button)findViewById(R.id.the_button)).setOnClickListener(this);
        h.postDelayed(new Runnable() {
			@Override
			public void run() {
				initGfx();
			}
        }, 1000);
    }
	
	private Point getRandomVelocity() {
		Random r = new Random();
		int min = 1;
		int max = 5;
		int x = r.nextInt(max-min+1)+min;
		int y = r.nextInt(max-min+1)+min;
		return new Point (-x,-y); 
	}

	private Point getRandomPoint() {
		Random r = new Random();
	    int minX = 0;
	    int maxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard)findViewById(R.id.the_canvas)).asteroid.getWidth();
		int x = 0;
	    int minY = 0;
 	    int maxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard)findViewById(R.id.the_canvas)).asteroid.getHeight();
 	    int y = 0;
  	   	x = r.nextInt(maxX-minX+1)+minX;
		y = r.nextInt(maxY-minY+1)+minY;
		return new Point (x,y);
	}
    
    synchronized public void initGfx() {
    	((GameBoard)findViewById(R.id.the_canvas)).resetStarField();
    	Point p1, p2;
    	do {
    		p1 = getRandomPoint();
    		p2 = getRandomPoint();
    	} while (Math.abs(p1.x - p2.x) < ((GameBoard)findViewById(R.id.the_canvas)).asteroid.getWidth());
    	((GameBoard)findViewById(R.id.the_canvas)).asteroid.setSprite(p1.x, p1.y);
    	((GameBoard)findViewById(R.id.the_canvas)).setfrog(p2.x, p2.y);
    	((GameBoard)findViewById(R.id.the_canvas)).asteroid.setVelocity(getRandomVelocity());
    	frogVelocity = new Point(1,1);
    	asteroidMaxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard)findViewById(R.id.the_canvas)).asteroid.getWidth();
    	asteroidMaxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard)findViewById(R.id.the_canvas)).asteroid.getHeight();
    	frogMaxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard)findViewById(R.id.the_canvas)).getfrogWidth();
    	frogMaxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard)findViewById(R.id.the_canvas)).getfrogHeight();
    	((Button)findViewById(R.id.the_button)).setEnabled(true);
    	frame.removeCallbacks(frameUpdate);
    	((GameBoard)findViewById(R.id.the_canvas)).invalidate();
    	frame.postDelayed(frameUpdate, FRAME_RATE);
    }

   @Override
   synchronized public void onClick(View v) {
	   initGfx();
   }
   
  private Runnable frameUpdate = new Runnable() {

	@Override
	synchronized public void run() {
		if (((GameBoard)findViewById(R.id.the_canvas)).wasCollisionDetected()) {
    		Point collisionPoint = ((GameBoard)findViewById(R.id.the_canvas)).getLastCollision();
    		if (collisionPoint.x>=0) {
    			((TextView)findViewById(R.id.the_other_label)).setText("Last Collision XY ("+Integer.toString(collisionPoint.x)+","+Integer.toString(collisionPoint.y)+")");
    		}
    		return;
    	}
		frame.removeCallbacks(frameUpdate);
		//Add our call to increase or decrease velocity
		updateVelocity();
		//Display UFO speed
		((TextView)findViewById(R.id.the_label)).setText("Sprite Acceleration ("+Integer.toString(frogVelocity.x)+","+Integer.toString(frogVelocity.y)+")");
		Point asteroid = new Point (((GameBoard)findViewById(R.id.the_canvas)).asteroid.getX(),
				((GameBoard)findViewById(R.id.the_canvas)).asteroid.getY()) ;
		Point frog = new Point (((GameBoard)findViewById(R.id.the_canvas)).getfrogX(),
				((GameBoard)findViewById(R.id.the_canvas)).getfrogY());
		asteroid.x = asteroid.x + ((GameBoard)findViewById(R.id.the_canvas)).asteroid.getVelocity().x;
//		if (asteroid.x > asteroidMaxX || asteroid.x < 5) {
//			asteroidVelocity.x *= -1;
//		}
//		asteroid.y = asteroid.y + 0;
//		if (asteroid.y > asteroidMaxY || asteroid.y < 5) {
//			asteroidVelocity.y *= -1;
//		}
		frog.x = 10;
		if (frog.x > frogMaxX || frog.x < 5) {
			frogVelocity.x *= -1;
		}
		frog.y = frog.y + frogVelocity.y;
		if (frog.y > frogMaxY || frog.y < 5) {
			frogVelocity.y *= 1;
			frog.y -= frogVelocity.y;
		}
		((GameBoard)findViewById(R.id.the_canvas)).asteroid.setSprite(asteroid.x, asteroid.y);
	    ((GameBoard)findViewById(R.id.the_canvas)).setfrog(frog.x, frog.y);
		((GameBoard)findViewById(R.id.the_canvas)).invalidate();
		frame.postDelayed(frameUpdate, FRAME_RATE);
	}
	   
   };
}
