package com.stefanini.gestiontareas;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class DatePickerFragment extends SherlockDialogFragment implements
		DatePickerDialog.OnDateSetListener {

	private onDataSetChangeListener mOnDataSetChangeListener;

	protected int mYear;
	protected int mMonth;
	protected int mDay;

	public DatePickerFragment() {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
	}

	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		mYear = year;
		mMonth = monthOfYear;
		mDay = dayOfMonth;

		if (mOnDataSetChangeListener != null) {
			mOnDataSetChangeListener.onDateChanged();
		}
	}

	public void setOnDataSetChangeListener(
			onDataSetChangeListener onDataSetChangeListener) {
		this.mOnDataSetChangeListener = onDataSetChangeListener;
	}

	public static interface onDataSetChangeListener {
		void onDateChanged();
	}
}
