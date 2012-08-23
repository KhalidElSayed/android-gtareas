package com.stefanini.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stefanini.util.AppConstants;
import com.stefanini.util.AppConstants.DataBase;

public class DBCreator extends SQLiteOpenHelper {

	public DBCreator(Context context) {
		super(context, AppConstants.DataBase.DATA_BASE_NAME, null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// tabla viceministerio
		db.execSQL("CREATE TABLE " + DataBase.NAME_TABLE_DIR_VICEMIN + " ("
				+ DataBase._ID + " " + DataBase.PRIMARY_KEY + ", "
				+ "descripcion " + DataBase.TEXT + ", " + "responsable "
				+ DataBase.TEXT_NOT_NULL + ", " + "viceministerioId "
				+ DataBase.TEXT_NOT_NULL + ", " + "viceministerioTitle "
				+ DataBase.TEXT_NOT_NULL + ", " + "title " + DataBase.TEXT
				+ ")");

		// tabla niveles importancia
		db.execSQL("CREATE TABLE " + DataBase.NAME_TABLE_TIPOS_IMPORTANCIA
				+ "( " + DataBase._ID + " " + DataBase.PRIMARY_KEY_AUTO + ", "
				+ "Descripcion " + DataBase.TEXT_NOT_NULL + ")");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
