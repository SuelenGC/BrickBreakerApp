package br.com.suelengc.brickbreaker.difficulty;

class DifficultyEasy extends Difficulty {

	@Override
	public void setValues() {
		ballSize = 2.0f;
        paddleSize = 2.0f;
        scoreMultiplier = 0.75f;
        maxLives = 4;
        minSpeed = 300;
        maxSpeed = 500;
	}

}
