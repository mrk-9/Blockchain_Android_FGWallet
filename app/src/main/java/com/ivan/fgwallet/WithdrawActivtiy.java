package com.ivan.fgwallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.interfaces.NetworkCallBack;
import com.ivan.fgwallet.networking.Networking;

import org.json.JSONException;
import org.json.JSONObject;

public class WithdrawActivtiy extends AppCompatActivity {
    PrefManager prefManager;
    String creatAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_activtiy);
        getSupportActionBar().hide();
        ImageView imageView = (ImageView) findViewById(R.id.menu);
        imageView.setImageResource(R.mipmap.backmenu);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText("Withdraw");
        prefManager = new PrefManager(WithdrawActivtiy.this);
        if(!prefManager.getpref(PrefManager.KEY_ADDRESS).equals("")){
           // createAddress();
            creatAddress = prefManager.getpref(PrefManager.KEY_ADDRESS);
            createAddress();
        }
    }

    public  void createAddress(){
        // https://api.blockcypher.com/v1/bcy/test/addrs?token=$YOURTOKEN
        new Networking(WithdrawActivtiy.this,networkCallBack).doexecutePOSTWITHOUTHEADER("http://veggierating.kundaliniyoga.us/vegirating2/php-client/blockcypher/php-client/sample/address-api/getWithdraw.php");
    }

    NetworkCallBack networkCallBack = new NetworkCallBack() {
        @Override
        public void callBack(String response) {

            try {
                JSONObject jsonObject = new JSONObject(response);
                prefManager.setPref(PrefManager.KEY_ADDRESS,jsonObject.getString("address"));
                Toast.makeText(WithdrawActivtiy.this,"Reciving Address"+jsonObject.getString("address"),Toast.LENGTH_LONG).show();
                Toast.makeText(WithdrawActivtiy.this,"Insuficient Balance",Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };
}
