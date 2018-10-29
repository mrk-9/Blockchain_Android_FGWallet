package com.ivan.fgwallet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ivan.fgwallet.WalletApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import com.ivan.fgwallet.HomeActivity;
import com.ivan.fgwallet.R;
import com.ivan.fgwallet.RestoreWalletActivtiy;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.interfaces.NetworkCallBack;
import com.ivan.fgwallet.utils.Constant;
import com.google.android.gms.internal.in;
import com.hbb20.CountryCodePicker;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.ivan.fgwallet.MainActivity.bytesToHex;
import static com.ivan.fgwallet.MainActivity.getPinFromReg;
import static com.ivan.fgwallet.MainActivity.savePinToReg;

public class ChangePasswordActivtiy extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;

    EditText edtOldPin;
    EditText edtNewPin;
    EditText reEnterNewPin;
    TextView btnChangePassword;

    private void init() {
        edtOldPin = findViewById(R.id.old_pin);
        edtNewPin = findViewById(R.id.new_pin);
        reEnterNewPin = findViewById(R.id.re_enter_new_pin);
        btnChangePassword = findViewById(R.id.btn_change_password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_activtiy);
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
        textView.setText(getResources().getString(R.string.change_password));
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //close keyboard
                View curView = getCurrentFocus();
                if (curView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(curView.getWindowToken(), 0);
                }
                // startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                String OldPin = edtOldPin.getText().toString();
                if (edtOldPin.getText().toString().equals(""))
                {
                    showNoConnect("Please enter old pin");

                } else if (edtNewPin.getText().toString().equals(""))
                {
//                    edtNewPin.setError("Please enter new pin");
                    showNoConnect("Please enter new pin");

                } else if (reEnterNewPin.getText().toString().equals("")) {
//                    reEnterNewPin.setError("Please re-enter new pin");
                    showNoConnect("Please re-enter new pin");

                } else if (!edtNewPin.getText().toString().equals(reEnterNewPin.getText().toString())) {
//                    reEnterNewPin.setError("Your pin not match");
                    showNoConnect("Pins don't match");

                } else {
                    changePassword();
                }
            }
        });
    }

    public void changePassword()
    {

        //проверяем старый ппин======
        ///считываем пин из реестра

        String pin_from_reg = getPinFromReg();
        String oldPin = edtOldPin.getText().toString();
        String newPin = edtNewPin.getText().toString();

        try{ //это будет вызываться после первого удачного входа в приложение====
            //конвертация введенного пина в SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(oldPin.getBytes(StandardCharsets.UTF_8));
            String entered_pin_sha256 = bytesToHex(hash);

            if(pin_from_reg.equals(entered_pin_sha256)) //соответсвует===
            {
                hash = digest.digest(newPin.getBytes(StandardCharsets.UTF_8));
                entered_pin_sha256 = bytesToHex(hash);
                //сохранение пина в реестр
                savePinToReg(entered_pin_sha256);
                showNoConnect("Change PIN success");
             }
             else
                showNoConnect("Wrong old pin");

        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    private void showNoConnect(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str)
                .setCancelable(false);

        if (str.equals("Change PIN success")) {
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            finish();
                        }
                    }).create().show();

        }
        else {
                     builder.setPositiveButton("OK",
                     new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            return;
                        }
                    }).create().show();
        }


    }
}
