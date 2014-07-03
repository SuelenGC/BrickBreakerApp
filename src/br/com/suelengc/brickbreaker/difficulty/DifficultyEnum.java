package br.com.suelengc.brickbreaker.difficulty;

public enum DifficultyEnum {
	EASY("Easy"), NORMAL("Normal"), HARD("Hard"), EXTREME_HARD("Extreme Hard"), EASTER_EGG("Easter Egg");
	
	private String description = "";
	
	private DifficultyEnum(String description) {
		this.description = description;
	}
	
	public String toString() {
		return this.description;		
	};
}
