package com.ivan.fgwallet.networking;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ivan.fgwallet.interfaces.NetworkCallBack;
import com.ivan.fgwallet.service.Constants;
import com.kaopiz.kprogresshud.KProgressHUD;


/**
 * Created by arvind on 16/07/17.
 */

public class Networking {

    private KProgressHUD progress_dialog;
    NetworkCallBack networkCallBack;
    Context context;

    public Networking(Context context, NetworkCallBack networkCallBack) {
        progress_dialog =   KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")

                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        this.context = context;
        this.networkCallBack = networkCallBack;
    }


    public static String URL = "http://www.marketbugs.com/fg-wallet/?";

    public void doexecuteGET(String method) {
        showProgress();
        AndroidNetworking.get(URL+ method)
                .addHeaders(Constants.getHeader())
                .addPathParameter(method)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        hideProgress();
                        networkCallBack.callBack(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println(anError.getResponse());
                        hideProgress();
                        networkCallBack.callBack("error");
                    }
                });
    }


    public void doexecutePOST(String  path) {
        showProgress();
        AndroidNetworking.post(path)
                .addHeaders(Constants.getHeader())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        hideProgress();
                        networkCallBack.callBack(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println(anError);
                        hideProgress();
                        networkCallBack.callBack("error");
                    }
                });
    }

    public void doexecutePOSTWITHOUTHEADER(String  path) {
        showProgress();
        AndroidNetworking.post(path)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        hideProgress();
                        networkCallBack.callBack(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println(anError);
                        hideProgress();
                        networkCallBack.callBack("error");
                    }
                });
    }


    public void doexecuteGETWITHOUTHEADER(String  path) {
        showProgress();
        AndroidNetworking.get(path)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        hideProgress();
                        networkCallBack.callBack(response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        System.out.println(anError);
                        hideProgress();
                        networkCallBack.callBack("error");
                    }
                });
    }

    public void showProgress() {
        if (progress_dialog != null)
            if (progress_dialog.isShowing()) {
                progress_dialog.dismiss();
            }
        progress_dialog.show();
    }

    public void hideProgress() {
        if (progress_dialog != null)
            if (progress_dialog.isShowing()) {
                progress_dialog.dismiss();
            }
    }


}
