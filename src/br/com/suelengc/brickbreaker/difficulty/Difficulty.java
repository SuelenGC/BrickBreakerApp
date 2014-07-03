package br.com.suelengc.brickbreaker.difficulty;

import br.com.suelengc.brickbreaker.BrickBreakerState;

public abstract class Difficulty {
	protected int maxLives, minSpeed, maxSpeed;
    protected float ballSize, paddleSize, scoreMultiplier;
    
    public static final int DIFFICULTY_MIN = 0;
    public static final int DIFFICULTY_MAX = 4;        // inclusive
    public static final int DIFFICULTY_DEFAULT = 1;
    
	public void configure(BrickBreakerState mBrickBreakerState) {
		setValues();

		mBrickBreakerState.setBallSizeMultiplier(ballSize);
		mBrickBreakerState.setPaddleSizeMultiplier(paddleSize);
		mBrickBreakerState.setScoreMultiplier(scoreMultiplier);
		mBrickBreakerState.setMaxLives(maxLives);
		mBrickBreakerState.setBallInitialSpeed(minSpeed);
		mBrickBreakerState.setBallMaximumSpeed(maxSpeed);
	}

	protected abstract void setValues();
}
