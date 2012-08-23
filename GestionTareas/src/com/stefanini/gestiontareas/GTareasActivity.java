package com.stefanini.gestiontareas;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.stefanini.db.DBAdapter;
import com.stefanini.util.AppConstants;

public class GTareasActivity extends SherlockFragmentActivity {

	private final String SOAP_ACTION_GET_DIRECCIONES = "http://IT.Mindefensa.WebService.CtrlActViceministerio.com/ObtenerDireccionesPorIdViceministerio";
	private final String METHOD_NAME_GET_DIRECCIONES = "ObtenerDireccionesPorIdViceministerio";
	private final String SOAP_ACTION_TIPO_IMPORTANCIA = "http://IT.Mindefensa.WebService.CtrlActViceministerio.com/ObtenerTiposImportancia";
	private final String METHOD_NAME_TIPO_IMPORTANCIA = "ObtenerTiposImportancia";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(HomeActivity.THEME);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
		actionBar.setDisplayShowTitleEnabled(true);

		// tab tareas
		Tab tab = actionBar
				.newTab()
				.setText(getString(R.string.title_panel_ctareas))
				.setTabListener(
						new TabListener<TareasFragment>(GTareasActivity.this,
								"tareas", TareasFragment.class));
		actionBar.addTab(tab);

		DBAdapter dbAdapter = new DBAdapter(this);
		dbAdapter.open();
		if (dbAdapter
				.countRecords(AppConstants.DataBase.NAME_TABLE_DIR_VICEMIN) <= 0
				|| dbAdapter
						.countRecords(AppConstants.DataBase.NAME_TABLE_TIPOS_IMPORTANCIA) <= 0) {
			new DescargarDataToSpinners().execute();
		}
		dbAdapter.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, R.drawable.ic_action_add, Menu.NONE, "Crear")
				.setIcon(R.drawable.ic_action_add_light)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();

		switch (item.getItemId()) {
		case R.drawable.ic_action_add:
			intent.setClass(getApplication(), CrearTareaActivity.class);
			break;
		case android.R.id.home:
			// app icon in action bar clicked; go home
			intent.setClass(getApplication(), HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			break;

		default:
			break;
		}
		startActivity(intent);
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// FIXME armar objeto tarea con los datos de la nueva tarea y
		// enviarselo a la lista de tareas del fragment
		Log.d("onNewIntent", intent.toString());
		if (getSupportFragmentManager().findFragmentByTag("tareas") != null) {
			Log.d("fragment no null", "fragment");
		}

	}

	private class DescargarDataToSpinners extends
			AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mProgressDialog;
		private DBAdapter dbAdapter;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dbAdapter = new DBAdapter(getApplicationContext());

			mProgressDialog = new ProgressDialog(GTareasActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setMessage("Descargando datos del servidor...");
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean thereIsAnError = false;

			if (isCancelled()) {
				GTareasActivity.this.runOnUiThread(new Runnable() {

					public void run() {
						if (mProgressDialog != null) {
							mProgressDialog.dismiss();
						}
					}
				});
				return thereIsAnError;
			}

			// // insertar datos en tabla viceministerio
			SoapSerializationEnvelope env = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			env.dotNet = true;
			env.enc = SoapSerializationEnvelope.ENC;
			SoapObject request = new SoapObject(
					AppConstants.SOAP.SOAP_NAMESPACE,
					METHOD_NAME_GET_DIRECCIONES);

			// id
			PropertyInfo p = new PropertyInfo();
			p.setType(PropertyInfo.STRING_CLASS);
			p.setName("IdViceministerio");
			p.setValue("1");
			request.addProperty(p);
			env.setOutputSoapObject(request);

			HttpTransportSE httpTransportSE = new HttpTransportSE(
					AppConstants.SOAP.URL_DIRECCION_VICEMINISTERIOS_WS);
			// httpTransportSE.debug = true;

			// open the database
			dbAdapter.open();

			try {
				httpTransportSE.call(SOAP_ACTION_GET_DIRECCIONES, env);
				SoapObject response = (SoapObject) env.getResponse();
				response = (SoapObject) response.getProperty(0);
				int totalCount = response.getPropertyCount();

				for (int i = 0; i < totalCount; i++) {
					SoapObject soapPojo = (SoapObject) response.getProperty(i);
					ContentValues values = new ContentValues();
					values.put("_id", Integer.parseInt(soapPojo
							.getPropertyAsString("Id")));
					values.put("descripcion",
							soapPojo.getPropertyAsString("Descripcion"));
					values.put("responsable",
							soapPojo.getPropertyAsString("Responsable"));
					values.put("viceministerioId",
							soapPojo.getPropertyAsString("ViceministerioId"));
					values.put("viceministerioTitle",
							soapPojo.getPropertyAsString("ViceministerioTitle"));
					values.put("title", soapPojo.getPropertyAsString("Title"));
					dbAdapter.insert(
							AppConstants.DataBase.NAME_TABLE_DIR_VICEMIN,
							values);
				}
			} catch (IOException e) {
				thereIsAnError = true;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				thereIsAnError = true;
				e.printStackTrace();
			}

			// insertar datos en tabla importancia
			env = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			env.dotNet = true;
			env.enc = SoapSerializationEnvelope.ENC;
			request = new SoapObject(AppConstants.SOAP.SOAP_NAMESPACE,
					METHOD_NAME_TIPO_IMPORTANCIA);

			httpTransportSE = new HttpTransportSE(
					AppConstants.SOAP.URL_TAREAS_WS);
			httpTransportSE.debug = true;

			try {
				httpTransportSE.call(SOAP_ACTION_TIPO_IMPORTANCIA, env);
				SoapObject response = (SoapObject) env.getResponse();
				response = (SoapObject) response.getProperty(0);
				int totalCount = response.getPropertyCount();
				for (int i = 0; i < totalCount; i++) {
					SoapPrimitive soapPojo = (SoapPrimitive) response
							.getProperty(i);
					ContentValues values = new ContentValues();
					values.put("descripcion", soapPojo.toString());
					dbAdapter.insert(
							AppConstants.DataBase.NAME_TABLE_TIPOS_IMPORTANCIA,
							values);
				}
			} catch (IOException e) {
				thereIsAnError = true;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				thereIsAnError = true;
				e.printStackTrace();
			}

			dbAdapter.close();
			return thereIsAnError;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
		}
	}
}
