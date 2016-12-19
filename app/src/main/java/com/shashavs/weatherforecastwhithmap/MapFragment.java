package com.shashavs.weatherforecastwhithmap;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends SupportMapFragment {
    private final String TAG = "MapFragment";

    private GoogleMap mMap;

    private double latitude;
    private double longitude;

    private final String KEY_LAT = "latitude";
    private final String KEY_LON = "longitude";

    @Override
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                if(bundle != null) {
                    latitude = bundle.getDouble(KEY_LAT, 0);
                    longitude = bundle.getDouble(KEY_LON, 0);
                    updateUI(latitude, longitude, false);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putDouble(KEY_LAT, latitude);
        bundle.putDouble(KEY_LON, longitude);
        super.onSaveInstanceState(bundle);
    }

    public void updateUI(double latitude, double longitude, boolean animate) {
        this.latitude = latitude;
        this.longitude = longitude;

        if (mMap == null) {
            return;
        }

        LatLng myPoint = new LatLng(latitude, longitude);
        MarkerOptions myMarker = new MarkerOptions().position(myPoint);
        mMap.clear();
        mMap.addMarker(myMarker);

        if(animate) {
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myPoint, 16);
            mMap.animateCamera(update);
        }
    }
}
