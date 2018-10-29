package com.ivan.fgwallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;

    RadioButton rdEnglish;
    RadioButton rdJapan;
    TextView tvOk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language_activtiy);
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
        textView.setText(getResources().getString(R.string.change_language));
        rdEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdEnglish.setChecked(true);
            }
        });
        rdJapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rdJapan.setChecked(true);
            }
        });
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rdEnglish.isChecked()) {
                    updateLanguage(getApplicationContext(), "en");
                } else {
                    updateLanguage(getApplicationContext(), "ja");
                }
            }
        });
    }
    private void init() {
        rdEnglish = findViewById(R.id.rd_english);
        rdJapan = findViewById(R.id.rd_japan);
        tvOk = findViewById(R.id.btn_ok);
    }
    public void updateLanguage(Context ctx, String lang) {
        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        ctx.getResources().updateConfiguration(cfg, null);
        saveLocale(lang);
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();

        SharedPreferences mPrefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.commit();
    }
}
