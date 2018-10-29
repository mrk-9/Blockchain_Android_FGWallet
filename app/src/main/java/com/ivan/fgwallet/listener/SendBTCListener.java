package com.ivan.fgwallet.listener;


import com.ivan.fgwallet.Fragments.WalletMenuFragment;

public class SendBTCListener {

	private static SendBTCListener instance;

	private SendBTCListener() {
		instance = this;
	}
	
	public static SendBTCListener getIntance() {
		if (instance == null)
			new SendBTCListener();
		return instance;
	}
	
	public void getBalance() {
		for (WalletMenuFragment fragment : WalletMenuFragment.getInstances()) {
			fragment.getBalance();
		}
	}
}
