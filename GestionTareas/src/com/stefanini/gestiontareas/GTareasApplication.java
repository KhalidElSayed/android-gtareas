package com.stefanini.gestiontareas;

import android.app.Application;

public class GTareasApplication extends Application {

	private boolean mIsADirector;
	private boolean mIsAnAdministrator;
	private String mUserFullName;

	public boolean ismIsADirector() {
		return mIsADirector;
	}

	public void setmIsADirector(boolean mIsADirector) {
		this.mIsADirector = mIsADirector;
	}

	public boolean ismIsAnAdministrator() {
		return mIsAnAdministrator;
	}

	public void setmIsAnAdministrator(boolean mIsAnAdministrator) {
		this.mIsAnAdministrator = mIsAnAdministrator;
	}

	public String getmUserFullName() {
		return mUserFullName;
	}

	public void setmUserFullName(String mUserFullName) {
		this.mUserFullName = mUserFullName;
	}

}
