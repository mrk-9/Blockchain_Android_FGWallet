package com.ivan.fgwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivan.fgwallet.schildbach.wallet.ui.AbstractWalletActivity;

public class TransactionsHistoryActivity extends AbstractWalletActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_history);
        ImageView imageView = (ImageView) findViewById(R.id.menu);
        imageView.setImageResource(R.mipmap.backmenu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView textView = (TextView) findViewById(R.id.title);
     //   textView.setText(getResources().getString(R.string.contact_support));
        textView.setText(getResources().getString(R.string.transaction_history));
    }
}
