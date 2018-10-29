package com.ivan.fgwallet;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.utils.Constant;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.ivan.fgwallet.MainActivity.bytesToHex;
import static com.ivan.fgwallet.MainActivity.savePinToReg;


public class EnterRecoveryPhraseActivity extends AppCompatActivity {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    String strRecovery = "";
    String strTimestamp = "";

    TextView btnNext;
    TextView progressView;
    EditText edtRecovery;
    EditText editPhrase;
    EditText editTimestamp;

    private WalletApplication application;
   // private Wallet wallet;



    private void init() {
        btnNext = findViewById(R.id.btn_next);
        editPhrase = findViewById(R.id.edt_recovery);
        editTimestamp = findViewById(R.id.edt_timestamp);
        progressView = findViewById(R.id.download_progres);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_recovery_phrase);
        getSupportActionBar().hide();

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
        textView.setText(getResources().getString(R.string.recovery_phrase));
        btnNext.setVisibility(View.VISIBLE);



        class CatTask extends AsyncTask<String, String, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressView.setText("Please wait...");
            }

            @Override
            protected Void doInBackground(String... params) {
                try {


                    String seedCode = params[0];
                    String creationtime_str = params[1].replaceAll("[^\\d.]", "").replace("\n","");
                    long creationtime = Long.parseLong(creationtime_str, 10);

                   // long creationtime = 1519725294L;
                    DeterministicSeed seed = new DeterministicSeed(seedCode, null, "", creationtime);
                    NetworkParameters paramsNet = MainNetParams.get();
                    Wallet wallet = Wallet.fromSeed(paramsNet, seed);

                    // Because we are importing an existing wallet which might already have transactions we must re-download the blockchain to make the wallet picks up these transactions
                    // You can find some information about this in the guides: https://bitcoinj.github.io/working-with-the-wallet#setup
                    // To do this we clear the transactions of the wallet and delete a possible existing blockchain file before we download the blockchain again further down.

                    System.out.println(wallet.toString());
                    wallet.clearTransactions(0);
                    File chainFile = new File(getApplication().getFilesDir(), "restore-from-seed.spvchain");

                    if (chainFile.exists()) {
                        chainFile.delete();
                    }

                    // Setting up the BlochChain, the BlocksStore and connecting to the network.
                    SPVBlockStore chainStore = new SPVBlockStore(paramsNet, chainFile);
                    BlockChain chain = new BlockChain(paramsNet, chainStore);
                    PeerGroup peerGroup = new PeerGroup(paramsNet, chain);
                    peerGroup.addPeerDiscovery(new DnsDiscovery(paramsNet));

                    // Now we need to hook the wallet up to the blockchain and the peers. This registers event listeners that notify our wallet about new transactions.
                    chain.addWallet(wallet);
                    peerGroup.addWallet(wallet);

                    DownloadProgressTracker bListener = new DownloadProgressTracker()
                    {
                        @Override
                        public void doneDownload() {
                            System.out.println("blockchain downloaded");
                        }
                                @Override
                                public void progress(double pct, int blocksSoFar, Date date) {
                                    publishProgress(String.format(Locale.US, "Recovery %d%%", (int) pct));
                                }
                    };
                    // Now we re-download the blockchain. This replays the chain into the wallet. Once this is completed our wallet should know of all its transactions and print the correct balance.

                    peerGroup.start();
                    peerGroup.startBlockChainDownload(bListener);

                    bListener.await();

                    // Print a debug message with the details about the wallet. The correct balance should now be displayed.
                    System.out.println(wallet.toString());




                    // shutting down again
                    peerGroup.stop();


                     application = (WalletApplication) getApplication();

                    application.wallet=wallet;
                    application.saveWallet();
                    application.backupWallet();




                } catch (UnreadableWalletException | BlockStoreException |InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);


                setContentView(R.layout.activity_new_wallet_activtiy);
                TextView bt_pinconfirm = findViewById(R.id.bt_restoreWallet);
              //  TextView terms  = findViewById(R.id.terms);
             //   terms.setVisibility(View.GONE);
                bt_pinconfirm.setText("OK");

                final EditText _newPin = findViewById(R.id.newPin);
                final EditText  _reenterPin = findViewById(R.id.reenterPin);


                bt_pinconfirm.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View view) {

                                                         if (_newPin.getText().toString().equals("")) {
//                    _newPin.setError("Please enter new pin");
                                                             Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter new pin", Snackbar.LENGTH_LONG)
                                                                     .setAction("Action", null).show();
                                                         } else if (_reenterPin.getText().toString().equals("")) {
//                    _reenterPin.setError("Please enter new pin");
                                                             Snackbar.make(getWindow().getDecorView().getRootView(), "Please enter new pin", Snackbar.LENGTH_LONG)
                                                                     .setAction("Action", null).show();
                                                         } else if (!_reenterPin.getText().toString().equals(_newPin.getText().toString())) {
//                    _reenterPin.setError("Your pin not match");
                                                             Snackbar.make(getWindow().getDecorView().getRootView(), "Your pin not match", Snackbar.LENGTH_LONG)
                                                                     .setAction("Action", null).show();
                                                         }
                                                         else {

                                                             //savePin to reg

                                                             try{
                                                                 //конвертация введенного пина в SHA-256
                                                                 MessageDigest digest = MessageDigest.getInstance("SHA-256");
                                                                 byte[] hash = digest.digest(_newPin.getText().toString().getBytes(StandardCharsets.UTF_8));
                                                                 String entered_pin_sha256 = bytesToHex(hash);

                                                                 savePinToReg(entered_pin_sha256);
                                                             }
                                                             catch (NoSuchAlgorithmException e) {
                                                                 e.printStackTrace();
                                                             }

                                                             Intent intent = new Intent(EnterRecoveryPhraseActivity.this, HomeActivity.class);
                                                             startActivity(intent);

                                                         }
                                                     }
                                                 }
                );
            }

            @Override
            protected void onProgressUpdate(String... value) {
                super.onProgressUpdate(value);
                progressView.setText(value[0]);
            }
        }


        btnNext.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {



                    strRecovery = editPhrase.getText().toString();
                    strTimestamp = editTimestamp.getText().toString();
                    if(strRecovery.isEmpty() || strTimestamp.isEmpty())
                        showNoConnect("All fields must be filled");
                    else {

                        //проверяем фразу. должно быть 12 слов и не быть цифр
                        String[] stringsRec = strRecovery.split(" ");
                        if(stringsRec.length !=12 || strRecovery.contains("0")
                                || strRecovery.contains("1") || strRecovery.contains("2")
                                || strRecovery.contains("3") || strRecovery.contains("4")
                                || strRecovery.contains("5") || strRecovery.contains("6")
                                || strRecovery.contains("7") || strRecovery.contains("8")
                                || strRecovery.contains("9") )
                            showNoConnect("Wrong phrase");
                        else {
                            long creationtime = Long.parseLong(strTimestamp, 10);
                            if (creationtime < 1231006505 || strTimestamp.length() != 10)
                                showNoConnect("Wrong timestamp");
                            else {



                                btnNext.setEnabled(false);
                                editPhrase.setEnabled(false);
                                editTimestamp.setEnabled(false);


                                CatTask catTask = new CatTask();
                                catTask.execute(strRecovery, strTimestamp);
                            }

                        }

                    }            }
        });
    }




    private void showNoConnect(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(str)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                return;
                            }
                        }).create().show();
    }
}
