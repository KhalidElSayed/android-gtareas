package com.stefanini.gestiontareas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.stefanini.db.DBAdapter;
import com.stefanini.util.AppConstants;

public class CrearTareaFragment extends SherlockFragment implements
		OnClickListener, DatePickerFragment.onDataSetChangeListener {

	private final String SOAP_ACTION_CREAR_TAREA = "http://IT.Mindefensa.WebService.CtrlActViceministerio.com/InsertarTareaPorNombreDireccion";
	private final String METHOD_NAME_CREAR_TAREA = "InsertarTareaPorNombreDireccion";

	private EditText mTxtNombre;
	private EditText mTxtFechaLimite;
	private Spinner mSpinDirAsig;
	private Spinner mSpinImportancia;
	private EditText mTxtDescripcion;
	private Button mButCrear;

	private DatePickerFragment mDatePicker;

	private List<String> mListDireccionesVice;
	private List<String> mListTiposImportancia;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DBAdapter dbAdapter = new DBAdapter(getActivity());
		dbAdapter.open();
		// load direcciones viceministro
		mListDireccionesVice = new ArrayList<String>();
		Cursor c = dbAdapter.consultar(
				AppConstants.DataBase.NAME_TABLE_DIR_VICEMIN,
				new String[] { "Title" }, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					mListDireccionesVice.add(c.getString(0));
				} while (c.moveToNext());
			}
			c.close();
		}

		// load tipos de importancia
		mListTiposImportancia = new ArrayList<String>();
		c = dbAdapter.consultar(
				AppConstants.DataBase.NAME_TABLE_TIPOS_IMPORTANCIA,
				new String[] { "descripcion" }, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					mListTiposImportancia.add(c.getString(0));
				} while (c.moveToNext());
			}
			c.close();
		}
		dbAdapter.close();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.crear_tarea_panel, container,
				false);
		mDatePicker = new DatePickerFragment();
		mDatePicker.setOnDataSetChangeListener(this);
		mTxtNombre = (EditText) view.findViewById(R.id.txt_title);
		mTxtFechaLimite = (EditText) view.findViewById(R.id.txt_fecha_limite);
		mTxtFechaLimite.setOnClickListener(this);
		// set the now date
		int year = mDatePicker.mYear;
		int month = mDatePicker.mMonth + 1;
		int day = mDatePicker.mDay;
		mTxtFechaLimite.setText(((day < 10) ? "0" + day : day) + "-"
				+ ((month < 10) ? "0" + month : month) + "-" + year);

		mSpinDirAsig = (Spinner) view.findViewById(R.id.spin_dir_asig);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				mListDireccionesVice);
		arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinDirAsig.setAdapter(arrayAdapter);
		mSpinDirAsig.setSelection(0);

		mSpinImportancia = (Spinner) view.findViewById(R.id.spin_importancia);
		arrayAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, mListTiposImportancia);
		arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinImportancia.setAdapter(arrayAdapter);
		mSpinImportancia.setSelection(0);

		mTxtDescripcion = (EditText) view.findViewById(R.id.txt_descripcion);
		mButCrear = (Button) view.findViewById(R.id.but_crear);
		mButCrear.setOnClickListener(this);

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_fecha_limite:
			mDatePicker.show(getFragmentManager(), "datePicker");
			break;

		case R.id.but_crear:
			crearTarea();
			break;
		default:
			break;
		}
	}

	private void crearTarea() {
		if (!isValidateInputData()) {
			Toast.makeText(getActivity(),
					"Por favor ingrese todos los datos solicitados",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (!isValidateDate()) {
			Toast.makeText(getActivity(),
					"La fecha limite no puede ser antes de la fecha actual",
					Toast.LENGTH_LONG).show();
			return;
		}

		new CrearTareaTask().execute();
	}

	public void onDateChanged() {
		int month = mDatePicker.mMonth + 1;
		mTxtFechaLimite.setText(((mDatePicker.mDay < 10) ? "0"
				+ mDatePicker.mDay : mDatePicker.mDay + "")
				+ "-"
				+ ((month < 10) ? "0" + month : mDatePicker.mMonth + "")
				+ "-" + mDatePicker.mYear);
	}

	private boolean isValidateInputData() {
		return !mTxtNombre.getText().toString().equals("")
				&& !mTxtFechaLimite.getText().toString().equals("")
				&& !mTxtDescripcion.getText().toString().equals("");
	}

	private boolean isValidateDate() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.MILLISECOND, 0);
		Date now = c.getTime();

		c.set(Calendar.YEAR, mDatePicker.mYear);
		c.set(Calendar.MONTH, mDatePicker.mMonth);
		c.set(Calendar.DAY_OF_MONTH, mDatePicker.mDay);
		Date other = c.getTime();

		return !other.before(now);

	}

	private class CrearTareaTask extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mProgressDialog;
		boolean isThereANewTask;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setMessage("Creando nueva tarea...");
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
					AppConstants.SOAP.SOAP_NAMESPACE, METHOD_NAME_CREAR_TAREA);

			// title
			PropertyInfo p = new PropertyInfo();
			p.setName("title");
			p.setType(PropertyInfo.STRING_CLASS);
			p.setValue(mTxtNombre.getText().toString());
			request.addProperty(p);

			// descripcion
			p = new PropertyInfo();
			p.setName("descripcion");
			p.setType(PropertyInfo.STRING_CLASS);
			p.setValue(mTxtDescripcion.getText().toString());
			request.addProperty(p);

			// idviceministerio
			p = new PropertyInfo();
			p.setName("idViceministerio");
			p.setType(PropertyInfo.STRING_CLASS);
			// FIXME resolver el id
			p.setValue("1");
			request.addProperty(p);

			// nombre direccionviceministerio
			p = new PropertyInfo();
			p.setName("nombreDireccionViceministerio");
			p.setType(PropertyInfo.STRING_CLASS);
			p.setValue(mSpinDirAsig.getSelectedItem().toString());
			request.addProperty(p);

			// fechalimite
			int month = mDatePicker.mMonth + 1;
			String strDate = (mDatePicker.mYear + "-"
					+ ((month < 10) ? "0" + month : mDatePicker.mMonth + "")
					+ "-" + ((mDatePicker.mDay < 10) ? "0" + mDatePicker.mDay
					: mDatePicker.mDay + ""));
			p = new PropertyInfo();
			p.setName("fechaLimite");
			p.setType(PropertyInfo.STRING_CLASS);
			p.setValue(strDate);
			request.addProperty(p);

			// importancia
			p = new PropertyInfo();
			p.setName("importancia");
			p.setType(PropertyInfo.STRING_CLASS);
			p.setValue(mSpinImportancia.getSelectedItem().toString());
			request.addProperty(p);

			env.setOutputSoapObject(request);
			HttpTransportSE httpTransportSE = new HttpTransportSE(
					AppConstants.SOAP.URL_TAREAS_WS);
			httpTransportSE.debug = true;
			SoapPrimitive response = null;

			try {
				httpTransportSE.call(SOAP_ACTION_CREAR_TAREA, env);
				response = (SoapPrimitive) env.getResponse();
				isThereANewTask = new Boolean(response.toString());
				Log.d("seCreoTarea", response.toString());
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
			mProgressDialog.dismiss();
			if (isThereAnError) {
				Toast.makeText(getActivity(),
						"Error: No se pudo crear la tarea", Toast.LENGTH_LONG)
						.show();
				return;
			}

			if (isThereANewTask) {
				Toast.makeText(getActivity(), "Se ha creado una nueva tarea",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClass(getActivity(), GTareasActivity.class);
				// FIXME override the onNewIntent() in GTareasActivity to
				// proccess the new task created
				// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				Toast.makeText(getActivity(), "La tarea no se ha creado",
						Toast.LENGTH_LONG).show();
			}
		}
	}

}
