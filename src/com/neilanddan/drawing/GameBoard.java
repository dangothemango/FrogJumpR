package com.neilanddan.drawing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.neilanddan.app.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class GameBoard extends View{
	
	private Paint p;
	private List<Point> starField = null;
	private int starAlpha = 80;
	private int starFade = 2;
//	private Rect asteroidBounds = new Rect(0,0,0,0);
	private Rect frogBounds = new Rect(0,0,0,0);
//	private Point asteroid;
	private Point frog;
	private Bitmap bm1 = null;
	private Matrix m = null;
	private Bitmap bm2 = null;
	//Collision flag and point
	private boolean collisionDetected = false;
	private Point lastCollision = new Point(-1,-1);
	

	
	private static final int NUM_OF_STARS = 25;
	
	//Allow our controller to get and set the sprite positions
	//sprite 1 setter
//	synchronized public void setSprite1(int x, int y) {
//		asteroid=new Point(x,y);
//	}
//	
//	//sprite 1 getter
//	synchronized public int getSprite1X() {
//		return asteroid.x;
//	}
//	
//	synchronized public int getSprite1Y() {
//		return asteroid.y;
//	}
	
	//sprite 2 setter
	synchronized public void setfrog(int x, int y) {
		frog=new Point(x,y);
	}
	synchronized public Asteroid createAsteroid(int x,int y,Point speed) {
		return new Asteroid(x,y,speed);
	}
	public Asteroid asteroid = createAsteroid(-1,-1, new Point (-10,-10));
	//sprite 2 getter
	synchronized public int getfrogX() {
		return frog.x;
	}
	
	synchronized public int getfrogY() {
		return frog.y;
	}
	
	synchronized public void resetStarField() {
		starField = null;
	}
	
//	//expose sprite bounds to controller
//	synchronized public int getSprite1Width() {
//		return asteroidBounds.width();
//	}
//	
//	synchronized public int getSprite1Height() {
//		return asteroidBounds.height();
//	}
//	
	synchronized public int getfrogWidth() {
		return frogBounds.width();
	}
	
	synchronized public int getfrogHeight() {
		return frogBounds.height();
	}
	
	//return the point of the last collision
	synchronized public Point getLastCollision() {
		return lastCollision;
	}
	
	//return the collision flag
	synchronized public boolean wasCollisionDetected() {
		return collisionDetected;
	}
	
	public GameBoard(Context context, AttributeSet aSet) {
		super(context, aSet);
		p = new Paint();
		//load our bitmaps and set the bounds for the controller
		frog = new Point(-1,-1);
		//Define a matrix so we can rotate the asteroid
		m = new Matrix(); 
		p = new Paint();
		bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
		bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.ufo);
		asteroid.setSpriteBounds(new Rect(0,0, bm1.getWidth(), bm1.getHeight()));
		frogBounds = new Rect(0,0, bm2.getWidth(), bm2.getHeight());
	}
	
	synchronized private void initializeStars(int maxX, int maxY) {
		starField = new ArrayList<Point>();
		for (int i=0; i<NUM_OF_STARS; i++) {
			 Random r = new Random();
			 int x = r.nextInt(maxX-5+1)+5;
			 int y = r.nextInt(maxY-5+1)+5;
			 starField.add(new Point (x,y)); 
		}
		collisionDetected = false;
	}
	
	private boolean checkForCollision() {
		if (asteroid.getX()<0 && frog.x<0 && asteroid.getY()<0 && frog.y<0) return false;
		Rect r1 = new Rect(asteroid.getX(), asteroid.getY(), asteroid.getX() + asteroid.getWidth(),  asteroid.getY() + asteroid.getHeight());
		Rect r2 = new Rect(frog.x, frog.y, frog.x + frogBounds.width(), frog.y + frogBounds.height());
		Rect r3 = new Rect(r1);
		if(r1.intersect(r2)) {
			for (int i = r1.left; i<r1.right; i++) {
				for (int j = r1.top; j<r1.bottom; j++) {
					if (bm1.getPixel(i-r3.left, j-r3.top)!= Color.TRANSPARENT) {
						if (bm2.getPixel(i-r2.left, j-r2.top) != Color.TRANSPARENT) {
							lastCollision = new Point(frog.x + i-r2.left, frog.y + j-r2.top);
							return true;
						}
					}
				}
			}
		}
		lastCollision = new Point(-1,-1);
		return false;
	}
	
	@Override
	synchronized public void onDraw(Canvas canvas) {
		
		p.setColor(Color.BLUE);
		p.setAlpha(255);
	    p.setStrokeWidth(1);
		canvas.drawRect(0, 0, getWidth(), getHeight(), p);
		
		if (starField==null) {
			initializeStars(canvas.getWidth(), canvas.getHeight());
		}
		
		p.setColor(Color.GREEN);
		p.setAlpha(starAlpha+=starFade);
		if (starAlpha>=252 || starAlpha <=80) starFade=starFade*-1;
		p.setStrokeWidth(5);
		for (int i=0; i<NUM_OF_STARS; i++) {
			canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
		}
		
		if (asteroid.getX()>=0) {
			m.reset();
			m.postTranslate((float)(asteroid.getX()), (float)(asteroid.getY()));
			m.postRotate(asteroid.getRot(), (float)(asteroid.getX()+asteroid.getWidth()/2.0), (float)(asteroid.getY()+asteroid.getWidth()/2.0));
			canvas.drawBitmap(bm1, m, null);
			asteroid.setRot(asteroid.getRot()+5);
			if (asteroid.getRot() >= 360) asteroid.setRot(0);
		}
		if (frog.x>=0) {
			canvas.drawBitmap(bm2, frog.x, frog.y, null);
		}
		collisionDetected = checkForCollision();
		if (collisionDetected ) {
			 p.setColor(Color.RED);
			 p.setAlpha(255);
		     p.setStrokeWidth(5);
		     canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5, lastCollision.x + 5, lastCollision.y + 5, p);
		     canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5, lastCollision.x - 5, lastCollision.y + 5, p);
		}
	}
}
