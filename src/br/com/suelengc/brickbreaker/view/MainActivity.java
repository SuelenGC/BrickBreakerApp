package br.com.suelengc.brickbreaker.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import br.com.suelengc.brickbreaker.preference.Preferences;
import br.com.suelengc.brickbreakerapp.R;

public class MainActivity extends Activity {
	private Fragment mFragment = null;
	private FragmentManager mFragmentManager;
	private Preferences preferences;
	
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_main);
        
        mFragment = (MainFragment) new MainFragment();
    	mFragmentManager = getFragmentManager();
    	
        mFragmentManager.beginTransaction()
                .replace(R.id.container, mFragment)
                .commit();

        preferences = new Preferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences.restorePreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferences.savePreferences();
    }
    
	public void onClickPlay(View control){
		BrickBreakerActivity.invalidateSavedGame();
		startGame();
	}
	
	public void onClickLevels(View control){
    	mFragment = (DifficultyFragment) new DifficultyFragment();
		
        mFragmentManager.beginTransaction()
                .replace(R.id.container, mFragment)
                .addToBackStack(null)
                .commit();
	}
		
    private void startGame() {
    	Intent intent = new Intent(this, BrickBreakerActivity.class);
        startActivity(intent);
    }
    
    public Preferences getPreferences() {
    	return this.preferences;
    }
    
	public void onClickBack(View control) {
		mFragmentManager.popBackStackImmediate();
	}
	
	public void onClickResetScore(View control) {
		
	}

	public void onClickChangeUsername(View control) {
		
	}
}