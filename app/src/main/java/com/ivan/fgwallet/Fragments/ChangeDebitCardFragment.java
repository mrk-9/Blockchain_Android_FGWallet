package com.ivan.fgwallet.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivan.fgwallet.R;
import com.ivan.fgwallet.listener.ChangeTitleListener;


public class ChangeDebitCardFragment extends Fragment {

    @Override
    public void onResume() {
        super.onResume();
        ChangeTitleListener.getIntance().setTitle("Sell my coins");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_debit_card, container, false);
    }

}
