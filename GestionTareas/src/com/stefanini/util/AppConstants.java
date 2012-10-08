package com.stefanini.util;

public interface AppConstants {

	String KEY_IS_AN_ADMIN = "isAnAdmin";
	String KEY_IS_A_DIRECTOR = "isAdirector";
	String KEY_USER_FULLNAME = "userFullName";
	String KEY_ID_TAREA = "id_tarea";
	String KEY_NAME_TAREA = "name_tarea";
	String KEY_DESCRIPCION_TAREA = "descripcion_tarea";
	String KEY_FECHA_LIMITE_TAREA = "fecha_limite_tarea";
	String KEY_IMPORTANCIA_TAREA = "importancia_tarea";
	String KEY_ESTADO_TAREA = "estado_tarea";
	String KEY_COMENTARIOS_TAREA = "comentarios_tarea";
	String KEY_EDIT = "EDIT";

	public interface SOAP {
		// nueva ip 190.60.245.41
		// anterior ip 172.16.11.107
		String SOAP_NAMESPACE = "http://IT.Mindefensa.WebService.CtrlActViceministerio.com/";
		String URL_USUARIOS_WS = "http://190.60.245.41:300/Usuarios.asmx?WSDL";
		String URL_VICIMINISTERIOS_WS = "http://190.60.245.41:300/Viceministerios.asmx?WSDL";
		String URL_DIRECCION_VICEMINISTERIOS_WS = "http://190.60.245.41:300/DireccionesViceministerio.asmx?WSDL";
		String URL_TAREAS_WS = "http://190.60.245.41:300/Tareas.asmx?WSDL";
	}

	public interface DataBase {
		String DATA_BASE_NAME = "tareas.db";

		String NAME_TABLE_DIR_VICEMIN = "viceministerio";
		String NAME_TABLE_TIPOS_IMPORTANCIA = "importancia";
		String _ID = "_id integer";

		String PRIMARY_KEY = "PRIMARY KEY";
		String PRIMARY_KEY_AUTO = "PRIMARY KEY AUTOINCREMENT";
		String INTEGER_NOT_NULL = "INTEGER NOT NULL";
		String INTEGER1_NOT_NULL = "INTEGER(1) NOT NULL";
		String DATE_NOT_NULL = " DATE NOT NULL";
		String TIMESTAMP_NOT_NULL = " TIMESTAMP NOT NULL";
		String TEXT_NOT_NULL = "TEXT NOT NULL";
		String TEXT = "TEXT";
	}
}
