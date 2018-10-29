package com.ivan.fgwallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.common.base.Joiner;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.utils.Utils;

import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import static android.provider.Telephony.Carriers.PASSWORD;
import static java.lang.System.out;
import static java.sql.DriverManager.println;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    TextView tv_newWallet;
    TextView tv_restoreWallet;
    LinearLayout lnLanguage;
    TextView tvLanguage;

    PrefManager prefManager;

    static SharedPreferences sharedPref;

    private WalletApplication application;
    private com.ivan.fgwallet.schildbach.wallet.Configuration config;
    private Wallet wallet;

    private Handler handler = new Handler();

    @Override
    protected void onResume() {
        super.onResume();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // delayed start so that UI has enough time to initialize
                application.startBlockchainService(true);
            }
        }, 1000);
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void savePinToReg(String pin)
    {

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("fgwp", pin);
        editor.commit();

    }

    public static String getPinFromReg()
    {

       // SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);

        String pin_from_reg = sharedPref.getString("fgwp", "");
        return pin_from_reg;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (WalletApplication) getApplication();
        config = application.getConfiguration();
        wallet = application.getWallet();


        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
//        ButterKnife.inject(this);
        init();
        prefManager = new PrefManager(MainActivity.this);
        loadLanguage();



        //String entered_pin ="1234";
      //      try {

                //считываем пин из реестра
                SharedPreferences sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);

                String pin_from_reg = sharedPref.getString("fgwp", "");

        //if(!pin_from_reg.isEmpty())
                if(!pin_from_reg.isEmpty()) //если пин в реестре не пуст
                {
                    Intent intent = new Intent(MainActivity.this, EnterPinActivtiy.class);

                    intent.putExtra("pin_from_reg", pin_from_reg);
                    startActivity(intent);
                    finish();
                }
                else { //если еще нет пина в реесте
                    // если нет еще пина=====================
                    //новый кош
                    tv_newWallet.setOnClickListener(new View.OnClickListener() {
                        //создать новый кошелек
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(), NewWalletActivtiy.class));
                        }
                    });

                    //восстановить кош
                    tv_restoreWallet.setOnClickListener(new View.OnClickListener() {
                        //восстановить кошелек
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getApplicationContext(), EnterRecoveryPhraseActivity.class));
                        }
                    });
                }

/*  РАБЮОТАЕТ!!!!!!!!
                    //конвертация введенного пина в SHA-256

                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(entered_pin.getBytes(StandardCharsets.UTF_8));
                    String entered_pin_sha256 = bytesToHex(hash);

                    //если пин из реестра пуст==
                    if (pin_from_reg.isEmpty()) {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("fgwp", entered_pin_sha256);
                        editor.commit();
                    } else {
                        startActivity(new Intent(getApplicationContext(), EnterPinActivtiy.class));
                        finish();
                        //if(entered_pin_sha256==pin_from_reg) {
                        //int a = 0; //запускаем homeActivity
                    }
                }
                */
/*
            }
            catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
*/



  //      DeterministicSeed seed = wallet.getKeyChainSeed();
  //      String seedCode = Joiner.on(" ").join(seed.getMnemonicCode());

 //       println("Seed words are: " + Joiner.on(" ").join(seed.getMnemonicCode()));
 //       println("Seed birthday is: " + seed.getCreationTimeSeconds());
/*
        String seedCode = "yard impulse luxury drive today throw farm pepper survey wreck glass federal";
        long creationtime = 1409478661L;
        DeterministicSeed seed = new DeterministicSeed(seedCode, null, "", creationtime);
        Wallet restoredWallet = Wallet.fromSeed(params, seed);
 */
// now sync the restored wallet as described below.





        //если телефон не равен нулю и пин не равен нулю=============

    //    if ( !prefManager.getpref(PrefManager.KEY_PIN).equals(""))
      //  {
            //ввод пина
      //      startActivity(new Intent(getApplicationContext(), EnterPinActivtiy.class));
      //      finish();
      //  }




        lnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });
    }

    private void init() {
        tv_newWallet = findViewById(R.id.tv_newWallet);
        tv_restoreWallet = findViewById(R.id.tv_restoreWallet);
        lnLanguage = findViewById(R.id.ln_language);
        tvLanguage = findViewById(R.id.tv_language);
        sharedPref = MainActivity.this.getPreferences(Context.MODE_PRIVATE);
    }

    public void loadLanguage() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "en");
        if (language.equals("en")) {
            tvLanguage.setText("English");
        } else {
            tvLanguage.setText("日本語");
        }
    }

    private void showChangeLanguageDialog() {
        AlertDialog.Builder dialogError = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_language, null);
        dialogError.setView(dialogView);
        dialogError.setCancelable(false);
        // set the custom dialog components - text, image and button
        TextView tvEnglish = (TextView) dialogView.findViewById(R.id.tv_english);
        TextView tvJapan = (TextView) dialogView.findViewById(R.id.tv_japan);
        final AlertDialog dialog = dialogError.show();
        tvEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(getApplicationContext(), "en");
            }
        });
        tvJapan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLanguage(getApplicationContext(), "ja");

            }
        });
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




    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";




}
