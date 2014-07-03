package br.com.suelengc.brickbreaker.view;

import android.app.Activity;
import android.os.Bundle;
import br.com.suelengc.brickbreaker.BrickBreakerState;
import br.com.suelengc.brickbreaker.difficulty.Difficulty;
import br.com.suelengc.brickbreaker.difficulty.DifficultyFactory;
import br.com.suelengc.brickbreaker.preference.Preferences;
import br.com.suelengc.brickbreaker.resource.SoundResources;
import br.com.suelengc.brickbreaker.resource.TextureResources;

public class BrickBreakerActivity extends Activity {
    private static int sDifficultyIndex = 1;
        
    private static boolean sSoundEffectsEnabled;
    private static boolean sVibrationMode;

    private BrickBreakerSurfaceView mGLView;
    private BrickBreakerState mBrickBreakerState;
    
    private Preferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SoundResources.initialize(this);
        TextureResources.Configuration textConfig = TextureResources.configure(this);
        
        preferences = new Preferences(getApplicationContext());
        
        mBrickBreakerState = new BrickBreakerState();
        configureBrickBreakerState();
        
        mGLView = new BrickBreakerSurfaceView(this, mBrickBreakerState, textConfig);
        setContentView(mGLView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
        preferences.updateHighScore(BrickBreakerState.getFinalScore());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    private void configureBrickBreakerState() {
        Difficulty difficulty = new DifficultyFactory().getDifficult(preferences.getDifficulty());
        difficulty.configure(mBrickBreakerState);
        
        SoundResources.setSoundEffectsEnabled(sSoundEffectsEnabled);
    }

    public static int getDifficultyIndex() {
        return sDifficultyIndex;
    }

    public static int getDefaultDifficultyIndex() {
        return Difficulty.DIFFICULTY_DEFAULT;
    }

    public static void setDifficultyIndex(int difficultyIndex) {
        if (sDifficultyIndex != difficultyIndex) {
            sDifficultyIndex = difficultyIndex;
            invalidateSavedGame();
        }
    }

    public static boolean getSoundEffectsEnabled() {
        return sSoundEffectsEnabled;
    }

    public static void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        sSoundEffectsEnabled = soundEffectsEnabled;
    }

    public static void invalidateSavedGame() {
        BrickBreakerState.invalidateSavedGame();
    }

    public static boolean canResumeFromSave() {
        return BrickBreakerState.canResumeFromSave();
    }

}
