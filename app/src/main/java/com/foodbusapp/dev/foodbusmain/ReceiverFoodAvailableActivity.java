package com.foodbusapp.dev.foodbusmain;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReceiverFoodAvailableActivity extends AppCompatActivity {


    private static final int ERROR_DIALOG_REQUEST = 9001;

    private GoogleMap mMap;
    Intent i;
    private String myLat;
    private String name;
    private String myLng;
    private String door;
    private String street;
    private String area;
    private String email;
    private String village;
    private String city;
    private String pin;
    private String phone;
    private Marker home;
    private LatLng myHome;

    ArrayList<FoodLocation> foodLocations;
    ArrayList<Marker> markers;

    // inside your loop:


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_available);
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

        markers = new ArrayList<Marker>();
        foodLocations = new ArrayList<>();

        i = getIntent();
        myLat = i.getExtras().getString("lat");
        myLng = i.getExtras().getString("lng");
        door = i.getExtras().getString("door");
        street = i.getExtras().getString("street");
        name = i.getExtras().getString("name");
        email = i.getExtras().getString("email");
        area = i.getExtras().getString("area");
        village = i.getExtras().getString("village");
        city = i.getExtras().getString("city");
        pin = i.getExtras().getString("pin");
        phone = i.getExtras().getString("phone");


        if (servicesOK()) {

            if (initMap()) {

                myHome = new LatLng(Utils.sToD(myLat),Utils.sToD(myLng));

                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myHome, 10f);
                mMap.moveCamera(update);
                home = mMap.addMarker(new MarkerOptions()
                        .position(myHome).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker)));
                home.showInfoWindow();

                RequestPackage requestPackage = new RequestPackage();
                requestPackage.setUri(Utils.BASE_URL + "android/get-food-locations.php");
                requestPackage.setMethod("GET");
               new FoodAvailableTask().execute(requestPackage);

            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverFoodAvailableActivity.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            builder.setTitle("Connection Failed");
            builder.setMessage("Cannot connect to mapping services.Please Try after a while");
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 900 && resultCode == RESULT_OK){
            finish();
        }

    }

    public boolean initMap() {

        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map_foods);
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

                        Button main_name = (Button) v.findViewById(R.id.marker_name);
                        TextView main_address = (TextView) v.findViewById(R.id.marker_address);
                        TextView main_phone = (TextView) v.findViewById(R.id.marker_phone);

                        if(marker.equals(home)){

                            View view = getLayoutInflater().inflate(R.layout.map_small_marker, null);
                            TextView tmp = (TextView) view.findViewById(R.id.marker_small);
                            tmp.setText("My Home Location");
                            return view;


                        }else{

                            int pos = markers.indexOf(marker);
                            main_name.setText(foodLocations.get(pos).getName());
                            main_address.setText("#" + foodLocations.get(pos).getDoor() + ", " + foodLocations.get(pos).getStreet() + ",\n"
                                    + foodLocations.get(pos).getArea() + ",\n" + foodLocations.get(pos).getVillage() + ",\n"
                                    + foodLocations.get(pos).getCity() + ",\n" + foodLocations.get(pos).getPin() );
                            main_phone.setText(foodLocations.get(pos).getPhone());

                        }


                        return v;
                    }
                });

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        if(marker.equals(home)){


                        }else{

                            int pos = markers.indexOf(marker);

                            Intent i = new Intent(ReceiverFoodAvailableActivity.this,ReceiverProfileCheckActivity.class);
                            i.putExtra("lat", foodLocations.get(pos).getLatitude());
                            i.putExtra("id", foodLocations.get(pos).getUserId());
                            i.putExtra("lng", foodLocations.get(pos).getLongitude());
                            i.putExtra("name",foodLocations.get(pos).getName());
                            i.putExtra("phone",foodLocations.get(pos).getPhone());
                            i.putExtra("door",foodLocations.get(pos).getDoor());
                            i.putExtra("area",foodLocations.get(pos).getArea());
                            i.putExtra("street",foodLocations.get(pos).getStreet());
                            i.putExtra("village",foodLocations.get(pos).getVillage());
                            i.putExtra("city",foodLocations.get(pos).getCity());
                            i.putExtra("pin",foodLocations.get(pos).getPin());
                            i.putExtra("email",foodLocations.get(pos).getEmail());
                            i.putExtra("profile",foodLocations.get(pos).getProfile());
                            i.putExtra("rating",foodLocations.get(pos).getRating());
                            i.putExtra("times",foodLocations.get(pos).getTimes());
                            i.putExtra("visible","yes");
                            startActivityForResult(i,900);

                        }

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
                .position(new LatLng(latitude, longitude)));

    }


    public boolean servicesOK(){


        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS){
            return true;
        }else if(GooglePlayServicesUtil.isUserRecoverableError(isAvailable)){
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable,this,ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverFoodAvailableActivity.this);
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


    public class FoodAvailableTask extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva", "FOOD_LOCATIONS_MAP_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                if(response.equalsIgnoreCase("no")){

                    Toast.makeText(ReceiverFoodAvailableActivity.this, "No Food Available right now", Toast.LENGTH_SHORT).show();

                }else{

                    response = response.trim();

                    Log.d("Jeeva", "FOOD_LOCATIONS_MAP_RESPONSE: " + response);

                    try {
                        JSONArray array = new JSONArray(response);

                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            FoodLocation location = new FoodLocation();
                            location.setName(object.getString("name"));
                            location.setUserId(object.getString("user_id"));
                            location.setLatitude(object.getString("lat"));
                            location.setLongitude(object.getString("lng"));
                            location.setDoor(object.getString("door"));
                            location.setVillage(object.getString("village"));
                            location.setStreet(object.getString("street"));
                            location.setArea(object.getString("area"));
                            location.setCity(object.getString("city"));
                            location.setPin(object.getString("pin"));
                            location.setPhone(object.getString("phone"));
                            location.setEmail(object.getString("email"));
                            location.setRating(object.getString("rating"));
                            location.setTimes(object.getString("times"));
                            location.setProfile(object.getString("profile"));
                            foodLocations.add(location);
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(location.getLatitude()), Double.parseDouble(location.getLongitude()))));
                            markers.add(marker);

                            Log.d("Jeeva", "FOOD_LOCATION_ITEM_LAT: " + String.valueOf(Double.parseDouble(location.getLatitude())));
                            Log.d("Jeeva" , "FOOD_LOCATION_ITEM_LNG: " + String.valueOf(Double.parseDouble(location.getLongitude())));
                        }

                        for (int i=0;i<foodLocations.size();i++) {
                            Log.d("Jeeva","FOOD_LOCATIONS_RESPONSE_ITEM: " + foodLocations.get(i).getName());
                        }

                        setMarkers();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }else{

                Toast.makeText(ReceiverFoodAvailableActivity.this, "Error getting available food data", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void setMarkers() {

        for(int i=0;i<foodLocations.size();i++){

        }
    }

}
