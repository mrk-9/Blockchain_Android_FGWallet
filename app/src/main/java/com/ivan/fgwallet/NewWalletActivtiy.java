package com.ivan.fgwallet;

import com.google.common.base.Joiner;
import com.ivan.fgwallet.WalletApplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.utils.Constant;
import com.ivan.fgwallet.MainActivity;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.bitcoinj.core.Address;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.ivan.fgwallet.MainActivity.bytesToHex;
import static com.ivan.fgwallet.MainActivity.savePinToReg;

public class NewWalletActivtiy extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    private CountryCodePicker ccp;

    TextView bt_restoreWallet;
    EditText _number;
    EditText _newPin;
    EditText _reenterPin;


    String phoneText;
    String address;
    PrefManager prefManager;

    private WalletApplication application;
    private com.ivan.fgwallet.schildbach.wallet.Configuration config;
    private Wallet wallet;

    private void init() {
        bt_restoreWallet = findViewById(R.id.bt_restoreWallet);
        _number = findViewById(R.id.number);
        _newPin = findViewById(R.id.newPin);
        _reenterPin = findViewById(R.id.reenterPin);

        application = (WalletApplication) getApplication();
        config = application.getConfiguration();
        wallet = application.getWallet();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_wallet_activtiy);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        getSupportActionBar().hide();
//        ButterKnife.inject(this);
        init();
        if (!new PrefManager(NewWalletActivtiy.this).getpref(PrefManager.KEY_ADDRESS).equals("")) {
            address = new PrefManager(NewWalletActivtiy.this).getpref(PrefManager.KEY_ADDRESS);

        }
        ccp.setCountryForPhoneCode(81);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                Constant.COUNTRY_CODE = "+" + ccp.getSelectedCountryCode();
            }
        });


        bt_restoreWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneText = _number.getText().toString();
                if (phoneText.length() < 10) {
                    _number.setError("Please enter mobile number");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter mobile number", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                if (_newPin.getText().toString().equals(""))
                {
//                    _newPin.setError("Please enter new pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter new pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                }
                else if (_reenterPin.getText().toString().equals("")) {
//                    _reenterPin.setError("Please enter new pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter new pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                } else if (!_reenterPin.getText().toString().equals(_newPin.getText().toString())) {
//                    _reenterPin.setError("Your pin not match");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Your pin not match", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return;
                } else {
                    createWallet(Constant.COUNTRY_CODE + phoneText, _newPin.getText().toString(), _reenterPin.getText().toString());
                }
            }
        });

    }






    public void createWallet(final String number, final String newPin, String reenterPin) {


//===================================



        final String bitcoinAddress = wallet.freshReceiveAddress().toString();


        final String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);


        String path = "https://smsapi.atmconsole.com/api/notification/send-code";

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
        params.put("deviceId", deviceId);
        params.put("phone", number);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                path, new JSONObject(params),
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        try {
                            message = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (message.equals("Success"))
                        {
                            Intent intent = new Intent(NewWalletActivtiy.this, SmsActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", newPin);
                            intent.putExtra("deviceId", deviceId);
                            intent.putExtra("bitcoinAddress", bitcoinAddress);


                            startActivity(intent);
                            finish();

                        }
                        else
                        {
                            Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG)
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
        })
        {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Auth AC61a6b28a82e32bf3818df5fd1d5caf06");

                return headers;
            }
        };
        // Adding request to request queue

        WalletApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}

