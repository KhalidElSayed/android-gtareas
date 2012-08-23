package com.stefanini.gestiontareas;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TareasListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<TareaActividadCT> mListTareas;

	static class ViewHolder {
		TextView labTitle;
		TextView labDate;
		TextView labDirTitle;
		TextView labDescripcion;
	}

	public TareasListAdapter(ArrayList<TareaActividadCT> listTareas,
			LayoutInflater inflater) {
		mListTareas = listTareas;
		mInflater = inflater;
	}

	public int getCount() {
		return mListTareas.size();
	}

	public Object getItem(int position) {
		return mListTareas.get(position);
	}

	public long getItemId(int position) {
		return mListTareas.get(position).id;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.tarea_row, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.labTitle = (TextView) convertView
					.findViewById(R.id.lab_title);
			viewHolder.labDate = (TextView) convertView
					.findViewById(R.id.lab_date);
			viewHolder.labDirTitle = (TextView) convertView
					.findViewById(R.id.lab_dir_title);
			viewHolder.labDescripcion = (TextView) convertView
					.findViewById(R.id.lab_descripcion);
			convertView.setTag(viewHolder);
		}

		viewHolder = (ViewHolder) convertView.getTag();
		TareaActividadCT tarea = mListTareas.get(position);
		viewHolder.labTitle.setText(tarea.title);
		viewHolder.labDate.setText(TareaActividadCT
				.getDateInString(tarea.fechaLimiteDate));
		viewHolder.labDirTitle.setText(tarea.direccionTitle);
		viewHolder.labDescripcion.setText(tarea.descripcion);

		return convertView;
	}

}
