package com.ivan.fgwallet.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivan.fgwallet.InvestmentActivtiy;
import com.ivan.fgwallet.R;
import com.ivan.fgwallet.WithdrawActivtiy;


public class DepositAccount extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_deposit_account, container, false);
        rootView.findViewById(R.id.invest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), InvestmentActivtiy.class));
            }
        });
        rootView.findViewById(R.id.withdraw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), WithdrawActivtiy.class));
            }
        });
        return rootView;
    }

}
