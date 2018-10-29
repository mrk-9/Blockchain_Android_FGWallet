package com.ivan.fgwallet;

import com.ivan.fgwallet.WalletApplication;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.listener.SendBTCListener;
import com.ivan.fgwallet.utils.Constant;
import com.ivan.fgwallet.utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;


import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class SendBtcActivity extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;

    EditText recipent;
    EditText amount;
    TextView edtMinusFee;
    TextView edtNetworkFee;
    EditText memo;
    EditText edtJPY;
    ProgressBar proBar;
    TextView tvJPYBalance;
    TextView tvBalance;
    ScrollView scrollView;
    ImageView refresh;

    Double curency;
    BigDecimal totalBalance;
    String address;

    private void init() {
        recipent = findViewById(R.id.recipent);
        amount = findViewById(R.id.amount);
        edtMinusFee = findViewById(R.id.minus_fee);
        edtNetworkFee = findViewById(R.id.network_fee);
        memo = findViewById(R.id.memo);
        edtJPY = findViewById(R.id.tv_jpy);
        proBar = findViewById(R.id.pro_bar);
        tvJPYBalance = findViewById(R.id.tv_jpy_balance);
        tvBalance = findViewById(R.id.tv_balance);
        scrollView = findViewById(R.id.scrollView);
        refresh = findViewById(R.id.refresh);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_btc);
        getSupportActionBar().hide();
//        ButterKnife.inject(this);
        init();
        ImageView imageView = (ImageView) findViewById(R.id.menu);
        imageView.setImageResource(R.mipmap.backmenu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText(getResources().getString(R.string.send));
        if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_ADDRESS).equals("")) {
            address = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_ADDRESS);

        }
        String totalBalance = "";
        DecimalFormat df = new DecimalFormat("0.00000000");
        if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_TOTAL_BALANCE).equals("")) {
            totalBalance = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_TOTAL_BALANCE);
        }
        tvBalance.setText(df.format(BigDecimal.valueOf(Double.valueOf(totalBalance))) + " BTC");
        DecimalFormat df2 = new DecimalFormat("0");
        tvJPYBalance.setText("￥ " + df2.format(Constant.CURRENCY_JPY * Double.valueOf(String.valueOf(totalBalance))));
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshBtc();
            }
        });

        amount.setEnabled(false);
        edtJPY.setEnabled(false);
//        recipent.setOnTouchListener(new View.OnTouchListener() {
//                                      @Override
//                                      public boolean onTouch(View v, MotionEvent event) {
//                                          scrollView.smoothScrollTo(0, scrollView.getBottom());
//                                          scrollView.post(new Runnable() {
//                                                              @Override
//                                                              public void run() {
//                                                                  recipent.requestFocus();
//                                                              }
//                                                          }
//                                          );
//                                          return false;
//                                      }
//                                  }
//        );
        recipent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                if (recipent.getText().toString().length() > 0) {
                    amount.setEnabled(true);
                    edtJPY.setEnabled(true);
                } else {
                    amount.setEnabled(false);
                    edtJPY.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                if (recipent.getText().toString().length() > 0) {
                    if (Utils.parseForgiveness(s.toString()) > 0) {
                        calculateFee(s.toString(), recipent.getText().toString());
                    } else {
                        edtNetworkFee.setVisibility(View.GONE);
                        edtMinusFee.setVisibility(View.VISIBLE);
                        edtMinusFee.setText(getResources().getString(R.string.min_withdrawal) + " 0.000010 BTC");
                        edtMinusFee.setTextColor(getResources().getColor(R.color.red));
                    }
                }
                if (start != 0) {
                    DecimalFormat df = new DecimalFormat("0");
                    if (s.toString().length() > 0) {
                        try {
                            edtJPY.setText(df.format(BigDecimal.valueOf(Double.valueOf(s.toString()) * Constant.CURRENCY_JPY)) + "");
                        } catch (Exception e) {
                            edtJPY.setText("0");
                        }
                    } else {
                        edtJPY.setText("0");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
        edtJPY.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                if (start != 0) {
                    DecimalFormat df = new DecimalFormat("0.0000000");
                    if (s.toString().length() > 0) {
                        try {
                            amount.setText(df.format(Double.valueOf(s.toString()) / Constant.CURRENCY_JPY) + "");
                        } catch (Exception e) {
                            amount.setText("0");
                        }
                    } else {
                        amount.setText("0");
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

//        edtJPY.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                DecimalFormat df = new DecimalFormat("0.0000000");
//                if (edtJPY.getText().toString().length() > 0) {
//                    try {
//                        amount.setText(df.format(Double.valueOf(edtJPY.getText().toString()) / Constant.CURRENCY_JPY) + "");
//                    } catch (Exception e) {
//                        amount.setText("0");
//                    }
//                } else {
//                    amount.setText("0");
//                }
//            }
//        });

        findViewById(R.id.scanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SendBtcActivity.this, ScannerActivity.class);
                startActivityForResult(i, 1);
            }
        });
        findViewById(R.id.continue_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recipent.getText().toString().equals("")) {
//                    Toast.makeText(getApplicationContext(), "Please enter recipent address", Toast.LENGTH_SHORT).show();
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter recipient address", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else if (amount.getText().toString().equals("")) {
//                    Toast.makeText(getApplicationContext(), "Please enter amount", Toast.LENGTH_SHORT).show();
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter amount", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    sendBtc(recipent.getText().toString(), amount.getText().toString(), memo.getText().toString());
                }
            }
        });

    }

    public void calculateFee(String amount, String address) {
        String path = "http://128.199.129.208/api/wallet/calculate-fee";
        proBar.setVisibility(View.VISIBLE);
        edtMinusFee.setVisibility(View.GONE);
        edtNetworkFee.setVisibility(View.GONE);

        String apiToken = "";
        if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN).equals("")) {
            apiToken = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("amount", amount);
        params.put("address", address);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                path, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        double networkFee = 0;
                        try {
                            message = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (message.equals("SUCCESS")) {
                            JSONObject ojb = null;
                            try {
                                ojb = response.getJSONObject("data");
                                networkFee = ojb.getDouble("estimated_network_fee");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Toast.makeText(getApplicationContext(),networkFee + "",Toast.LENGTH_SHORT).show();
                        } else {
                        }
                        proBar.setVisibility(View.GONE);
                        edtMinusFee.setVisibility(View.VISIBLE);
                        edtNetworkFee.setVisibility(View.VISIBLE);
                        edtMinusFee.setTextColor(getResources().getColor(R.color.colorAccent));

                        String totalBalance = "";
                        if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_TOTAL_BALANCE).equals("")) {
                            totalBalance = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_TOTAL_BALANCE);
                        }
                        DecimalFormat df = new DecimalFormat("0.00000000");
                        edtNetworkFee.setText(getResources().getString(R.string.send_network_fee) + " " + df.format(BigDecimal.valueOf(networkFee))+ " BTC");
                        if (Double.valueOf(totalBalance) - networkFee <= 0) {
                            edtMinusFee.setText(getResources().getString(R.string.balance_minus_fee) + " 0 BTC");
                        } else {
                            edtMinusFee.setText(getResources().getString(R.string.balance_minus_fee) + " " + df.format(BigDecimal.valueOf(Double.valueOf(totalBalance) - networkFee)) + " BTC");
                        }
                        //                        progress_dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                proBar.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
//                progress_dialog.dismiss();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        WalletApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RESULT_SENT).equals("")) {
            String result = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RESULT_SENT);
            recipent.setText(result);
            edtMinusFee.setVisibility(View.VISIBLE);
            edtNetworkFee.setVisibility(View.GONE);
            edtMinusFee.setText(getResources().getString(R.string.min_withdrawal) + " 0.000010 BTC");
            edtMinusFee.setTextColor(getResources().getColor(R.color.red));

        }

//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
//            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//            String pasteText = clipboard.getText().toString();
//            recipent.setText(pasteText);
//        } else {
//            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//            if (clipboard.hasPrimaryClip() == true) {
//                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
//                if (item.getText() != null) {
//                    String pasteText = item.getText().toString();
//                    recipent.setText(pasteText);
//                }
//            } else {
//                Toast.makeText(getApplicationContext(), "Nothing to Paste", Toast.LENGTH_SHORT).show();
//
//            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                recipent.setText(result.split("[?]")[0]);
                edtMinusFee.setVisibility(View.VISIBLE);
                edtNetworkFee.setVisibility(View.GONE);
                edtMinusFee.setText(getResources().getString(R.string.min_withdrawal) + " 0.000010 BTC");
                edtMinusFee.setTextColor(getResources().getColor(R.color.red));

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
//                Toast.makeText(getApplicationContext(), "User cancel scanning", Toast.LENGTH_SHORT).show();
                Snackbar.make(getWindow().getDecorView().getRootView(), "User cancel scanning", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        }
    }//onActivityResult


    public void sendBtc(String reciveraddress, String value, String memo) {
//        String url = "http://veggierating.kundaliniyoga.us/vegirating2/php-client/blockcypher/php-client/sample/transaction-api/CreateTransaction.php?sender_address="+address+"&reciever_address="+reciveraddress+"&Satoshis="+value;
//        System.out.println(url);
//        new Networking(SendBtcActivity.this,networkCallBack).doexecutePOSTWITHOUTHEADER(url);
        String path = "http://128.199.129.208/api/wallet/spend";
        System.out.println(path);
        progress_dialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        progress_dialog.show();
        String apiToken = "";
        if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN).equals("")) {
            apiToken = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_token", apiToken);
        params.put("amount", value);
        params.put("address", reciveraddress);
        params.put("memo", memo);

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
                            SendBTCListener.getIntance().getBalance();
//                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                            Snackbar.make(getWindow().getDecorView().getRootView(), "Success", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            String msg = "Something went wrong , Please try later";
                            int code = 0;
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                msg = ojb.getString("message");
                                code = ojb.getInt("code");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (code == 21) {
                                double networkFee = 0;
                                try {
                                    JSONObject ojb = response.getJSONObject("data");
                                    networkFee = ojb.getDouble("network_fee");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Snackbar.make(getWindow().getDecorView().getRootView(), "Insufficient funds", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            } else {
//                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        WalletApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void refreshBtc() {
        String path = "http://128.199.129.208/api/wallet/balance";
        progress_dialog = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        progress_dialog.show();
        String apiToken = "";
        if (!new PrefManager(getApplicationContext().getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN).equals("")) {
            apiToken = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_token", apiToken);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                path, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        DecimalFormat df = new DecimalFormat("0.00000000");

                        try {
                            message = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (message.equals("SUCCESS")) {
//                            Toast.makeText(getActivity().getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                JSONObject address = ojb.getJSONObject("address");
                                Double balance = Double.valueOf(address.getString("available_balance"));
                                Double balancePending = Double.valueOf(address.getString("pending_received_balance"));
                                totalBalance = BigDecimal.valueOf(balance + balancePending);
                                PrefManager prefManager = new PrefManager(getApplicationContext());
                                prefManager.setPref(PrefManager.KEY_TOTAL_BALANCE, String.valueOf(totalBalance));
                                tvBalance.setText(df.format(totalBalance) + " BTC");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getCurencyRate();
                        } else {
                            String msg = "Something went wrong , Please try later";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                msg = ojb.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Toast.makeText(getApplicationContext().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            progress_dialog.dismiss();
                        }
//                        progress_dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext().getApplicationContext(), "Can't connect to server!", Toast.LENGTH_LONG).show();
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
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        WalletApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void getCurencyRate() {
        String path = "https://api.coindesk.com/v1/bpi/currentprice/JPY.json";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                path, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        try {
                            JSONObject jsonObject = response.getJSONObject("bpi");
                            JSONObject jsoJPY = jsonObject.getJSONObject("JPY");
                            Constant.CURRENCY_JPY = Double.valueOf(jsoJPY.getString("rate_float"));
                            DecimalFormat df = new DecimalFormat("0");

                            if (jsoJPY.getString("rate_float") == null || jsoJPY.getString("rate_float").equals("")) {
                                getCurencyRate();
                                return;
                            }
                            curency = Double.valueOf(jsoJPY.getString("rate_float")) * Double.valueOf(String.valueOf(totalBalance));
                            tvJPYBalance.setText("￥ " + df.format(curency));
                        } catch (JSONException e) {
                            e.printStackTrace();
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
