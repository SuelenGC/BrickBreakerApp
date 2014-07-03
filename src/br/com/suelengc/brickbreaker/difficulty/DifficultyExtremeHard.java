package br.com.suelengc.brickbreaker.difficulty;

class DifficultyExtremeHard extends Difficulty {

	@Override
	public void setValues() {
		ballSize = 1.0f;
        paddleSize = 0.5f;
        scoreMultiplier = 1.75f;
        maxLives = 1;
        minSpeed = 1000;
        maxSpeed = 100000;		
	}

}
