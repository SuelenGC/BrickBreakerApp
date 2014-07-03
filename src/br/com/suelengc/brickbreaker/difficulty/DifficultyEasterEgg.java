package br.com.suelengc.brickbreaker.difficulty;

class DifficultyEasterEgg extends Difficulty {

	@Override
	public void setValues() {
		ballSize = 1.0f;
        paddleSize = 7.0f;
        scoreMultiplier = 0.1f;
        maxLives = 1;
        minSpeed = 500;
        maxSpeed = 1500;		
	}

}
