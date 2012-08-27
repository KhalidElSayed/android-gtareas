package com.stefanini.gestiontareas;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.stefanini.util.AppConstants;

public class EditarTareaActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setTheme(HomeActivity.THEME);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_action_bar));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		actionBar.setDisplayShowTitleEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			actionBar.setTitle(extras.getString(AppConstants.KEY_NAME_TAREA));
		}

		if (savedInstanceState == null) {
			EditarTareaFragment editarTareaFragment = new EditarTareaFragment();
			editarTareaFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, editarTareaFragment).commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// Intent intent = new Intent();
			// intent.setClass(getApplication(), BEntradaActivity.class);
			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
			// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// startActivity(intent);
			finish();
			break;

		default:
			break;
		}
		return true;
	}
}
