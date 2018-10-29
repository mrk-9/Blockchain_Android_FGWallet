package com.ivan.fgwallet.Fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ivan.fgwallet.WalletApplication;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ivan.fgwallet.Adapter.HistoryAdapter;
import com.ivan.fgwallet.R;
import com.ivan.fgwallet.WalletApplication;
import com.ivan.fgwallet.helper.PrefManager;
import com.ivan.fgwallet.listener.ChangeTitleListener;
import com.ivan.fgwallet.model.History;
//import com.ivan.fgwallet.volley.AppController;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TransactionHistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public final String TAG = "VOLLEY";
    String tag_json_obj = "json_obj_req";
    KProgressHUD progress_dialog;
    ListView listView;
    String address;
    String phone;
    SwipeRefreshLayout swipeRefreshLayout;
    Collection<History> histories = new LinkedList<History>();
    HistoryAdapter historyAdapter;
    @Override
    public void onResume() {
        super.onResume();
        ChangeTitleListener.getIntance().setTitle(getResources().getString(R.string.transaction_history));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (!new PrefManager(getActivity()).getpref(PrefManager.KEY_ADDRESS).equals("")) {
            address = new PrefManager(getActivity()).getpref(PrefManager.KEY_ADDRESS);

        }
        getBalance();

        return rootView;
    }

    public void getBalance() {
        swipeRefreshLayout.setRefreshing(true);
        String path = "http://128.199.129.208/api/wallet/history";
        progress_dialog = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        progress_dialog.show();
        String apiToken = "";
        if (!new PrefManager(getActivity().getApplicationContext()).getpref(PrefManager.KEY_API_TOKEN).equals("")) {
            apiToken = new PrefManager(getActivity()).getpref(PrefManager.KEY_API_TOKEN);
        }
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("api_token", apiToken);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                path, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        String message = "";
                        try {
                            message = response.getString("status");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (message.equals("SUCCESS")) {
                            try {
                                JSONObject ojb = response.getJSONObject("data");
                                JSONArray jsonArray = ojb.getJSONArray("history");
                                Gson gson = new Gson();
                                Collection<History> histories = gson.fromJson(jsonArray.toString(), new TypeToken<List<History>>() {
                                }.getType());
                                if (histories.size() > 0) {
                                    createHistory(histories);
//                                    historyAdapter = new HistoryAdapter(getActivity(), histories);
//                                    historyAdapter.notifyDataSetChanged();
                                } else {
//                                    Toast.makeText(getActivity(), "Not found history", Toast.LENGTH_LONG).show();
                                    Snackbar.make(getView(), "Not found history", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            String msg = "Something went wrong , Please try later";
//                            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                            Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        progress_dialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getActivity(), "Can't connect to server!", Toast.LENGTH_LONG).show();
                Snackbar.make(getView(), "Can't connect to server!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                progress_dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        // Adding request to request queue
        WalletApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void createHistory(Collection<History> histories) {
        this.histories = histories;
        historyAdapter = new HistoryAdapter(getActivity(), histories);
        listView.setAdapter(historyAdapter);
    }

    @Override
    public void onRefresh() {
        getBalance();
    }
}
