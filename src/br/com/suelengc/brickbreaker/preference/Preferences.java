package br.com.suelengc.brickbreaker.preference;

import android.content.Context;
import br.com.suelengc.brickbreaker.difficulty.Difficulty;
import br.com.suelengc.brickbreaker.view.BrickBreakerActivity;

public class Preferences {

    public static final String LOGGED_USER = "LoggedUser";
    public static final String HIGH_SCORE_KEY = "high-score";
    private static final String DIFFICULTY_KEY = "difficulty";
    private static final String SOUND_EFFECTS_ENABLED_KEY = "sound-effects-enabled";
    public static final String EASTER_EGG_UNLOCKED_KEY = "easter-egg-unlocked";
    private SharedPreferencesManager manager;

    public Preferences(Context context) {
        manager = new SharedPreferencesManager(context);
    }

    public void setHighScore(int score) {
        manager.setInt(HIGH_SCORE_KEY, score);
    }

    public int getHighScore() {
        return manager.getInt(HIGH_SCORE_KEY);
    }

    public void setDifficulty(int difficulty) {
    	if (difficulty < Difficulty.DIFFICULTY_MIN || difficulty > Difficulty.DIFFICULTY_MAX) {
    		difficulty = Difficulty.DIFFICULTY_DEFAULT;
        }
        manager.setInt(DIFFICULTY_KEY, difficulty);
    }

    public int getDifficulty() {
        return manager.getInt(DIFFICULTY_KEY);
    }

    public void setSoundEffects(boolean enable) {
        manager.setBoolean(SOUND_EFFECTS_ENABLED_KEY, enable);
    }

    public boolean getSoundEffects() {
        return manager.getBoolean(SOUND_EFFECTS_ENABLED_KEY);
    }
    
    public void setLoggedUser(boolean logged) {
        manager.setBoolean(LOGGED_USER, logged);
    }

    public boolean getLoggedUser() {
        return manager.getBoolean(LOGGED_USER);
    }

    public void setEasterEggUnlocked(boolean unlocked) {
        manager.setBoolean(EASTER_EGG_UNLOCKED_KEY, unlocked);
    }

    public boolean isEasterEggUnlocked() {
        return manager.getBoolean(EASTER_EGG_UNLOCKED_KEY);
    }
    
    public void savePreferences() {
        setDifficulty(BrickBreakerActivity.getDifficultyIndex());
        setSoundEffects(BrickBreakerActivity.getSoundEffectsEnabled());
    }
    
    public void restorePreferences() {
        BrickBreakerActivity.setDifficultyIndex(getDifficulty());
        BrickBreakerActivity.setSoundEffectsEnabled(getSoundEffects());
    }
    
    public void updateHighScore(int lastScore) {
    	int highScore = getHighScore();
    	
        if (lastScore > highScore) {
            setHighScore(lastScore);
        }
    }
}
