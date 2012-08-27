package com.stefanini.gestiontareas;

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class BEntradaActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTheme(HomeActivity.THEME);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_action_bar));
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		actionBar.setDisplayShowTitleEnabled(true);

		// tab pendientes
		Tab tab = actionBar
				.newTab()
				.setText(getString(R.string.title_panel_pendientes))
				.setTabListener(
						new TabListener<PendientesFragment>(this, "Pendientes",
								PendientesFragment.class));
		actionBar.addTab(tab);

		// tab realizadas
		tab = actionBar
				.newTab()
				.setText(getString(R.string.title_panel_realizadas))
				.setTabListener(
						new TabListener<RealizadasFragment>(this, "realizadas",
								RealizadasFragment.class));
		actionBar.addTab(tab);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent intent = new Intent(this, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
