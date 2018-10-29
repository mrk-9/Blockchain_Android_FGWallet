package com.ivan.fgwallet.service;

import android.util.Base64;


import java.util.HashMap;
import java.util.Map;

public class Constants {
	public static String AUTH_USER ="admin";
	public static String AUTH_PASS ="admin123";

	public static Map<String, String> getHeader(){
		Map<String, String> map = new HashMap<>();
		map.put("userauth", AUTH_USER);
		map.put("userpass", AUTH_PASS);
		return map;

	}




	public static Map<String, String> getHeader(String user, String pass){
		Map<String, String> map = new HashMap<>();

		String credentials = user + ":" + pass;
		String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
		map.put("Authorization", auth);
		map.put("Content-Type", "application/json");

		return map;

	}






}
