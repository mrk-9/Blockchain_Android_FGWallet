package com.ivan.fgwallet.Fragments;

import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivan.fgwallet.R;
import com.ivan.fgwallet.listener.ChangeTitleListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AtmMapFragment extends Fragment implements OnMapReadyCallback{

    @Override
    public void onResume() {
        super.onResume();
        ChangeTitleListener.getIntance().setTitle(getResources().getString(R.string.atm_map));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_atm_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        LatLng sydney = new LatLng(43.272709, 141.549481);
        LatLng sydney2 = new LatLng(38.435252, 140.920245);
        LatLng sydney3 = new LatLng(35.779326, 139.542881);
        LatLng sydney4 = new LatLng(35.539428, 139.497176);
        LatLng sydney5 = new LatLng(34.847636, 135.311275);
        LatLng sydney6 = new LatLng(34.364564, 132.386433);
        LatLng center = new LatLng(37.481965, 136.577850);
        String openTime = "電話番号：011-271-3660\n" +
                "営業時間：10：00～24：00\n" +
                "定休日：年中無休";
        String openTime2 = "電話番号：022−797−7068\n" +
                "営業時間：19:00～24:00\n" +
                "定休日：日曜日定休";
        String openTime3 = "店舗の詳細はこちら";
        String openTime4 = "店舗の詳細はこちら";
        String openTime5 = "電話番号：06-6226-7220\n" +
                "営業時間：17：00～24：00\n" +
                "定休日：月曜日";
        String openTime6 = "店舗の詳細はこちら";
        googleMap.addMarker(new MarkerOptions().position(sydney).title("nORBESA（ノルベサ）" + "," + openTime).snippet("5 Chome-1-1 Minami 3 Jōnishi, Chūō-ku, Sapporo-shi, Hokkaidō 060-0063, Japan"));
        googleMap.addMarker(new MarkerOptions().position(sydney2).title("手品家 仙台店" + "," + openTime2).snippet("2 Chome-12-18 Kokubunchō, Aoba-ku, Sendai-shi, Miyagi-ken 980-0803, Japan"));
        googleMap.addMarker(new MarkerOptions().position(sydney3).title("WORLD STAR CAFE" + "," + openTime3).snippet("5 Chome-1-3 Roppongi, Minato-ku, Tōkyō-to 106-0032, Japan"));
        googleMap.addMarker(new MarkerOptions().position(sydney4).title("Cuba mojit 専門店" + "," + openTime4).snippet("1 Chome-8 Ishikawachō, Naka-ku, Yokohama-shi, Kanagawa-ken 231-0868, Japan"));
        googleMap.addMarker(new MarkerOptions().position(sydney5).title("chef's kichen 白猫" + "," + openTime5).snippet("1 Chome-3-5 Shinsaibashisuji, Chūō-ku, Ōsaka-shi, Ōsaka-fu 542-0085, Japan"));
        googleMap.addMarker(new MarkerOptions().position(sydney6).title("Petit Colon" + "," + openTime6).snippet("2-19 Hashimotochō, Naka-ku, Hiroshima-shi, Hiroshima-ken 730-0015, Japan"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(center));
        googleMap.animateCamera( CameraUpdateFactory.zoomTo( 5.0f ) );

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.custom_info_contents, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) v.findViewById(R.id.title);
                TextView tvOpenTime = (TextView) v.findViewById(R.id.opentime);

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.snippet);

                // Setting the latitude
                if (arg0.getTitle() != null) {
                    if (arg0.getTitle().split(",")[0] != null)
                        tvLat.setText("" + arg0.getTitle().split(",")[0]);
                    if (arg0.getTitle().split(",")[1] != null)
                        tvOpenTime.setText("" + arg0.getTitle().split(",")[1]);
                    // Setting the longitude
                    tvLng.setText("" + arg0.getSnippet());
                } else {
                    v.setVisibility(View.GONE);
                }
                // Returning the view containing InfoWindow contents
                return v;

            }
        });

        // Adding and showing marker while touching the GoogleMap
//        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//
//            @Override
//            public void onMapClick(LatLng arg0) {
//                // Clears any existing markers from the GoogleMap
//                googleMap.clear();
//
//                // Creating an instance of MarkerOptions to set position
//                MarkerOptions markerOptions = new MarkerOptions();
//
//                // Setting position on the MarkerOptions
//                markerOptions.position(arg0);
//
//                // Animating to the currently touched position
//                googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));
//
//                // Adding marker on the GoogleMap
//                Marker marker = googleMap.addMarker(markerOptions);
//
//                // Showing InfoWindow on the GoogleMap
//                marker.showInfoWindow();
//
//            }
//        });

    }
}
