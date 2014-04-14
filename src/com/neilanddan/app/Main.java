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
	private Point sprite1Velocity;
	private Point sprite2Velocity;
	private int sprite1MaxX;
	private int sprite1MaxY;
	private int sprite2MaxX;
	private int sprite2MaxY;
	//acceleration flag
	private boolean isAccelerating = false;
	private static final int FRAME_RATE = 20; //50 frames per second
	
	//Method for getting touch state--requires android 2.1 or greater
	@Override
	synchronized public boolean onTouchEvent(MotionEvent ev) {
       final int action = ev.getAction();
       switch (action & MotionEvent.ACTION_MASK) {
	      case MotionEvent.ACTION_DOWN:
	      case MotionEvent.ACTION_POINTER_DOWN:
	   	   	isAccelerating = true;
    	   	break;
    	  case MotionEvent.ACTION_UP: 
	      case MotionEvent.ACTION_POINTER_UP:
	   		isAccelerating = false;
     		break;
     	}
	    return true;
	}
	
	//Increase the velocity towards five or decrease
	//back to one depending on state
	private void updateVelocity() {
		int xDir = (sprite2Velocity.x > 0) ? 1 : -1;
		int yDir = (sprite2Velocity.y > 0) ? 1 : -1;
		int speed = 0;
		if (isAccelerating) {
			speed = Math.abs(sprite2Velocity.x)+1;
		} else {
			speed = Math.abs(sprite2Velocity.x)-1;
		}
		if (speed>5) speed =5;
		if (speed<1) speed =1;
		sprite2Velocity.x=speed*xDir;	
		sprite2Velocity.y=speed*yDir;
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
		return new Point (x,y); 
	}

	private Point getRandomPoint() {
		Random r = new Random();
	    int minX = 0;
	    int maxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Width();
		int x = 0;
	    int minY = 0;
 	    int maxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Height();
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
    	} while (Math.abs(p1.x - p2.x) < ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Width());
    	((GameBoard)findViewById(R.id.the_canvas)).setSprite1(p1.x, p1.y);
    	((GameBoard)findViewById(R.id.the_canvas)).setSprite2(p2.x, p2.y);
    	sprite1Velocity = getRandomVelocity();
    	sprite2Velocity = new Point(1,1);
    	sprite1MaxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Width();
    	sprite1MaxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Height();
    	sprite2MaxX = findViewById(R.id.the_canvas).getWidth() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Width();
    	sprite2MaxY = findViewById(R.id.the_canvas).getHeight() - ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Height();
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
		((TextView)findViewById(R.id.the_label)).setText("Sprite Acceleration ("+Integer.toString(sprite2Velocity.x)+","+Integer.toString(sprite2Velocity.y)+")");
		Point sprite1 = new Point (((GameBoard)findViewById(R.id.the_canvas)).getSprite1X(),
				((GameBoard)findViewById(R.id.the_canvas)).getSprite1Y()) ;
		Point sprite2 = new Point (((GameBoard)findViewById(R.id.the_canvas)).getSprite2X(),
				((GameBoard)findViewById(R.id.the_canvas)).getSprite2Y());
		sprite1.x = sprite1.x + sprite1Velocity.x;
		if (sprite1.x > sprite1MaxX || sprite1.x < 5) {
			sprite1Velocity.x *= -1;
		}
		sprite1.y = sprite1.y + sprite1Velocity.y;
		if (sprite1.y > sprite1MaxY || sprite1.y < 5) {
			sprite1Velocity.y *= -1;
		}
		sprite2.x = sprite2.x + sprite2Velocity.x;
		if (sprite2.x > sprite2MaxX || sprite2.x < 5) {
			sprite2Velocity.x *= -1;
		}
		sprite2.y = sprite2.y + sprite2Velocity.y;
		if (sprite2.y > sprite2MaxY || sprite2.y < 5) {
			sprite2Velocity.y *= -1;
		}
		((GameBoard)findViewById(R.id.the_canvas)).setSprite1(sprite1.x, sprite1.y);
	    ((GameBoard)findViewById(R.id.the_canvas)).setSprite2(sprite2.x, sprite2.y);
		((GameBoard)findViewById(R.id.the_canvas)).invalidate();
		frame.postDelayed(frameUpdate, FRAME_RATE);
	}
	   
   };
}
