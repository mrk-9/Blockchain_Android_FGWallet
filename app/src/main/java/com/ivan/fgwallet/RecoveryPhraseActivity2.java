package com.ivan.fgwallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ivan.fgwallet.helper.PrefManager;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import static com.ivan.fgwallet.MainActivity.bytesToHex;
import static com.ivan.fgwallet.MainActivity.savePinToReg;

public class RecoveryPhraseActivity2 extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    int thSelect;
    String[] stringsRecovery;

    TextView tv1;
    TextView tv2;
    TextView tv3;
    TextView tv4;
    TextView tv5;
    TextView tv6;
    TextView tv7;
    TextView tv8;
    TextView tv9;
    TextView tv10;
    TextView tv11;
    TextView tv12;
    TextView tvTitle;
    private void init() {
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);
        tv7 = findViewById(R.id.tv7);
        tv8 = findViewById(R.id.tv8);
        tv9 = findViewById(R.id.tv9);
        tv10 = findViewById(R.id.tv10);
        tv11 = findViewById(R.id.tv11);
        tv12 = findViewById(R.id.tv12);
        tvTitle = findViewById(R.id.tv_title_select);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery_phrase1);
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

        String strRecovery = "";
 //       if (!new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RECOVERY_PHRASE).equals("")) {
  //          strRecovery = new PrefManager(getApplicationContext()).getpref(PrefManager.KEY_RECOVERY_PHRASE);
   //     }
        Bundle b = getIntent().getExtras();

        if(b != null){
            strRecovery = b.getString("KEY_RECOVERY_PHRASE");

        }
        stringsRecovery = strRecovery.split(" ");
        ArrayList<Integer> numsRandom = new ArrayList<>();
        int iNew = 0;
        Random rd = new Random();
        for (int i = 0; i < 12; ) {
            iNew = rd.nextInt(12);
            if (!numsRandom.contains(iNew) && stringsRecovery.length>iNew )
            {
                i++;
                numsRandom.add(iNew);
            }
        }

        tv1.setText(stringsRecovery[numsRandom.get(0)]);
        tv2.setText(stringsRecovery[numsRandom.get(1)]);
        tv3.setText(stringsRecovery[numsRandom.get(2)]);
        tv4.setText(stringsRecovery[numsRandom.get(3)]);
        tv5.setText(stringsRecovery[numsRandom.get(4)]);
        tv6.setText(stringsRecovery[numsRandom.get(5)]);
        tv7.setText(stringsRecovery[numsRandom.get(6)]);
        tv8.setText(stringsRecovery[numsRandom.get(7)]);
        tv9.setText(stringsRecovery[numsRandom.get(8)]);
        tv10.setText(stringsRecovery[numsRandom.get(9)]);
        tv11.setText(stringsRecovery[numsRandom.get(10)]);
        tv12.setText(stringsRecovery[numsRandom.get(11)]);


        thSelect = rd.nextInt(12);
        while (!numsRandom.contains(thSelect)) {
            thSelect = rd.nextInt(12);
        }
        if (thSelect == 0) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery1));
        } else if (thSelect == 1) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery2));
        } else if (thSelect == 2) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery3));
        } else if (thSelect == 3) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery4));
        } else if (thSelect == 4) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery5));
        } else if (thSelect == 5) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery6));
        } else if (thSelect == 6) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery7));
        } else if (thSelect == 7) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery8));
        } else if (thSelect == 8) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery9));
        } else if (thSelect == 9) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery10));
        } else if (thSelect == 10) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery11));
        } else if (thSelect == 11) {
            tvTitle.setText(getResources().getString(R.string.msg_recovery12));
        }
//        tvTitle.setText("select the " + (thSelect + 1) + "th word of your recovery phrase from the list below");

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv1.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv1.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv1.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv2.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv2.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv2.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv3.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv3.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv3.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv4.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv4.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv4.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv5.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv5.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv5.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv6.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv6.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv6.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });

        tv7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv7.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv7.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv7.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });

        tv8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv8.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv8.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv8.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });

        tv9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv9.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv9.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv9.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });

        tv10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv10.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv10.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv10.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });
        tv11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv11.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv11.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }
                            savePin(pin);
                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv11.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });

        tv12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv12.getText().toString().equals(stringsRecovery[thSelect])) {
                    tv12.setTextColor(getResources().getColor(R.color.green));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            Bundle b = getIntent().getExtras();
                            String number = "", pin = "";
                            if (b != null) {
                                number = b.getString("number");
                                pin = b.getString("pin");
                            }

                            savePin(pin);

                            Intent intent = new Intent(RecoveryPhraseActivity2.this, HomeActivity.class);
                            intent.putExtra("number", number);
                            intent.putExtra("pin", pin);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    tv12.setTextColor(getResources().getColor(R.color.red));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long l) {
                        }

                        @Override
                        public void onFinish() {
                            finish();
                        }
                    }.start();
                }
            }
        });



    }

    public void savePin(String pin){
        try{ //это будет вызываться после первого удачного входа в приложение====
            //конвертация введенного пина в SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
            String entered_pin_sha256 = bytesToHex(hash);
            //сохранение пина в реестр
            savePinToReg(entered_pin_sha256);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }try{ //это будет вызываться после первого удачного входа в приложение====
            //конвертация введенного пина в SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(pin.getBytes(StandardCharsets.UTF_8));
            String entered_pin_sha256 = bytesToHex(hash);
            //сохранение пина в реестр
            savePinToReg(entered_pin_sha256);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
