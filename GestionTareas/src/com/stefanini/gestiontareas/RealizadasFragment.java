package com.stefanini.gestiontareas;

import java.io.IOException;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.stefanini.util.AppConstants;
import com.stefanini.util.AppStatus;

public class RealizadasFragment extends SherlockFragment implements
		OnItemClickListener {

	private String SOAP_ACTION_TAREAS_TERMINADAS = "http://IT.Mindefensa.WebService.CtrlActViceministerio.com/ObtenerTareasTerminadasPorResponsableLogueadoActualmente";
	private String METHOD_NAME_TAREAS_TERMINADAS = "ObtenerTareasTerminadasPorResponsableLogueadoActualmente";

	private ProgressBar mProgBarData;
	private ListView mListViewData;
	private ArrayList<TareaActividadCT> mListTareas;
	private LayoutInflater mInflater;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		View view = inflater.inflate(R.layout.pendientes_panel, container,
				false);
		mProgBarData = (ProgressBar) view.findViewById(R.id.prog_bar_data);
		mListViewData = (ListView) view.findViewById(android.R.id.list);
		mListViewData.setOnItemClickListener(this);
		// las tareas ya fueron cargadas
		if (mListTareas != null && !mListTareas.isEmpty()) {
			mListViewData.setAdapter(new TareasListAdapter(mListTareas,
					mInflater));
		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		String userFullName = ((GTareasApplication) getActivity()
				.getApplication()).getmUserFullName();
		if (mListTareas == null && userFullName != null) {
			if (AppStatus.isOnline(getActivity())) {
				new ConsultarTareasPendientesTask().execute(userFullName);
			} else {
				Toast.makeText(
						getActivity(),
						"Sin conexion a internet!, por favor verifique su conexion.",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	private class ConsultarTareasPendientesTask extends
			AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgBarData.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean isThereAnError = false;
			if (isCancelled())
				return isThereAnError;

			String fullName = params[0];

			SoapSerializationEnvelope env = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			env.dotNet = true;
			env.enc = SoapSerializationEnvelope.ENC;
			SoapObject request = new SoapObject(
					AppConstants.SOAP.SOAP_NAMESPACE,
					METHOD_NAME_TAREAS_TERMINADAS);

			PropertyInfo p = new PropertyInfo();
			p.setType(PropertyInfo.STRING_CLASS);
			p.setName("responsable");
			p.setValue(fullName);
			request.addProperty(p);
			env.setOutputSoapObject(request);

			HttpTransportSE httpTransportSE = new HttpTransportSE(
					AppConstants.SOAP.URL_TAREAS_WS);
			// httpTransportSE.debug = true;

			mListTareas = new ArrayList<TareaActividadCT>();
			try {
				httpTransportSE.call(SOAP_ACTION_TAREAS_TERMINADAS, env);

				SoapObject response = (SoapObject) env.getResponse();
				response = (SoapObject) response.getProperty(0);
				int totalCount = response.getPropertyCount();
				for (int i = 0; i < totalCount; i++) {
					SoapObject soapPojo = (SoapObject) response.getProperty(i);
					TareaActividadCT tarea = new TareaActividadCT();
					tarea.descripcion = soapPojo
							.getPropertyAsString("Descripcion");
					tarea.responsable = soapPojo
							.getPropertyAsString("Responsable");
					tarea.fechaLimiteString = soapPojo
							.getPropertyAsString("FechaLimite");
					tarea.fechaLimiteDate = TareaActividadCT
							.getDateFromString(soapPojo
									.getPropertyAsString("FechaLimite"));
					tarea.comentarioResponsable = soapPojo
							.getPropertyAsString("ComentarioResponsable");
					tarea.fechaCulminacionString = soapPojo
							.getPropertyAsString("FechaDeCulminacion");
					tarea.fechaCulminacionDate = TareaActividadCT
							.getDateFromString(soapPojo
									.getPropertyAsString("FechaDeCulminacion"));
					tarea.importancia = soapPojo
							.getPropertyAsString("Importancia");
					tarea.estadoCulminacion = soapPojo
							.getPropertyAsString("EstadoDeCulminacion");
					tarea.viceministerioId = Long.parseLong(soapPojo
							.getPropertyAsString("ViceministerioId"));
					tarea.viceministerioTitle = soapPojo
							.getPropertyAsString("ViceministerioTitle");
					tarea.direccionId = Long.parseLong(soapPojo
							.getPropertyAsString("DireccionId"));
					tarea.direccionTitle = soapPojo
							.getPropertyAsString("DireccionTitle");
					tarea.id = Long.parseLong(soapPojo.getProperty("Id")
							.toString());
					tarea.title = soapPojo.getPropertyAsString("Title");
					mListTareas.add(tarea);
				}
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
		protected void onPostExecute(Boolean error) {
			mProgBarData.setVisibility(View.GONE);
			if (error) {
				Toast.makeText(getActivity(),
						"Error en el proceso de carga de tareas realizadas",
						Toast.LENGTH_LONG).show();
				return;
			}
			if (mListTareas != null && !mListTareas.isEmpty()) {
				mListViewData.setAdapter(new TareasListAdapter(mListTareas,
						mInflater));
			}
			super.onPostExecute(error);
		}

	}

	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		TareaActividadCT tarea = mListTareas.get(pos);
		Intent intent = new Intent();
		intent.setClass(getActivity(), EditarTareaActivity.class);
		intent.putExtra(AppConstants.KEY_ID_TAREA, tarea.id);
		intent.putExtra(AppConstants.KEY_NAME_TAREA, tarea.title);
		intent.putExtra(AppConstants.KEY_DESCRIPCION_TAREA, tarea.descripcion);
		intent.putExtra(AppConstants.KEY_FECHA_LIMITE_TAREA,
				TareaActividadCT.getDateInString(tarea.fechaLimiteDate));
		intent.putExtra(AppConstants.KEY_IMPORTANCIA_TAREA, tarea.importancia);
		intent.putExtra(AppConstants.KEY_ESTADO_TAREA, tarea.estadoCulminacion);
		intent.putExtra(AppConstants.KEY_COMENTARIOS_TAREA,
				tarea.comentarioResponsable);
		intent.putExtra(AppConstants.KEY_EDIT, false);
		startActivity(intent);
	}
}
