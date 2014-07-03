package br.com.suelengc.brickbreaker.difficulty;

public class DifficultyFactory {
	
	public Difficulty getDifficult(int level) {
		switch (level) {
		case 0:
			return new DifficultyEasy();
			
		case 1:
			return new DifficultyNormal();
			
		case 2:
			return new DifficultyHard();
			
		case 3:
			return new DifficultyExtremeHard();
			
		case 4:
			return new DifficultyEasterEgg();
		}
		return new DifficultyNormal();
	}
	
}
