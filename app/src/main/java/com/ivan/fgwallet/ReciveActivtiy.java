package com.ivan.fgwallet;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.ivan.fgwallet.WalletApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.interfaces.NetworkCallBack;
import com.ivan.fgwallet.listener.CreateAddressListener;
import com.ivan.fgwallet.listener.SendBTCListener;
import com.ivan.fgwallet.utils.MyClipboardManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReciveActivtiy extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;

    String address;
    PrefManager prefManager;

    TextView btn_address;
    ImageView qrimage;
    TextView tv_address;

    private void init() {
        btn_address = findViewById(R.id.btn_address);
        qrimage = findViewById(R.id.qrimage);
        tv_address = findViewById(R.id.address);
    }

    public void changeAddress() {
        if(!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_ADDRESS).equals("")){
            address = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_ADDRESS);
        }
        tv_address.setText(address);
        qrimage.setImageBitmap(creatQrCode("address",address));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recive_activtiy);
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
        textView.setText(getResources().getString(R.string.receive));
        changeAddress();
        prefManager = new PrefManager(ReciveActivtiy.this);

        btn_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddress();
            }
        });
        tv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyClipboardManager().copyToClipboard(getApplicationContext(),address);
//                Toast.makeText(getApplicationContext(),"Address Copied",Toast.LENGTH_SHORT).show();
                Snackbar.make(getWindow().getDecorView().getRootView(), "Address Copied", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });
        findViewById(R.id.copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyClipboardManager().copyToClipboard(getApplicationContext(),address);
                Snackbar.make(getWindow().getDecorView().getRootView(), "Address Copied", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();            }
        });
    }


    public static Bitmap creatQrCode(String tag, String data)
    {

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = null;
        try {
            matrix = writer.encode(data, BarcodeFormat.QR_CODE, 150, 150);
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
        Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < 150; x++){
            for (int y = 0; y < 150; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

    public  void createAddress(){
        String path  = "http://128.199.129.208/api/wallet/new_address";
        HashMap<String, String> params = new HashMap<String, String>();
        progress_dialog =   KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        progress_dialog.show();
        String apiToken = "";
        if(!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN).equals("")){
            apiToken = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN);
        }
        params.put("api_token", apiToken);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                path,  new JSONObject(params),
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
                            CreateAddressListener.getIntance().changeAddress();
                            SendBTCListener.getIntance().getBalance();
                            changeAddress();
//                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                            Snackbar.make(getWindow().getDecorView().getRootView(), "Success", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            String msg = "Something went wrong , Please try later";
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                msg = ojb.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();                        }
                        progress_dialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getApplicationContext(), "REPONSE ERROR", Toast.LENGTH_LONG).show();
                Snackbar.make(getWindow().getDecorView().getRootView(), "REPONSE ERROR", Snackbar.LENGTH_LONG)
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

    NetworkCallBack networkCallBack = new NetworkCallBack() {
        @Override
        public void callBack(String response) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                prefManager.setPref(PrefManager.KEY_ADDRESS,jsonObject.getString("address"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
