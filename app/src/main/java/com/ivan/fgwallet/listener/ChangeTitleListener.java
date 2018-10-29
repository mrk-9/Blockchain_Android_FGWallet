package com.ivan.fgwallet.listener;


import com.ivan.fgwallet.HomeActivity;

public class ChangeTitleListener {

	private static ChangeTitleListener instance;

	private ChangeTitleListener() {
		instance = this;
	}
	
	public static ChangeTitleListener getIntance() {
		if (instance == null)
			new ChangeTitleListener();
		return instance;
	}
	
	public void setTitle(String title) {
		for (HomeActivity activity : HomeActivity.getInstances()) {
			activity.setTitle(title);
		}
	}
}
