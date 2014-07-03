package br.com.suelengc.brickbreaker.difficulty;

class DifficultyHard extends Difficulty {

	@Override
	public void setValues() {
		ballSize = 1.0f;
        paddleSize = 0.8f;
        scoreMultiplier = 1.25f;
        maxLives = 3;
        minSpeed = 600;
        maxSpeed = 1200;	
	}

}
