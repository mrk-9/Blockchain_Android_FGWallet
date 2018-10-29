package com.ivan.fgwallet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.common.base.Joiner;
import com.ivan.fgwallet.helper.PrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.ivan.fgwallet.WalletApplication;

import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SmsActivity extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;

    EditText inputOtp;

    String Otp, number = "", pin = "", bitcoinAddress = "", deviceId = "";
    private FirebaseAuth mAuth;
    // [END declare_auth]
    boolean mVerificationInProgress = false;
    String mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ProgressDialog dialog;

    private void init() {
        inputOtp = findViewById(R.id.newPin);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        getSupportActionBar().hide();
//        ButterKnife.inject(this);
        init();
        mAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(SmsActivity.this);
        dialog.setMessage("Please Wait...");

        findViewById(R.id.btn_verify_otp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
                if (inputOtp.getText().toString().equals("")) {
//                    inputOtp.setError("Please enter verification code");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter verification code", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    verify(inputOtp.getText().toString());
                }


            }
        });
        findViewById(R.id.btn_reend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(number.startsWith("+")){
//                    setOTP(number);
//                }else{
//                    setOTP("+86"+number);
//                }
                //  setOTP(number);

            }
        });

        Bundle b = getIntent().getExtras();
        if (b != null) {
            number = b.getString("number");
            pin = b.getString("pin");
            bitcoinAddress = b.getString("bitcoinAddress");
            deviceId = b.getString("deviceId");
        }


    }

    public void verify(String s)
    {



                                restore(s);

    }


    public void restore(final String code) {


        String path =  "https://smsapi.atmconsole.com/api/notification/verify-code";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", number);
        params.put("pin", pin);
        params.put("bitcoinAddress", bitcoinAddress);
        params.put("deviceId", deviceId);
        params.put("code", code);



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
                        if (message.equals("Success"))
                        {

                            Wallet wallet = ((WalletApplication) getApplication()).getWallet();

                            DeterministicSeed seed = wallet.getKeyChainSeed();
                            String strRecovery = Joiner.on(" ").join(seed.getMnemonicCode());


                            String timestampCreation = Objects.toString(seed.getCreationTimeSeconds(), null);

                            Intent intent = new Intent(SmsActivity.this, RecoveryPhraseActivity.class);
                            intent.putExtra("pin", pin);
                            intent.putExtra("KEY_RECOVERY_PHRASE", strRecovery);
                            intent.putExtra("TIMESTAMP_CREATION", timestampCreation);
                            startActivity(intent);
                            finish();



                          //  Intent intent = new Intent(SmsActivity.this, RecoveryPhraseActivity.class);
                          //  startActivity(intent);
                        }
                        else {

//                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
//                        progress_dialog.dismiss();
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
                headers.put("Authorization", "Auth AC61a6b28a82e32bf3818df5fd1d5caf06");
                return headers;
            }

        };

        // Adding request to request queue
        WalletApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

}
