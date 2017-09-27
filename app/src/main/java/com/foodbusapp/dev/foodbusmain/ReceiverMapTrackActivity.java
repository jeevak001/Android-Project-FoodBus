package com.foodbusapp.dev.foodbusmain;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class ReceiverMapTrackActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private GoogleMap mMap;
    Intent i;
    String lat, lng;
    String name, door, street, area, village, city, pin, phone,foodId;
    Marker home;
    Marker donor;
    Marker tmpLocation;

    private GoogleApiClient mLocationClient;
    private LocationListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_map_track);
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
        foodId = i.getExtras().getString("food_id");


        if (servicesOK()) {

            if (initMap()) {
                
                showHome();

                if(checkLocationON()){

                    mLocationClient = new GoogleApiClient.Builder(this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                    mLocationClient.connect();

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverMapTrackActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
                    builder.setTitle("Tracking Failed");
                    builder.setMessage("Please enable Location Services to Enable Tracking");
                    builder.show();

                    mLocationClient = new GoogleApiClient.Builder(this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                    mLocationClient.connect();
                }




            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverMapTrackActivity.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            builder.setTitle("Connection Failed");
            builder.setMessage("Cannot connect to mapping services.Please Try after a while");
            builder.show();
        }

    }

    public boolean checkLocationON(){
        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled) {

            return false;

        }else{
            return true;
        }
    }

    private void showHome() {



        donor = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Utils.sToD(lat), Utils.sToD(lng))));

        home = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Utils.sToD(ReceiverHomeActivity.receiver.getLatitude()), Utils.sToD(ReceiverHomeActivity.receiver.getLongitude())))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
        home.showInfoWindow();

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(home.getPosition(), 15f);
        mMap.moveCamera(update);

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

                if(marker.equals(donor)){
                    main_name.setText(name);
                    main_phone.setText(phone);
                    main_address.setText("#" + door + ", " + street + ",\n" + area + ",\n" + village + ",\n" + city + ",\n" + pin);

                }else if (marker.equals(home)) {

                        View view = getLayoutInflater().inflate(R.layout.map_small_marker, null);
                        TextView tmp = (TextView) view.findViewById(R.id.marker_small);
                        tmp.setText("Home Location");
                        return view;

                    } else {

                        View view = getLayoutInflater().inflate(R.layout.map_small_marker, null);
                        TextView tmp = (TextView) view.findViewById(R.id.marker_small);
                        tmp.setText("Current Location");
                        return view;
                    }


                return v;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

            }
        });
    }


    public boolean initMap() {

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_receiver_map_track);
            mMap = mapFragment.getMap();
        }
        return mMap != null;

    }


    public boolean servicesOK(){


        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,this,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverMapTrackActivity.this);
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



    @Override
    public void onConnected(Bundle bundle) {

        Toast.makeText(ReceiverMapTrackActivity.this, " Location Service Connected", Toast.LENGTH_SHORT).show();

        mListener = new LocationListener() {
            @Override
            public void  onLocationChanged(Location location) {

                RequestPackage requestPackage = new RequestPackage();
                requestPackage.setMethod("GET");
                requestPackage.setUri(Utils.BASE_URL + "android/set-receiver-tmp-gps.php");
                requestPackage.setParam("food_id", foodId);
                requestPackage.setParam("user_id", Utils.USER_ID);
                requestPackage.setParam("tmp_lat", String.valueOf(location.getLatitude()));
                requestPackage.setParam("tmp_lng",String.valueOf(location.getLongitude()));
                new SetGPSTask().execute(requestPackage);

               Log.d("Jeeva", "Location Changed " + location.getLatitude() + "," + location.getLongitude());

                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());

                if(tmpLocation == null){
                    tmpLocation = mMap.addMarker(new MarkerOptions()
                            .position(current).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation)));
                }else{
                    tmpLocation.remove();
                    tmpLocation = mMap.addMarker(new MarkerOptions()
                            .position(current).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation)));
                }
            }
        };

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(100);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                 mLocationClient,locationRequest,mListener
        );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mLocationClient,  mListener
        );

    }


    public class SetGPSTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }


        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            Log.d("Jeeva", "SET_GPS_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva", "SET_GPS_RESPONSE: " + response);


            }else{
            }

        }

    }


}
