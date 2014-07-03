package br.com.suelengc.brickbreaker.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import br.com.suelengc.brickbreaker.preference.Preferences;
import br.com.suelengc.brickbreakerapp.R;

public class MainFragment extends Fragment {
	
	private MainActivity activity;
	private TextView highScore;
	private Preferences preferences;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		setUpButtons(rootView);

		highScore = (TextView) rootView.findViewById(R.id.high_score);
		
		preferences = activity.getPreferences();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		highScore.setText("High Score: " + String.valueOf(preferences.getHighScore()));
	}
	
	private void setUpButtons(final View view) {
		Button btPlay = (Button) view.findViewById(R.id.btPlay);
		btPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.onClickPlay(view);
			}
		});

		Button btLevels = (Button) view.findViewById(R.id.btLevels);
		btLevels.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.onClickLevels(view);
			}
		});

	}
}