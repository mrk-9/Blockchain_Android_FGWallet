package com.ivan.fgwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ivan.fgwallet.interfaces.NetworkCallBack;
import com.ivan.fgwallet.networking.Networking;

import java.util.ArrayList;

public class InvestmentActivtiy extends AppCompatActivity {

    ArrayList<String> itemsList = new ArrayList<>();
    Spinner spn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investment_activtiy);
        getSupportActionBar().hide();

        ImageView  imageView = (ImageView) findViewById(R.id.menu);
        imageView.setImageResource(R.mipmap.backmenu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText("Invest");
        spn = (Spinner) findViewById(R.id.spn);
        itemsList.add("$1,000    1 Year Max 10%");
        itemsList.add("$3,000    3 Year Max 15%");
        itemsList.add("$5,000    5 Year Max 20%");
        ArrayAdapter<String> adapter = new ArrayAdapter<String> ( this, R.layout.spn_item,R.id.text, itemsList ) ;
        spn.setAdapter(adapter);

        findViewById(R.id.invest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBalance();
            }
        });

    }

    public  void getBalance(){
        new Networking(InvestmentActivtiy.this,networkCallBack).doexecuteGETWITHOUTHEADER("http://veggierating.kundaliniyoga.us/vegirating2/php-client/blockcypher/php-client/sample/address-api/GetAddress.php?address=");
    }

    NetworkCallBack networkCallBack = new NetworkCallBack() {
        @Override
        public void callBack(String response) {
            Toast.makeText(InvestmentActivtiy.this,"You dont have balance to invest",Toast.LENGTH_SHORT).show();

        }
    };

}
