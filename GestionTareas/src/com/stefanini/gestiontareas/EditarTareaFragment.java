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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.stefanini.util.AppConstants;
import com.stefanini.util.AppStatus;

public class EditarTareaFragment extends SherlockFragment implements
		OnClickListener {

	private String SOAP_ACTION_UPDATE_TAREA = "http://IT.Mindefensa.WebService.CtrlActViceministerio.com/ActualizarTareaResponsable";
	private String METHOD_NAME_UPDATE_TAREA = "ActualizarTareaResponsable";

	private EditText mTxtIdTarea;
	private EditText mTxtNombre;
	private EditText mTxtDescripcion;
	private EditText mTxtFechaLimite;
	private EditText mTxtImportancia;
	private Spinner mSpinEstado;
	private EditText mTxtComentario;
	private Button mButGuardar;

	private String mEstados[] = new String[] { "No terminada", "Terminada" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editar_tarea_panel, container,
				false);
		mTxtIdTarea = (EditText) view.findViewById(R.id.txt_id_tarea);
		mTxtNombre = (EditText) view.findViewById(R.id.txt_nombre);
		mTxtDescripcion = (EditText) view.findViewById(R.id.txt_descripcion);
		mTxtFechaLimite = (EditText) view.findViewById(R.id.txt_fecha_limite);
		mTxtImportancia = (EditText) view.findViewById(R.id.txt_importancia);

		// estados
		mSpinEstado = (Spinner) view.findViewById(R.id.spin_estado);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, mEstados);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinEstado.setAdapter(adapter);

		mTxtComentario = (EditText) view.findViewById(R.id.txt_comentario);
		mButGuardar = (Button) view.findViewById(R.id.but_guardar);
		mButGuardar.setOnClickListener(this);

		// set data from selection
		Bundle arguments = getArguments();

		if (arguments != null) {
			mTxtIdTarea.setText(""
					+ arguments.getLong(AppConstants.KEY_ID_TAREA));
			mTxtNombre
					.setText(arguments.getString(AppConstants.KEY_NAME_TAREA));
			mTxtDescripcion.setText(arguments
					.getString(AppConstants.KEY_DESCRIPCION_TAREA));
			mTxtFechaLimite.setText(arguments
					.getString(AppConstants.KEY_FECHA_LIMITE_TAREA));
			mTxtImportancia.setText(arguments
					.getString(AppConstants.KEY_IMPORTANCIA_TAREA));
			String estado = arguments.getString(AppConstants.KEY_ESTADO_TAREA);
			if (estado.equalsIgnoreCase(mEstados[0].replace(" ", ""))) {
				mSpinEstado.setSelection(0);
			} else {
				mSpinEstado.setSelection(1);
			}
			mTxtComentario.setText(arguments
					.getString(AppConstants.KEY_COMENTARIOS_TAREA));

			boolean isToEdit = arguments.getBoolean(AppConstants.KEY_EDIT,
					false);
			if (!isToEdit) {
				mSpinEstado.setEnabled(false);
				mTxtComentario.setInputType(InputType.TYPE_NULL);
				mTxtComentario.setFocusableInTouchMode(false);
				mButGuardar.setVisibility(View.GONE);
			}
		}
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void onClick(View v) {
		if (AppStatus.isOnline(getActivity())) {
			new ModificarTareaTask().execute();
		} else {
			Toast.makeText(
					getActivity(),
					"Sin conexion a internet!, por favor verifique su conexion.",
					Toast.LENGTH_LONG).show();
		}

	}

	private class ModificarTareaTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mProgressDialog;
		private boolean updated;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setMessage("Actualizando la tarea...");
			mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean isThereAnError = false;

			if (isCancelled()) {
				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						if (mProgressDialog != null) {
							mProgressDialog.dismiss();
						}
					}
				});
				return isThereAnError;
			}

			SoapSerializationEnvelope env = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			env.dotNet = true;
			env.enc = SoapSerializationEnvelope.ENC;
			SoapObject request = new SoapObject(
					AppConstants.SOAP.SOAP_NAMESPACE, METHOD_NAME_UPDATE_TAREA);

			// idTarea
			PropertyInfo p = new PropertyInfo();
			p.setType(PropertyInfo.STRING_CLASS);
			p.setName("idTarea");
			p.setValue(mTxtIdTarea.getText().toString());
			request.addProperty(p);
			// comentarios
			p = new PropertyInfo();
			p.setType(PropertyInfo.STRING_CLASS);
			p.setName("comentarios");
			String comment = mTxtComentario.getText().toString();
			p.setValue(mTxtComentario.getText().toString());
			request.addProperty(p);
			// estado
			p = new PropertyInfo();
			p.setType(PropertyInfo.STRING_CLASS);
			p.setName("estadoCulminacion");
			String estado = mSpinEstado.getSelectedItem().toString();
			p.setValue(estado);
			request.addProperty(p);
			env.setOutputSoapObject(request);

			HttpTransportSE httpTransportSE = new HttpTransportSE(
					AppConstants.SOAP.URL_TAREAS_WS);
			SoapPrimitive response = null;
			try {
				httpTransportSE.call(SOAP_ACTION_UPDATE_TAREA, env);
				response = (SoapPrimitive) env.getResponse();
				updated = new Boolean(response.toString());
				Log.d("seModificoTarea", response.toString());
			} catch (IOException e) {
				isThereAnError = true;
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				isThereAnError = true;
				e.printStackTrace();
			}
			return isThereAnError;
		}

		@Override
		protected void onPostExecute(Boolean isThereAnError) {
			super.onPostExecute(isThereAnError);
			mProgressDialog.dismiss();
			if (isThereAnError) {
				Toast.makeText(getActivity(),
						"Error: No se pudo actualizar la tarea",
						Toast.LENGTH_LONG).show();
				return;
			}

			if (updated) {
				Toast.makeText(getActivity(),
						"La tarea ha sido actualizada correctamente",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClass(getActivity(), BEntradaActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(),
						"La tarea no ha sido actualizada", Toast.LENGTH_LONG)
						.show();
			}
		}

	}

}
