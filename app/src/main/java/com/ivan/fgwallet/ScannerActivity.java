package com.ivan.fgwallet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    private static final int MY_CAMERA_REQUEST_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
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
        textView.setText("Scanner");
        mScannerView = (ZXingScannerView)findViewById(R.id.scanner);

    }



    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(ScannerActivity.this, new String[] {android.Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);

        }
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            } else {

                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();

            }

        }
    }

    @Override
    public void handleResult(Result rawResult) {
        /*
        Toast.makeText(getActivity(), "Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat(), Toast.LENGTH_SHORT).show();
        */
        if ( rawResult != null && rawResult.getText().equals("")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScannerView.resumeCameraPreview(ScannerActivity.this);
                }
            }, 2000);
        }else{
            String result[];
            if(rawResult.getText().contains(":")){
                result = rawResult.getText().split(":");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",result[1]);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }else{
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",rawResult.getText());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }



}
