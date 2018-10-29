package com.ivan.fgwallet.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Ravi on 08/07/15.
 */
public class PrefManager {
    // Shared Preferences
    public static  SharedPreferences pref;

    // Editor for Shared preferences
    public static  Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;



    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PIN = "pin";
    public static final String KEY_API_TOKEN = "api_token";
    public static final String KEY_RECOVERY_PHRASE = "recovery_phrase";
    public static final String KEY_RESULT_SENT = "result_sent";
    public static final String KEY_TOTAL_BALANCE = "total_balance";
    public static final String TIMESTAMP_CREATION ="0";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences("FWWallet", PRIVATE_MODE);
        editor = pref.edit();
    }
    public static void setPref(String key,String mobileNumber) {
        editor.putString(key, mobileNumber);
        editor.commit();
    }

    public static String getpref(String key) {
        String data = pref.getString(key,"");
        return data;
    }
}
