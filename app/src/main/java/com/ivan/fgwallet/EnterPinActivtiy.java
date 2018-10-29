package com.ivan.fgwallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.ivan.fgwallet.helper.PrefManager;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.ivan.fgwallet.MainActivity.bytesToHex;

public class EnterPinActivtiy extends AppCompatActivity
{
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    PrefManager prefManager;

    TextView bt_restoreWallet;
    EditText _pin;

    String entered_pin = "";
    String pin_from_reg;

    private void init() {
        bt_restoreWallet = findViewById(R.id.bt_restoreWallet);
        _pin = findViewById(R.id.pin);
        Bundle b = getIntent().getExtras();

        if (b != null) {
            pin_from_reg = b.getString("pin_from_reg");
        }


        //SharedPreferences sharedPref = EnterPinActivtiy.this.getPreferences(Context.MODE_PRIVATE);
        //pin_from_reg = sharedPref.getString("fgwp", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pin_activtiy);
        getSupportActionBar().hide();

        init();
        //======================


        //======================
        prefManager = new PrefManager(getApplicationContext());//pin = prefManager.getpref(PrefManager.KEY_PIN);
        bt_restoreWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                entered_pin = _pin.getText().toString();

                if (entered_pin.equals("")) //если ввели пустой пин
                {
//                    _pin.setError("Please enter your pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter your pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    try {
                        //конвертация введенного пина в SHA-256
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        byte[] hash = digest.digest(entered_pin.getBytes(StandardCharsets.UTF_8));
                        String entered_pin_sha256 = bytesToHex(hash);
                        if (entered_pin_sha256.equals(pin_from_reg)) {
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        } else {

                            Snackbar.make(getWindow().getDecorView().getRootView(), "Your PIN not match", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            }
        } );
    }
}


      /*
                if (_pin.getText().toString().equals("")) {
//                    _pin.setError("Please enter your pin");
                    Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter your pin", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else
                    {
                  //  if (_pin.getText().toString().equals(pin))
                        if (1>0)
                    {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                    else {
//                        _pin.setError("Your PIN not match");
                        Snackbar.make(getWindow().getDecorView().getRootView(), "Your PIN not match", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
       */
 //           }
 //       });
  //  }
//}
