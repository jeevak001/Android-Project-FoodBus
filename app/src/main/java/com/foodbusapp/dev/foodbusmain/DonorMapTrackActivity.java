package com.foodbusapp.dev.foodbusmain;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class DonorMapTrackActivity extends AppCompatActivity {

    private static final int ERROR_DIALOG_REQUEST = 9001;

    private GoogleMap mMap;
    private Timer timer;
    TimerTask doAsynchronousTask;
    Intent i;
    String lat, lng;
    String id,name, door, street, area, village, city, pin, phone,foodId;
    Marker home;
    Marker receiver;
    Marker tmpLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_map_track);
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
        id = i.getExtras().getString("user_id");
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
                callAsynchronousTask();
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DonorMapTrackActivity.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            builder.setTitle("Connection Failed");
            builder.setMessage("Cannot connect to mapping services.Please Try after a while");
            builder.show();
        }

    }



    private void showHome() {



        receiver = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Utils.sToD(lat), Utils.sToD(lng))));

        home = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Utils.sToD(DonorHomeActivity.donor.getLatitude()), Utils.sToD(DonorHomeActivity.donor.getLongitude())))
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

                if(marker.equals(receiver)){
                    main_name.setText(name);
                    main_phone.setText(phone);
                    main_address.setText("#" + door + ", " + street + ",\n" + area + ",\n" + village + ",\n" + city + ",\n" + pin);

                }else if(marker.equals(home)){

                    View view = getLayoutInflater().inflate(R.layout.map_small_marker, null);
                    TextView tmp = (TextView) view.findViewById(R.id.marker_small);
                    tmp.setText("Home Location");
                    return view;

                }else{

                    View view = getLayoutInflater().inflate(R.layout.map_small_marker, null);
                    TextView tmp = (TextView) view.findViewById(R.id.marker_small);
                    tmp.setText("Receiver Current Location");
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
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_donor_map_track);
            mMap = mapFragment.getMap();
        }
        return mMap != null;

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        callAsynchronousTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    public boolean servicesOK(){


        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,this,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(DonorMapTrackActivity.this);
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
    public void onBackPressed() {

        timer.cancel();
        finish();
    }

    public void callAsynchronousTask() {
        timer = new Timer();
        doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {

                RequestPackage requestPackage = new RequestPackage();
                requestPackage.setMethod("GET");
                requestPackage.setUri(Utils.BASE_URL + "android/get-gps-receiver.php");
                requestPackage.setParam("food_id", foodId);
                requestPackage.setParam("user_id",id);
                new ReceiverLocationUpdate().execute(requestPackage);
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000);
    }




    public class ReceiverLocationUpdate extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva", "RECEIVER_LOCATION_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva","RECEIVER_LOCATION_RESPONSE: " + response);

                try {
                        JSONObject object = new JSONObject(response);
                        String lat = object.getString("tmp_lat");
                        String lng = object.getString("tmp_lng");


                    LatLng current = new LatLng(Utils.sToD(lat),Utils.sToD(lng));

                    Log.d("Jeeva","Location: " + lat + "," + lng);

                    if(tmpLocation == null){
                        tmpLocation = mMap.addMarker(new MarkerOptions()
                                .position(current).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation)));
                    }else{
                        tmpLocation.remove();
                        tmpLocation = mMap.addMarker(new MarkerOptions()
                                .position(current).icon(BitmapDescriptorFactory.fromResource(R.drawable.mylocation)));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else{
            }

        }

    }

}
