package com.foodbusapp.dev.foodbusmain;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DonorHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Intent intent;
    public static Donor donor;
    ImageView imageView;
    Bitmap b;
    Context ctx;
    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    public static Toolbar toolbar;
    public static ProgressBar progress;
    String regId;


    NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = (ProgressBar) findViewById(R.id.donor_main_progress);
        progress.setVisibility(View.GONE);

        ctx = this;

        prefs = getSharedPreferences("Pref",
                Context.MODE_PRIVATE);

        donor = new Donor();

        intent = getIntent();
        donor.setUserId(intent.getExtras().getString("userId"));

        setupGCM();

        Log.d("Jeeva","USER_ID: " +  donor.getUserId());



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        getInfo();
    }

    private void setupGCM() {


        if(userRegistered()){

            Log.i("Jeeva", "Registration found.");

            regId = getRegistrationId();

        }else{
            if(checkPlayServices()){

                gcm = GoogleCloudMessaging.getInstance(this);
                regId = getRegistrationId();

                if(regId.isEmpty()){
                    registerInbackGround();
                }

            }else{

                Log.i("Jeeva", "Play Services Not found");

            }

        }

        sendToServer();
    }

    private void sendToServer() {

        String id = donor.getUserId();

        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod("GET");
        requestPackage.setUri(Utils.APP_SERVER_URL + "add_reg.php");
        requestPackage.setParam("regIdDonor", regId);
        requestPackage.setParam("id", id);
        new WebGCMTask().execute(requestPackage);

    }

    private void registerInbackGround() {

        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] params) {

                try{

                    if(gcm == null){
                        gcm = GoogleCloudMessaging.getInstance(DonorHomeActivity.this);
                    }

                    regId = gcm.register(Utils.SENDER_ID);
                    String msg = "Device Registered " + regId;

                    storeRegistration(regId);

                }catch (Exception e){
                    Log.d("Jeeva",e.getMessage());
                }
                return null;
            }
        }.execute();
    }



    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){

            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,9000).show();
            }else{
                Log.i("Jeeva", "This device is not supported.");
            }
            return false;

        }

        return true;
    }

    private void storeRegistration(String id) {

        SharedPreferences prefs = getSharedPreferences("Pref",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("regIdDonor", regId);
        editor.commit();

    }

    private boolean userRegistered() {

        SharedPreferences prefs = getSharedPreferences("Pref",
                Context.MODE_PRIVATE);

        String regId = prefs.getString("regIdDonor", "");
        if (regId.isEmpty()) {

            Log.i("Jeeva", "Registration not found.");
            return false;
        }

        return true;
    }

    private String getRegistrationId(){

        SharedPreferences prefs = getSharedPreferences("Pref",
                Context.MODE_PRIVATE);
        String registrationId = prefs.getString("regIdDonor", "");
        if (registrationId.isEmpty()){

            Log.i("Jeeva", "Registration ID not found.");
            return "";
        }

        return registrationId;
    }






    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_donor_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_logout) {

            SharedPreferences.Editor editor = prefs.edit();
            String iden = prefs.getString("regIdDonor", "0");
            RequestPackage pack = new RequestPackage();
            pack.setParam("user_id",iden);
            pack.setMethod("GET");
            pack.setUri(Utils.BASE_URL + "android/gcm-remove.php");
            new GCMRemoveTask().execute(pack);
            editor.remove("donor_id");
            editor.commit();

            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DonorHomeFragment homeFragment = new DonorHomeFragment(donor,ctx);
        getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, homeFragment).commit();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_donor_home) {

            Log.d("Jeeva" , "DONOR_HOME");
            toolbar.setTitle("My Foods");
            DonorHomeFragment homeFragment = new DonorHomeFragment(donor,ctx);
            getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, homeFragment).commit();

        } else if (id == R.id.nav_donor_add) {

            Log.d("Jeeva", "DONOR_ADD");
            toolbar.setTitle("Add Food");
            DonorFoodAddFragment homeFragment = new DonorFoodAddFragment(donor,ctx);
            getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, homeFragment).commit();
        }else if (id == R.id.nav_donor_request) {

            Log.d("Jeeva", "DONOR_REQUST");
            toolbar.setTitle("View Requests");
            DonorRequestFragment requestFragment = new DonorRequestFragment(donor,ctx);
            getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, requestFragment).commit();
        }
        else if (id == R.id.nav_donor_profile) {

            Log.d("Jeeva", "DONOR_PROFILE");
            toolbar.setTitle("My Profile");
            DonorProfileFragment homeFragment = new DonorProfileFragment(donor,ctx);
            getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, homeFragment).commit();

        } else if (id == R.id.nav_donor_request_track) {

            Log.d("Jeeva", "DONOR_REQUEST_TRACK");
            toolbar.setTitle("Track Requests");
            DonorTrackRequestFragment trackFragment = new DonorTrackRequestFragment(ctx,donor);
            getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, trackFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getInfo() {

        RequestPackage request = new RequestPackage();
        request.setUri( Utils.BASE_URL + "android/get-donor-data.php");
        request.setMethod("GET");
        request.setParam("user_id",donor.getUserId());
        new WebTask().execute(request);

    }

    private void setInfo() {

        TextView name = (TextView) findViewById(R.id.donor_name);
        TextView email = (TextView) findViewById(R.id.donor_email);
        imageView = (ImageView) findViewById(R.id.donor_image);
        name.setText(donor.getName());
        email.setText(donor.getEmail());


        if(donor.getImage() == null){
            Log.d("JEEVA", "PROFILE_URL: " + Utils.BASE_URL + donor.getProfile() );
            new ProfileTask().execute(Utils.BASE_URL + donor.getProfile());

        }else{
            imageView.setImageBitmap(donor.getImage());
        }

    }



    public class WebTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            DonorHomeActivity.progress.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            Log.d("Jeeva","GET_DATA_REQUEST: " +  params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva", "GET_DATA_RESPONSE: " + response);

                try {
                    JSONObject object = new JSONObject(response);
                    Log.d("Jeeva NAME:",object.getString("name"));
                    Log.d("Jeeva EMAIL:", object.getString("email"));
                    donor.setName(object.getString("name"));
                    donor.setProfile(object.getString("profile"));
                    donor.setEmail(object.getString("email"));
                    donor.setDoor(object.getString("door"));
                    donor.setArea(object.getString("area"));
                    donor.setCity(object.getString("city"));
                    donor.setStreet(object.getString("street"));
                    donor.setVillage(object.getString("village"));
                    donor.setPhone(object.getString("phone"));
                    donor.setPin(object.getString("pin"));
                    donor.setLatitude(object.getString("latitude"));
                    donor.setLongitude(object.getString("longitude"));
                    donor.setRating(object.getString("rating"));
                    donor.setTimes(object.getString("times"));
                    setInfo();

                    try{
                        DonorHomeFragment homeFragment = new DonorHomeFragment(donor,ctx);
                        getSupportFragmentManager().beginTransaction().add(R.id.donor_fragment,homeFragment).commit();
                        DonorHomeActivity.progress.setVisibility(View.GONE);
                    }catch (Exception e){

                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }else{
                ErrorFragment errorFragment = new ErrorFragment("Problem getting donor details \n Please try after a while !",ctx);
                getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();
                DonorHomeActivity.progress.setVisibility(View.GONE);
            }

        }

    }

    public class GCMRemoveTask extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva","GCM_REMOVE_DATA_REQUEST: " +  params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null) {

                response = response.trim();

                Log.d("Jeeva", "GCM_REMOVE_DATA_RESPONSE: " + response);

            }

        }

    }

    public class ProfileTask extends AsyncTask<String,String,Bitmap> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            DonorHomeActivity.progress.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            URL imageUrl = null;
            try {

                Bitmap bitmap=null;

                imageUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();

                bitmap = BitmapFactory.decodeStream(is);

                return bitmap;


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(Bitmap b) {

            super.onPostExecute(b);

            imageView.setImageBitmap(b);
            donor.setImage(b);

            DonorHomeActivity.progress.setVisibility(View.GONE);


        }

    }


    public class WebGCMTask extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva","GCM_DONOR_REGISTER_REQUEST" + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {



            String response = s;


            if(response.equalsIgnoreCase("ok")){

                response = response.trim();
                Log.d("Jeeva","GCM_DONOR_REGISTER_RESPONSE" + response);

            }else{
                Log.d("Jeeva","DEVICE GCM FAILED");

            }


            super.onPostExecute(s);
        }

    }



}
