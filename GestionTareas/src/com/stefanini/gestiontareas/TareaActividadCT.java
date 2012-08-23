package com.stefanini.gestiontareas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TareaActividadCT {

	final static String DATE_PATTERN = "MM/dd/yyyy hh:mm:ss a";

	protected String descripcion;
	protected String responsable;
	protected String fechaLimiteString;
	protected String comentarioResponsable;
	protected String fechaCulminacionString;
	protected String importancia;
	protected String estadoCulminacion;
	protected long viceministerioId;
	protected String viceministerioTitle;
	protected long direccionId;
	protected String direccionTitle;
	protected long id;
	protected String title;
	protected Date fechaLimiteDate;
	protected Date fechaCulminacionDate;

	protected TareaActividadCT() {

	}

	public static Date getDateFromString(String strDate) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
			return dateFormat.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getDateInString(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);

		return ((day < 10) ? "0" + day : day + "") + "/"
				+ ((month < 10) ? "0" + month : month + "") + "/" + year;
	}
}
