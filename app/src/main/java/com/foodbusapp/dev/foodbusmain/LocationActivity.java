package com.foodbusapp.dev.foodbusmain;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends AppCompatActivity {


    private static final int ERROR_DIALOG_REQUEST = 9001;

    private GoogleMap mMap;
    Intent i;
    String lat, lng;
    String name, door, street, area, village, city, pin, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        i = getIntent();
        lat = i.getExtras().getString("lat");
        lng = i.getExtras().getString("lng");
        name = i.getExtras().getString("name");
        door = i.getExtras().getString("door");
        street = i.getExtras().getString("street");
        area = i.getExtras().getString("area");
        village = i.getExtras().getString("village");
        city = i.getExtras().getString("city");
        pin = i.getExtras().getString("pin");
        phone = i.getExtras().getString("phone");


        if (servicesOK()) {

            if (initMap()) {
                moveToLocation(lat, lng, 18.0f);
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            builder.setTitle("Connection Failed");
            builder.setMessage("Cannot connect to mapping services.Please Try after a while");
            builder.show();
        }
    }

    public boolean initMap() {

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
            mMap = mapFragment.getMap();

            if (mMap != null) {

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View v = getLayoutInflater().inflate(R.layout.map_marker, null);

                        TextView main_name = (TextView) v.findViewById(R.id.marker_name);
                        TextView main_address = (TextView) v.findViewById(R.id.marker_address);
                        TextView main_phone = (TextView) v.findViewById(R.id.marker_phone);
                        main_name.setText(name);
                        main_phone.setText(phone);
                        main_address.setText("#" + door + ", " + street + ",\n" + area + ",\n" + village + ",\n" + city + ",\n" + pin);

                        return v;
                    }
                });

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                    }
                });
            }
        }
        return mMap != null;

    }

    public void moveToLocation(String lat,String lng,float zoom){

        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lng);
        LatLng home = new LatLng(latitude, longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(home, zoom);
        mMap.moveCamera(update);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));

    }


    public boolean servicesOK(){


        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,this,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            builder.setTitle("Connection Failed");
            builder.setMessage("Cannot connect to mapping services.Please Try after a while");
            builder.show();
        }


        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            CameraUpdateFactory.zoomTo(15);
        }

        if (id == R.id.terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            CameraUpdateFactory.zoomTo(15);

        }
        if (id == R.id.traffic) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            CameraUpdateFactory.zoomTo(15);

        }

        return super.onOptionsItemSelected(item);
    }

}
