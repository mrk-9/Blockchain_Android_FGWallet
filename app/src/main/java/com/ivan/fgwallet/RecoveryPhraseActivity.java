package com.ivan.fgwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivan.fgwallet.helper.PrefManager;
import com.kaopiz.kprogresshud.KProgressHUD;

public class RecoveryPhraseActivity extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;

    TextView btnNext;
    TextView tvRecovery;

    String strRecovery = "";
    String strTimeStampCreation = "";

    TextView tvTimestampCreation;

    private void init() {
        btnNext = findViewById(R.id.btn_next);
        tvRecovery = findViewById(R.id.tv_string_recovery);
        tvTimestampCreation = findViewById(R.id.tv_creation_timestamp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_phrase);
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
        textView.setText("");

        Bundle b = getIntent().getExtras();

        if(b != null){
            strRecovery = b.getString("KEY_RECOVERY_PHRASE");
            strTimeStampCreation =  b.getString("TIMESTAMP_CREATION");
        }


        tvRecovery.setText(strRecovery);
        tvTimestampCreation.setText(strTimeStampCreation);
        final String finalStrRecovery = strRecovery;
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = getIntent().getExtras();
                String number = "", pin = "";
                if(b != null){
                    number = b.getString("number");
                    pin = b.getString("pin");


                }
                Intent intent = new Intent(RecoveryPhraseActivity.this, RecoveryPhraseActivity1.class);
                intent.putExtra("number", number);
                intent.putExtra("pin", pin);
                intent.putExtra("KEY_RECOVERY_PHRASE", strRecovery);
                intent.putExtra("TIMESTAMP_CREATION", strTimeStampCreation);

                startActivity(intent);
//                finish();
            }
        });
    }
}
