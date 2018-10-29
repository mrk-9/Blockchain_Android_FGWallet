package com.ivan.fgwallet.listener;


import com.ivan.fgwallet.Fragments.WalletMenuFragment;

public class CreateAddressListener {

	private static CreateAddressListener instance;

	private CreateAddressListener() {
		instance = this;
	}
	
	public static CreateAddressListener getIntance() {
		if (instance == null)
			new CreateAddressListener();
		return instance;
	}
	
	public void changeAddress() {
		for (WalletMenuFragment fragment : WalletMenuFragment.getInstances()) {
			fragment.changeAddress();
		}
	}
}
