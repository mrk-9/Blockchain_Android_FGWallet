package com.ivan.fgwallet.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.ivan.fgwallet.R;
import com.ivan.fgwallet.listener.ChangeTitleListener;

public class RegisterMyAccountFragment extends Fragment {
    EditText edtAccountNumber;
    EditText edtName;
    EditText edtEmail;
    EditText edtMobilePhone;
    EditText edtBirthday;
    private void init(View rootView) {
        edtAccountNumber = rootView.findViewById(R.id.account_number);
        edtName = rootView.findViewById(R.id.name);
        edtEmail = rootView.findViewById(R.id.email);
        edtMobilePhone = rootView.findViewById(R.id.mobile_phone);
        edtBirthday = rootView.findViewById(R.id.birthday);
    }
    @Override
    public void onResume() {
        super.onResume();
        ChangeTitleListener.getIntance().setTitle("Register My Net Banking");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView  = inflater.inflate(R.layout.fragment_register_my_account, container, false);
//        ButterKnife.inject(this,rootView);
        init(rootView);
        rootView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailSupport();
            }
        });
        return rootView;
    }

    private void emailSupport() {
        String email = "exc@srsfc.com";
        String body = "EXC Account Number: " + edtAccountNumber.getText().toString() + "\n" +
                "Name: " + edtName.getText().toString() + "\n" +
                "Email Address: " + edtEmail.getText().toString() + "\n" +
                "Mobile Phone: " + edtMobilePhone.getText().toString() + "\n" +
                "Date of Birth: " + edtBirthday.getText().toString() + "\n";
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:" + email));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Register My Net Banking");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
//        Utils.sendEmail(getActivity(), "Apply for Debit Card", body, new ArrayList<Uri>());

    }
}
