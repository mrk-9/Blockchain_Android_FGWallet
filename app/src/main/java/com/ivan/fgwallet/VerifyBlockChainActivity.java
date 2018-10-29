package com.ivan.fgwallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivan.fgwallet.helper.PrefManager;


public class VerifyBlockChainActivity extends AppCompatActivity {
    WebView webView;

    private void init() {
        webView = findViewById(R.id.web_view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
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
        textView.setText(getResources().getString(R.string.verify_blockchain));
        String address = "";
        if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_ADDRESS).equals("")) {
            address = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_ADDRESS);
        }

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.loadUrl("https://live.blockcypher.com/btc/address/" + address);
    }

}
