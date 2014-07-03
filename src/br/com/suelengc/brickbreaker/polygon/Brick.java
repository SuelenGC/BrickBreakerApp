package br.com.suelengc.brickbreaker.polygon;

import br.com.suelengc.brickbreaker.BasicAlignedRect;

public class Brick extends BasicAlignedRect {
    private boolean mAlive = false;
    private int mPoints = 0;
    private boolean isLiveBrick;

    public boolean isAlive() {
        return mAlive;
    }

    public void setAlive(boolean alive) {
        mAlive = alive;
    }

    public int getScoreValue() {
        return mPoints;
    }

    public void setScoreValue(int points) {
        mPoints = points;
    }

	public boolean isLiveBrick() {
		return this.isLiveBrick;
	}
	
	public void setLiveBrick(boolean isLiveBrick) {
		this.isLiveBrick = isLiveBrick;
		
	}
}
