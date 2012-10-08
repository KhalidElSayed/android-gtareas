package com.stefanini.gestiontareas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.stefanini.util.AppConstants;

public class EditarTareaActivity extends SherlockFragmentActivity {

	private boolean isToEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(HomeActivity.THEME);
		super.onCreate(savedInstanceState);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_action_bar));
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		actionBar.setDisplayShowTitleEnabled(true);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			actionBar.setTitle(extras.getString(AppConstants.KEY_NAME_TAREA));
			isToEdit = extras.getBoolean(AppConstants.KEY_EDIT, false);
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

	@Override
	public void onBackPressed() {
		if (isToEdit) {
			mostrarAlertDialog();
		} else {
			super.onBackPressed();
		}
	}

	public void mostrarAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Esta seguro de salir ?, los cambios aun no han sido guardados.")
				.setCancelable(false)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						EditarTareaActivity.this.finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
