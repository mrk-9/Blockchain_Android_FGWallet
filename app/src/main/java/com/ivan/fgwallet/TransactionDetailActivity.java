package com.ivan.fgwallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.model.History;
import com.ivan.fgwallet.utils.Utils;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.color.holo_green_light;
import static android.R.color.holo_red_light;


public class TransactionDetailActivity extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    History history;

    TextView tvReveicedSent;
    TextView tvAmount;
    TextView tvValue;
    TextView tvTransactionFee;
    TextView tvMemo;
    TextView tvTo;
    TextView tvFrom;
    TextView tvDate;
    TextView btnVerify;

    private void init() {
        tvReveicedSent = findViewById(R.id.tv_reveiced_sent);
        tvAmount = findViewById(R.id.tv_amount);
        tvValue = findViewById(R.id.tv_value);
        tvTransactionFee = findViewById(R.id.tv_transaction_fee);
        tvMemo = findViewById(R.id.tv_memo);
        tvTo = findViewById(R.id.tv_to);
        tvFrom = findViewById(R.id.tv_from);
        tvDate = findViewById(R.id.tv_date);
        btnVerify = findViewById(R.id.btn_verify);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);
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
        textView.setText(getResources().getString(R.string.transaction));
        Intent intent = getIntent();
        history = (History) intent.getExtras().getSerializable("HISTORY_ITEM");

        tvReveicedSent.setText(Utils.upCaseFirst(history.getType()));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        try {
            date = dateFormat.parse(history.getTime().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        tvDate.setText(dateFormat.format(date));
        tvDate.setText(history.getTime().toString());
        tvAmount.setText(history.getAmount().toString() + " BTC");
        if (history.getMemo() != null && !history.getMemo().equals("")) {
            tvMemo.setText(history.getMemo());
        }
        if (Utils.upCaseFirst(history.getType()).equals("Received")) {
            tvReveicedSent.setTextColor(getResources().getColor(holo_green_light));
            tvReveicedSent.setText(getResources().getString(R.string.received));
            tvAmount.setTextColor(getResources().getColor(holo_green_light));
            tvFrom.setText(history.getAddresses());
        } else {
            tvReveicedSent.setTextColor(getResources().getColor(holo_red_light));
            tvReveicedSent.setText(getResources().getString(R.string.sent));
            tvAmount.setTextColor(getResources().getColor(holo_red_light));
            tvTo.setText(history.getAddresses());
        }

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(TransactionDetailActivity.this, VerifyBlockChainActivity.class));
                String address = "";
                if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_ADDRESS).equals("")) {
                    address = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_ADDRESS);
                }
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://live.blockcypher.com/btc/address/" + address)));
            }
        });
    }

}
