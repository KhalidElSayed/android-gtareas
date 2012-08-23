package com.stefanini.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {
	SQLiteDatabase dataBase; // base de datos
	Context context;
	DBCreator dbCreator; // helper que crea la base de datos

	public DBAdapter(Context ctx) {
		context = ctx;
		dbCreator = new DBCreator(context);
	}

	public void open() {
		dataBase = dbCreator.getWritableDatabase();
	}

	public void close() {
		dbCreator.close();
	}

	public Cursor consultar(String table, String[] columns, String selection,
			String[] selectionArgs, String orderBy) {
		return dataBase.query(table, columns, selection, selectionArgs, null,
				null, orderBy);
	}

	public long delete(String table, String where, String whereArgs[]) {
		return dataBase.delete(table, where, whereArgs);
	}

	public long insert(String table, ContentValues values) {
		return dataBase.insert(table, null, values);
	}

	public long update(String table, ContentValues values, String whereClause,
			String whereArgs[]) {
		return dataBase.update(table, values, whereClause, whereArgs);
	}

	public long countRecords(String table) {
		if (dataBase != null) {
			return DatabaseUtils.queryNumEntries(dataBase, table);
		} else {
			return -1;
		}

	}
}
