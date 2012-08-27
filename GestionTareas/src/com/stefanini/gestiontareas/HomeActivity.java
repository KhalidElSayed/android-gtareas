package com.stefanini.gestiontareas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class HomeActivity extends SherlockFragmentActivity implements
		OnClickListener {

	public static int THEME = R.style.Theme_Sherlock;

	private ImageButton mImgButGTareas;
	private ImageButton mImgButBEntrada;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(THEME);
		setContentView(R.layout.home_screen);

		GTareasApplication app = (GTareasApplication) getApplication();

		mImgButGTareas = (ImageButton) findViewById(R.id.img_but_gtareas);
		mImgButGTareas.setOnClickListener(this);
		mImgButBEntrada = (ImageButton) findViewById(R.id.img_but_bentrada);
		mImgButBEntrada.setOnClickListener(this);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.bg_action_bar));
		actionBar.setDisplayShowTitleEnabled(true);

		if (app.ismIsADirector()) {
			mImgButBEntrada.setVisibility(View.VISIBLE);
		}

		if (app.ismIsAnAdministrator()) {
			mImgButGTareas.setVisibility(View.VISIBLE);
		}
	}

	public void onClick(View v) {
		Class className;

		switch (v.getId()) {
		case R.id.img_but_gtareas:
			className = GTareasActivity.class;
			break;
		case R.id.img_but_bentrada:
			className = BEntradaActivity.class;
			break;
		default:
			className = null;
			break;
		}

		if (className != null) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), className);
			startActivity(intent);
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Esta seguro de cerrar sesi\u00F3n ?")
				.setCancelable(false)
				.setPositiveButton("Si", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						HomeActivity.this.finish();
						Intent intent = new Intent();
						intent.setClass(getApplication(), LoginActivity.class);
						startActivity(intent);
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
