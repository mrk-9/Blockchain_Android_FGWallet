package com.ivan.fgwallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.ivan.fgwallet.helper.PrefManager;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;

import java.util.Objects;

public class RecoveryPhraseSettingActivity2 extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;

    TextView tvTitle;
    TextView timestamp_creation;
    private WalletApplication application;
    private Wallet wallet;

    private void init() {
        tvTitle = findViewById(R.id.tv_title_recovery);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_setting2);
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
        TextView timestamp_creation = (TextView) findViewById(R.id.timestamp_creation);
        textView.setText("");

        application = (WalletApplication) getApplication();
        this.wallet = application.getWallet();

        DeterministicSeed seed = wallet.getKeyChainSeed();
        String strRecovery = Joiner.on(" ").join(seed.getMnemonicCode());

        String timestampCreation= Objects.toString(seed.getCreationTimeSeconds(),null);


    //    if(!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RECOVERY_PHRASE).equals("")){
     //       strRecovery = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RECOVERY_PHRASE);
      //  }
        tvTitle.setText(strRecovery);
        timestamp_creation.setText("Timestamp creation: "+timestampCreation);
    }
}
