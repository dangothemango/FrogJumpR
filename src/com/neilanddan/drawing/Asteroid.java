package com.neilanddan.drawing;

import android.graphics.Point;
import android.graphics.Rect;

public class Asteroid {
	
	private Point sprite;
	private Rect spriteBounds = new Rect(0,0,0,0);
	private int spriteRotation = 0;
	private Point spriteVelocity;
	
	public Asteroid(int x, int y) {
		sprite=new Point(x,y);
	}
	synchronized public void setSprite(int x, int y) {
		sprite=new Point(x,y);
	}
	synchronized public Point getVelocity() {
		return spriteVelocity;
	}
	synchronized public void setVelocity(Point x) {
		spriteVelocity = x;
	}	
	//sprite 1 getter
	synchronized public int getRot() {
		return spriteRotation;
	}
	synchronized public void setRot(int x) {
		spriteRotation= x;
	}
	synchronized public int getX() {
		return sprite.x;
	}
	
	synchronized public int getY() {
		return sprite.y;
	}
	//expose sprite bounds to controller
	synchronized public int getWidth() {
		return spriteBounds.width();
	}
	
	synchronized public int getHeight() {
		return spriteBounds.height();
	}
	
	synchronized public void setSpriteBounds(Rect r) {
		spriteBounds = r;
	}
	
}

