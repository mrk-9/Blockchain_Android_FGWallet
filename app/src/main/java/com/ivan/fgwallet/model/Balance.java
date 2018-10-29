package com.ivan.fgwallet.model;

@SuppressWarnings("serial")
public class Balance {

	private String user_id;
	private String label;
	private String address;
	private String available_balance;
	private String pending_received_balance;

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAvailable_balance() {
		return available_balance;
	}

	public void setAvailable_balance(String available_balance) {
		this.available_balance = available_balance;
	}

	public String getPending_received_balance() {
		return pending_received_balance;
	}

	public void setPending_received_balance(String pending_received_balance) {
		this.pending_received_balance = pending_received_balance;
	}
}