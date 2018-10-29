package com.ivan.fgwallet;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ivan.fgwallet.WalletApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.utils.Constant;

import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.bitcoinj.wallet.Wallet;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RestoreWalletActivtiy extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    private CountryCodePicker ccp;

    TextView bt_restoreWallet;
    EditText _number;
    EditText _pin;
    String phoneText;

    private void init() {
        bt_restoreWallet = findViewById(R.id.bt_restoreWallet);
        _number = findViewById(R.id.number);
        _pin = findViewById(R.id.pin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_wallet_activtiy);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        getSupportActionBar().hide();
//        ButterKnife.inject(this);
        init();
        ccp.setCountryForPhoneCode(81);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                Constant.COUNTRY_CODE = "+" + ccp.getSelectedCountryCode();
            }
        });
//        final ArrayList<String> arrCode = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.country_code)));
//        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.country_code,R.id.textId,arrCode);
//        countryCode.setAdapter(stringArrayAdapter);
//        countryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                System.out.println(arrCode.get(i));
//                Constant.COUNTRY_CODE = arrCode.get(i);
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        bt_restoreWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                phoneText = _number.getText().toString();
                if (phoneText.length() < 10) {
//                    _number.setError("Please enter mobile number");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter mobile number", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (_pin.getText().toString().equals("")) {
//                    _pin.setError("Please enter new pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter new pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    restore(Constant.COUNTRY_CODE + phoneText, _pin.getText().toString());
                }
            }
        });


    }

    public void restore(final String number, final String newPin) {

        String path = "http://128.199.129.208/api/user/login";
        System.out.println(path);
        progress_dialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        progress_dialog.show();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", number);
        params.put("pin", newPin);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                path, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        try {
                            message = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (message.equals("SUCCESS")) {
                            PrefManager prefManager = new PrefManager(getApplicationContext());
                            prefManager.setPref(PrefManager.KEY_PHONE, number);
                            prefManager.setPref(PrefManager.KEY_PIN, newPin);
                            String apiToken = "", strRecovery = "";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                apiToken = ojb.getString("api_token");
                                strRecovery = ojb.getString("recovery_phrase");

//                                JSONObject jb2 = ojb.getJSONObject("balance");
//                                System.out.println(jb2.toString());
//                                JSONArray venues = jb2.getJSONArray("balances");
//                                int len = venues.length();
//                                JSONObject jsonBalance = venues.getJSONObject(len - 1);
//                                address = jsonBalance.getString("address");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            prefManager.setPref(PrefManager.KEY_API_TOKEN, apiToken);
                            prefManager.setPref(PrefManager.KEY_RECOVERY_PHRASE, strRecovery);
                            Intent intent = new Intent(RestoreWalletActivtiy.this, EnterRecoveryPhraseActivity.class);
                            startActivity(intent);
                        } else {
                            String msg = "Something went wrong , Please try later";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                msg = ojb.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        progress_dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Can't connect to server!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progress_dialog.dismiss();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        // Adding request to request queue
        WalletApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void createAddress(String apiToken) {
        String path = "http://128.199.129.208/api/wallet/new_address";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_token", apiToken);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                path, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        try {
                            message = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (message.equals("SUCCESS")) {
                            String address = "";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                JSONObject jsonAddress = ojb.getJSONObject("address");
                                address = jsonAddress.getString("address");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            PrefManager prefManager = new PrefManager(getApplicationContext());
                            prefManager.setPref(PrefManager.KEY_ADDRESS, address);
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {
                            String msg = "Something went wrong , Please try later";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                msg = ojb.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        progress_dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Can't connect to server!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progress_dialog.dismiss();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        // Adding request to request queue
        WalletApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
