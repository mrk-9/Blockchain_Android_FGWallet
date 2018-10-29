package com.ivan.fgwallet.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ivan.fgwallet.R;
import com.ivan.fgwallet.listener.ChangeTitleListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ApplyforDebitcard extends Fragment {
    public static final int PICK_IMAGE1 = 1;
    public static final int PICK_IMAGE2 = 2;

    EditText edtName;
    EditText edtLastName;
    EditText edtBirthday;
    EditText edtEmail;
    EditText edtAddress;
    EditText edtCity;
    EditText edtPerfecture;
    EditText edtPostCode;
    EditText edtCountryCode;
    EditText edtPhone;
    EditText edtMobilePhone;
    ImageView imgDocument1;
    ImageView imgDocument2;

    Uri selectedImageURI1, selectedImageURI2;
    private void init(View rootView) {
        edtName = rootView.findViewById(R.id.name);
        edtLastName = rootView.findViewById(R.id.last_name);
        edtBirthday = rootView.findViewById(R.id.birthday);
        edtEmail = rootView.findViewById(R.id.email);
        edtAddress = rootView.findViewById(R.id.address);
        edtCity = rootView.findViewById(R.id.city);
        edtPerfecture = rootView.findViewById(R.id.perfecture);
        edtPostCode = rootView.findViewById(R.id.post_code);
        edtCountryCode = rootView.findViewById(R.id.country_code);
        edtPhone = rootView.findViewById(R.id.phone);
        edtMobilePhone = rootView.findViewById(R.id.mobile_phone);
        imgDocument1 = rootView.findViewById(R.id.img_document1);
        imgDocument2 = rootView.findViewById(R.id.img_document2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_applyfor_debitcard, container, false);
//        ButterKnife.inject(this, rootView);
        init(rootView);
        rootView.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailSupport();
            }
        });
        rootView.findViewById(R.id.img_document1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE1);
            }
        });
        rootView.findViewById(R.id.img_document2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE2);
            }
        });
        return rootView;
    }

    private void emailSupport() {
        String email = "exc@srsfc.com";
        String body = "Name: " + edtName.getText().toString() + "\n" +
                "Last Name: " + edtLastName.getText().toString() + "\n" +
                "DOB (DD-MM-YYYY): " + edtBirthday.getText().toString() + "\n" +
                "Mobile Phone: " + edtMobilePhone.getText().toString() + "\n" +
                "Email Address: " + edtEmail.getText().toString() + "\n" +
                "Address 1: " + edtAddress.getText().toString() + "\n" +
                "City: " + edtCity.getText().toString() + "\n" +
                "Perfecture: " + edtPerfecture.getText().toString() + "\n" +
                "Post Code: " + edtPostCode.getText().toString() + "\n" +
                "Country Code: " + edtCountryCode.getText().toString() + "\n" +
                "Phone: " + edtPhone.getText().toString() + "\n" +
                "Mobile Phone: " + edtMobilePhone.getText().toString() + "\n";
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
//        emailIntent.setClassName("com.google.android.gm","com.google.android.gm.ComposeActivityGmail");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Apply for Debit Card");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.setType("message/rfc822");
        ArrayList<Uri> uris = new ArrayList<Uri>();
        if(selectedImageURI1 != null){
            uris.add(selectedImageURI1);
        }
        if(selectedImageURI2 != null){
            uris.add(selectedImageURI2);
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
//
//        ArrayList<Uri> uris = new ArrayList<Uri>();
//        if(selectedImageURI1 != null){
//            uris.add(selectedImageURI1);
//        }
//        if(selectedImageURI2 != null){
//            uris.add(selectedImageURI2);
//        }
//        Utils.sendEmail(getActivity(), "Apply for Debit Card", body, uris);
    }

    @Override
    public void onResume() {
        super.onResume();
        ChangeTitleListener.getIntance().setTitle("Apply for Debit Card");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE1) {
                //TODO: action
                selectedImageURI1 = data.getData();
                imgDocument1.setImageResource(getActivity().getResources().getColor(R.color.trans));
                Picasso.with(getActivity()).load(selectedImageURI1).noPlaceholder().centerCrop().fit()
                        .into(imgDocument1);
            } else if (requestCode == PICK_IMAGE2) {
                //TODO: action
                selectedImageURI2 = data.getData();
                imgDocument2.setImageResource(getActivity().getResources().getColor(R.color.trans));
                Picasso.with(getActivity()).load(selectedImageURI2).noPlaceholder().centerCrop().fit()
                        .into(imgDocument2);
            }
        }
    }
}
