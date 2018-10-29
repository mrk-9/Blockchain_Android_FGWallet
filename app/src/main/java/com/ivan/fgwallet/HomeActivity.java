package com.ivan.fgwallet;

import android.content.Intent;
import android.os.Handler;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ivan.fgwallet.Adapter.ExpandListAdapter;
import com.ivan.fgwallet.Fragments.AtmMapFragment;
import com.ivan.fgwallet.Fragments.MapChartFragment;
import com.ivan.fgwallet.Fragments.SettingFragment;
import com.ivan.fgwallet.Fragments.TransactionHistoryFragment;
import com.ivan.fgwallet.Fragments.WalletMenuFragment;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.schildbach.wallet.Configuration;
import com.ivan.fgwallet.schildbach.wallet.ui.AbstractWalletActivity;
import com.ivan.fgwallet.schildbach.wallet.ui.NetworkMonitorActivity;
import com.ivan.fgwallet.WalletApplication;


import org.bitcoinj.wallet.Wallet;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//public class HomeActivity extends AppCompatActivity {

public class HomeActivity extends AppCompatActivity {

    private WalletApplication application;
    private Configuration config;
    private Wallet wallet;

    private Handler handler = new Handler();
    ArrayList<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ExpandableListView expListView;
    ExpandListAdapter listAdapter;
    Fragment fragment;
    DrawerLayout drawer_layout;
    TextView title;
    ImageView imgTitle;
    private static List<HomeActivity> instances = new ArrayList<HomeActivity>();



    @Override
    public void onDestroy() {
        super.onDestroy();
        instances.remove(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        instances.remove(this);
    }

    public static List<HomeActivity> getInstances() {
        return instances;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        instances.add(this);


        setContentView(R.layout.activity_home);
        // getSupportActionBar().hide();
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.openDrawer(Gravity.LEFT);
            }
        });
        enableExpandableList();
        fragment = new WalletMenuFragment();
        title.setText("FG Wallet");
        imgTitle.setVisibility(View.VISIBLE);
        title.setVisibility(View.GONE);
        setFragment(fragment);
    }

    private void enableExpandableList() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        expListView = (ExpandableListView) findViewById(R.id.left_drawer);
        title = (TextView) findViewById(R.id.title);
        imgTitle = (ImageView) findViewById(R.id.img_title);
        prepareListData(listDataHeader, listDataChild);
        listAdapter = new ExpandListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);




        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {


            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                System.out.println(groupPosition);

                switch (groupPosition) {
                    case 0:
                        imgTitle.setVisibility(View.VISIBLE);
                        title.setVisibility(View.GONE);
                        fragment = new WalletMenuFragment();
                        title.setText("FG Wallet");
                        setFragment(fragment);
                        drawer_layout.closeDrawer(Gravity.LEFT);
                        break;


                    case 1:
                        startActivity(new Intent(HomeActivity.this, TransactionsHistoryActivity.class));
                        break;

                    case 2:
                        imgTitle.setVisibility(View.GONE);
                        title.setVisibility(View.VISIBLE);
                        fragment = new MapChartFragment();
                        title.setText(getResources().getString(R.string.market_chart));
                        setFragment(fragment);
                        drawer_layout.closeDrawer(Gravity.LEFT);
                        break;
//
                    case 3:
                        startActivity(new Intent(HomeActivity.this, ATMmapActivity.class));
                        drawer_layout.closeDrawer(Gravity.LEFT);
                        break;



                    case 4:
                        imgTitle.setVisibility(View.GONE);
                        title.setVisibility(View.VISIBLE);
                        fragment = new SettingFragment();
                        title.setText(getResources().getString(R.string.settings));
                        setFragment(fragment);
                        drawer_layout.closeDrawer(Gravity.LEFT);
                        break;
                    case 5:
                        startActivity(new Intent(HomeActivity.this, ContactSupportActivity.class));
                        drawer_layout.closeDrawer(Gravity.LEFT);
                        break;
                    case 6:

                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);

                        break;
                }

                return false;
            }
        });
        // Listview Group expanded listener

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                // Temporary code:
                drawer_layout.closeDrawer(Gravity.LEFT);
                switch (childPosition) {


                }


                return false;
            }
        });
    }

    private void prepareListData(List<String> listDataHeader, Map<String,
            List<String>> listDataChild) {


        // Adding child data
        listDataHeader.add("FG Wallet");

        listDataHeader.add(getResources().getString(R.string.transaction_history));
        listDataHeader.add(getResources().getString(R.string.market_chart));

        listDataHeader.add(getResources().getString(R.string.atm_map));

        listDataHeader.add(getResources().getString(R.string.settings));
        listDataHeader.add(getResources().getString(R.string.contact_support));
        listDataHeader.add(getResources().getString(R.string.log_out));
        // Adding child data
        List<String> top = new ArrayList<String>();
        List<String> mid = new ArrayList<String>();

        listDataChild.put(listDataHeader.get(0), top); // Header, Child data
        listDataChild.put(listDataHeader.get(1), top);
        listDataChild.put(listDataHeader.get(2), top);
        listDataChild.put(listDataHeader.get(3), mid);
        listDataChild.put(listDataHeader.get(4), top);
        listDataChild.put(listDataHeader.get(5), top);
        listDataChild.put(listDataHeader.get(6), top);

    }


    public void setFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();

    }

    public void setTitle(String strTtitle) {
        title.setText(strTtitle);
        if (strTtitle.equals("FG Wallet")) {
            imgTitle.setVisibility(View.VISIBLE);
            title.setVisibility(View.GONE);
        } else {
            imgTitle.setVisibility(View.GONE);
            title.setVisibility(View.VISIBLE);
        }
    }
}
