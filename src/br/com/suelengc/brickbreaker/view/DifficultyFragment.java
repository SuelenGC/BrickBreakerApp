package br.com.suelengc.brickbreaker.view;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import br.com.suelengc.brickbreaker.difficulty.DifficultyEnum;
import br.com.suelengc.brickbreaker.preference.Preferences;
import br.com.suelengc.brickbreakerapp.R;

public class DifficultyFragment extends Fragment {
	private int nDifficulty;
	private List<String> levelList;
	private View mLevelsView;
	private ListView listViewLevel;

	private Preferences preferences;
	MainActivity activity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (MainActivity) activity;
	}
	
	public DifficultyFragment() {
//		nDifficulty = 4;
//		for(int i = 1; i <= nDifficulty; i++)
//			levelList.add("Difficulty " + i);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mLevelsView = inflater.inflate(R.layout.fragment_levels, container, false);
		listViewLevel = (ListView) mLevelsView.findViewById(R.id.gridview);

		preferences = activity.getPreferences();
		fillDifficultyList();
		
		return mLevelsView;
	}

	private void fillDifficultyList() {
		levelList = new ArrayList<String>();
		
		boolean unlockedEasterEgg = preferences.isEasterEggUnlocked();

		for (DifficultyEnum d : DifficultyEnum.values()) {
			if (d.equals(DifficultyEnum.EASTER_EGG) && !unlockedEasterEgg) {
				continue;
			} else {
				if (preferences.getDifficulty() == d.ordinal()) {
					levelList.add(d.toString() + " (selected)");
				} else {
					levelList.add(d.toString());	
				}
			}
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		final Preferences preferences = new Preferences(getActivity().getApplicationContext());
		
		ArrayAdapter<String> levelAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, levelList);
		listViewLevel.setAdapter(levelAdapter);
		
		listViewLevel.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					preferences.setDifficulty(position);
					BrickBreakerActivity.setDifficultyIndex(position);
					Toast.makeText(getActivity(), "Difficulty changed: " + (position+1), Toast.LENGTH_SHORT).show();
				}
		});
		
		listViewLevel.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
				Toast.makeText(getActivity(), "Congratulations, you found easter egg LEVEL!!", Toast.LENGTH_SHORT).show();
				preferences.setEasterEggUnlocked(true);
				return true;
			}
		});
	}
}