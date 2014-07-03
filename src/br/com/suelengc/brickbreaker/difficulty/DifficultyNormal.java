package br.com.suelengc.brickbreaker.difficulty;

class DifficultyNormal extends Difficulty {

	@Override
	public void setValues() {
		ballSize = 1;
        paddleSize = 1.0f;
        scoreMultiplier = 1.0f;
        maxLives = 3;
        minSpeed = 300;
        maxSpeed = 800;
	}

}
