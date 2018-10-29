package com.ivan.fgwallet.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivan.fgwallet.AboutActivity;
import com.ivan.fgwallet.ChangeLanguageActivity;
import com.ivan.fgwallet.ChangePasswordActivtiy;
import com.ivan.fgwallet.R;
import com.ivan.fgwallet.RecoveryPhraseSettingActivity;
import com.ivan.fgwallet.listener.ChangeTitleListener;

public class SettingFragment extends Fragment {
    TextView tvAbout;
    TextView tvRecovery;
    TextView tvChangePassword;
    TextView tv_Rescan;
    TextView tvChangeLanguage;

    private void init(View rootView) {
        tvAbout = rootView.findViewById(R.id.tv_about);
        tvRecovery = rootView.findViewById(R.id.tv_recovery);
        tvChangePassword = rootView.findViewById(R.id.tv_change_password);
        tv_Rescan = rootView.findViewById(R.id.tv_rescan);
        tvChangeLanguage = rootView.findViewById(R.id.tv_change_language);
    }

    @Override
    public void onResume() {
        super.onResume();
        ChangeTitleListener.getIntance().setTitle(getResources().getString(R.string.settings));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
//        ButterKnife.inject(this, rootView);
        init(rootView);
        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AboutActivity.class));

            }
        });
        tvRecovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RecoveryPhraseSettingActivity.class);
                startActivity(intent);
            }
        });
        tvChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ChangePasswordActivtiy.class));
            }
        });
        tv_Rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               // Fragment fragment = new WalletMenuFragment();
            //    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();
               // getActivity().getFragmentManager().beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();

            }
        });
        tvChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),ChangeLanguageActivity.class));
            }
        });
        return rootView;
    }

}
