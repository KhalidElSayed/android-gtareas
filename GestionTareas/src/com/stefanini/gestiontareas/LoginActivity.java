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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.stefanini.util.AppConstants;

public class LoginActivity extends SherlockFragmentActivity implements
		OnClickListener {

	// ///Constants/////
	private final String SOAP_ACTION_VALIDATE_USER = "http://IT.Mindefensa.WebService.CtrlActViceministerio.com/ValidadCredencialesUsuario";
	private final String METHOD_NAME_VALIDAR_USER = "ValidadCredencialesUsuario";
	private final String SOAP_ACTION_IS_AN_ADMIN = "http://IT.Mindefensa.WebService.CtrlActViceministerio.com/UsuarioPerteneceAGrupoAdministradoresBusquedaPorLogin";
	private final String METHOD_NAME_IS_AN_ADMIN = "UsuarioPerteneceAGrupoAdministradoresBusquedaPorLogin";
	private final String SOAP_ACTION_IS_A_DIRECTOR = "http://IT.Mindefensa.WebService.CtrlActViceministerio.com/UsuarioPerteneceAGrupoDirectoresBusquedaPorLogin";
	private final String METHOD_NAME_IS_A_DIRECTOR = "UsuarioPerteneceAGrupoDirectoresBusquedaPorLogin";
	private final String SOAP_ACTION_GET_USER_NAME = "http://IT.Mindefensa.WebService.CtrlActViceministerio.com/ObtenerNombreUsuario";
	private final String METHOD_NAME_GET_USER_NAME = "ObtenerNombreUsuario";

	// ///Properties/////
	private TextView mTxtUser;
	private TextView mTxtPass;
	private Button mButLogin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(HomeActivity.THEME);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_screen);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.bg_action_bar));
		init();
	}

	public void init() {
		mTxtUser = (TextView) findViewById(R.id.txt_user);
		// FIXME
		mTxtUser.setText("fleon");
		mTxtPass = (TextView) findViewById(R.id.txt_pass);
		mTxtPass.setText("F1073321637*");
		mButLogin = (Button) findViewById(R.id.but_login);
		mButLogin.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.but_login:

			if (hayDatosEntradaValidos()) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						mTxtPass.getApplicationWindowToken(), 0);
				new DoLoginTask().execute(mTxtUser.getText().toString(),
						mTxtPass.getText().toString());
			}
			break;

		default:
			break;
		}

	}

	private boolean hayDatosEntradaValidos() {
		return mTxtUser.getText().toString() != null
				&& !mTxtUser.getText().toString().equals("")
				&& mTxtPass.getText().toString() != null
				&& !mTxtPass.getText().toString().equals("");
	}

	/** tarea para validar usuario y password del usuario */
	private class DoLoginTask extends AsyncTask<String, Void, Boolean> {

		private ProgressDialog mProgressDialog;
		private boolean mIsLogged;
		private boolean mIsAnAdmin;
		private boolean mIsADirector;
		private String mUserFullName;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(LoginActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setMessage(getResources().getString(
					R.string.msg_login));
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {

			boolean isThereAnError = false;

			if (isCancelled()) {
				LoginActivity.this.runOnUiThread(new Runnable() {

					public void run() {
						if (mProgressDialog != null) {
							mProgressDialog.dismiss();
						}
					}
				});
				return isThereAnError;
			}

			String user = params[0];
			String pass = params[1];

			SoapSerializationEnvelope env = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			env.dotNet = true;
			env.enc = SoapSerializationEnvelope.ENC;
			SoapObject request = new SoapObject(
					AppConstants.SOAP.SOAP_NAMESPACE, METHOD_NAME_VALIDAR_USER);

			// user
			PropertyInfo p = new PropertyInfo();
			p.setType(PropertyInfo.STRING_CLASS);
			p.setName("login");
			p.setValue(user);
			request.addProperty(p);
			// pass
			p = new PropertyInfo();
			p.setType(PropertyInfo.STRING_CLASS);
			p.setName("contrasenia");
			p.setValue(pass);
			request.addProperty(p);

			env.setOutputSoapObject(request);
			HttpTransportSE httpTransportSE = new HttpTransportSE(
					AppConstants.SOAP.URL_USUARIOS_WS);
			SoapPrimitive response = null;
			try {
				httpTransportSE.call(SOAP_ACTION_VALIDATE_USER, env);
				response = (SoapPrimitive) env.getResponse();
				mIsLogged = new Boolean(response.toString());
				Log.d("isLogged", response.toString());

				if (mIsLogged) {
					// Consultar el nombre completo del usuario
					env = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					env.dotNet = true;
					env.enc = SoapSerializationEnvelope.ENC;
					request = new SoapObject(AppConstants.SOAP.SOAP_NAMESPACE,
							METHOD_NAME_GET_USER_NAME);
					p = new PropertyInfo();
					p.setName("login");
					p.setValue(user);
					request.addProperty(p);
					env.setOutputSoapObject(request);

					httpTransportSE = new HttpTransportSE(
							AppConstants.SOAP.URL_USUARIOS_WS);
					httpTransportSE.call(SOAP_ACTION_GET_USER_NAME, env);
					response = (SoapPrimitive) env.getResponse();
					mUserFullName = response.toString();
					Log.d("userFullName", response.toString());

					// Consultar si es un usuario administrador
					env = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					env.dotNet = true;
					env.enc = SoapSerializationEnvelope.ENC;
					request = new SoapObject(AppConstants.SOAP.SOAP_NAMESPACE,
							METHOD_NAME_IS_AN_ADMIN);
					p = new PropertyInfo();
					p.setType(PropertyInfo.STRING_CLASS);
					p.setName("login");
					p.setValue(user);
					request.addProperty(p);
					env.setOutputSoapObject(request);

					httpTransportSE = new HttpTransportSE(
							AppConstants.SOAP.URL_USUARIOS_WS);
					httpTransportSE.call(SOAP_ACTION_IS_AN_ADMIN, env);
					response = (SoapPrimitive) env.getResponse();
					mIsAnAdmin = new Boolean(response.toString());
					Log.d("isAnAdmin", response.toString());

					// Consultar si es un usuario director
					env = new SoapSerializationEnvelope(SoapEnvelope.VER11);
					env.dotNet = true;
					env.enc = SoapSerializationEnvelope.ENC;
					request = new SoapObject(AppConstants.SOAP.SOAP_NAMESPACE,
							METHOD_NAME_IS_A_DIRECTOR);
					p = new PropertyInfo();
					p.setType(PropertyInfo.STRING_CLASS);
					p.setName("login");
					p.setValue(user);
					request.addProperty(p);
					env.setOutputSoapObject(request);

					httpTransportSE = new HttpTransportSE(
							AppConstants.SOAP.URL_USUARIOS_WS);
					httpTransportSE.call(SOAP_ACTION_IS_A_DIRECTOR, env);
					response = (SoapPrimitive) env.getResponse();
					mIsADirector = new Boolean(response.toString());
					Log.d("isADirector", response.toString());
				}

			} catch (IOException e) {
				e.printStackTrace();
				isThereAnError = true;
			} catch (XmlPullParserException e) {
				e.printStackTrace();
				isThereAnError = true;
			}

			return isThereAnError;
		}

		@Override
		protected void onPostExecute(Boolean thereIsAnError) {
			super.onPostExecute(thereIsAnError);
			mProgressDialog.dismiss();
			if (thereIsAnError) {
				Toast.makeText(LoginActivity.this,
						"Error en el proceso de validacion", Toast.LENGTH_LONG)
						.show();
				return;
			}

			if (mIsLogged) {
				GTareasApplication app = (GTareasApplication) getApplication();
				app.setmIsADirector(mIsADirector);
				app.setmIsAnAdministrator(mIsAnAdmin);
				app.setmUserFullName(mUserFullName);
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), HomeActivity.class);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(LoginActivity.this,
						getResources().getString(R.string.msg_login_invalid),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
