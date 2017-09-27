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
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class ReceiverHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Context ctx;
    NavigationView navigationView;
    public static  Receiver receiver;
    ImageView imageView;
    Intent intent;
    SharedPreferences prefs;
    String regId;
    Context context;
    GoogleCloudMessaging gcm;
    public static Toolbar toolbar;
    public static ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = getApplicationContext();

        progress = (ProgressBar) findViewById(R.id.receiver_main_progress);
        progress.setVisibility(View.GONE);

        ctx = this;
        prefs = getSharedPreferences("Pref",
                Context.MODE_PRIVATE);

        receiver = new Receiver();

        intent = getIntent();
        receiver.setUserId(intent.getExtras().getString("userId"));
        Utils.USER_ID = receiver.getUserId();

        setupGCM();

        Log.d("Jeeva", "USER_ID: " + receiver.getUserId());


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.receiver_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view_receiver);
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

    private void registerInbackGround() {

        new AsyncTask(){
            @Override
            protected Object doInBackground(Object[] params) {

                try{

                    if(gcm == null){
                        gcm = GoogleCloudMessaging.getInstance(ReceiverHomeActivity.this);
                    }

                    regId = gcm.register(Utils.SENDER_ID);
                    String msg = "Device Registered " + regId;

                    storeRegistration(regId);

                }catch (Exception e){

                }
                return null;
            }
        }.execute();
    }

    private boolean userRegistered() {

        SharedPreferences prefs = getSharedPreferences("Pref",
                Context.MODE_PRIVATE);

        String regId = prefs.getString("regId", "");
        if (regId.isEmpty()) {

            Log.i("Jeeva", "Registration not found.");
            return false;
        }

        return true;
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
        editor.putString("regId", regId);
        editor.commit();

    }

    private String getRegistrationId(){

        SharedPreferences prefs = getSharedPreferences("Pref",
                Context.MODE_PRIVATE);
        String registrationId = prefs.getString("regId", "");
        if (registrationId.isEmpty()){

            Log.i("Jeeva", "Registration ID not found.");
            return "";
        }

        return registrationId;
    }



    private void sendToServer() {

            String id = receiver.getUserId();

            RequestPackage requestPackage = new RequestPackage();
            requestPackage.setMethod("GET");
            requestPackage.setUri(Utils.APP_SERVER_URL + "add_reg.php");
            requestPackage.setParam("regId", regId);
            requestPackage.setParam("id", id);
            new WebGCMTask().execute(requestPackage);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.receiver_drawer_layout);
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
            String iden = prefs.getString("regId", "0");
            RequestPackage pack = new RequestPackage();
            pack.setParam("user_rec_id",iden);
            pack.setMethod("GET");
            pack.setUri(Utils.BASE_URL + "android/gcm-remove.php");
            new GCMRemoveTask().execute(pack);
            editor.remove("receiver_id");
            editor.commit();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id == R.id.nav_receiver_request) {

            Log.d("Jeeva", "RECEIVER_REQUEST_EDIT");
            toolbar.setTitle("Sent Requests");
            ReceiverRequestEditFragment homeFragment = new ReceiverRequestEditFragment(ctx,receiver);
            getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, homeFragment).commit();

        }else if (id == R.id.nav_receiver_request_accept) {

            Log.d("Jeeva", "RECEIVER_ACCEPT_REQUEST");
            toolbar.setTitle("Approved Requests");
            ReceiverAcceptedRequestFragment acceptedFragment = new ReceiverAcceptedRequestFragment(ctx,receiver);
            getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, acceptedFragment).commit();

        }else if (id == R.id.nav_receiver_request_denied) {

            Log.d("Jeeva", "RECEIVER_DENIED_REQUEST");
            toolbar.setTitle("Denied Requests");
            ReceiverDeniedRequestFragment deniedFragment = new ReceiverDeniedRequestFragment(ctx,receiver);
            getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, deniedFragment).commit();

        } else if (id == R.id.nav_receiver_profile) {

            Log.d("Jeeva", "RECEIVER_PROFILE");
            toolbar.setTitle("My Profile");
            ReceiverProfileFragment homeFragment = new ReceiverProfileFragment(ctx,receiver);
            getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, homeFragment).commit();

        }else if (id == R.id.nav_track_request) {

            Log.d("Jeeva", "RECEIVER_TRACK_REQUEST");
            toolbar.setTitle("Track Requests");
            ReceiverTrackRequestFragment trackFragment = new ReceiverTrackRequestFragment(ctx,receiver);
            getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, trackFragment).commit();

        }else if(id == R.id.nav_receiver_food_available){

            if(Utils.isOnline(ReceiverHomeActivity.this)){
                Intent i = new Intent(ReceiverHomeActivity.this,ReceiverFoodAvailableActivity.class);
                i.putExtra("lat", receiver.getLatitude());
                i.putExtra("lng", receiver.getLongitude());
                i.putExtra("door",receiver.getDoor());
                i.putExtra("street",receiver.getStreet());
                i.putExtra("area",receiver.getArea());
                i.putExtra("village",receiver.getVillage());
                i.putExtra("city",receiver.getCity());
                i.putExtra("pin",receiver.getPin());
                i.putExtra("phone",receiver.getPhone());
                i.putExtra("name",receiver.getName());
                i.putExtra("email",receiver.getEmail());
                i.putExtra("profile",receiver.getProfile());

                startActivity(i);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverHomeActivity.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setTitle("Connection Failed");
                builder.setMessage("No Internet Available. Please connect to a network and try again !");
                builder.show();
            }



        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.receiver_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class WebTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
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
                    receiver.setName(object.getString("name"));
                    receiver.setProfile(object.getString("profile"));
                    receiver.setEmail(object.getString("email"));
                    receiver.setDoor(object.getString("door"));
                    receiver.setArea(object.getString("area"));
                    receiver.setCity(object.getString("city"));
                    receiver.setStreet(object.getString("street"));
                    receiver.setVillage(object.getString("village"));
                    receiver.setPhone(object.getString("phone"));
                    receiver.setPin(object.getString("pin"));
                    receiver.setLatitude(object.getString("latitude"));
                    receiver.setLongitude(object.getString("longitude"));
                    receiver.setRating(object.getString("rating"));
                    receiver.setTimes(object.getString("times"));
                    setInfo();

                    ReceiverHomeActivity.toolbar.setTitle("Revoke Requests");
                    try {
                        ReceiverRequestEditFragment homeFragment = new ReceiverRequestEditFragment(ctx, receiver);
                        getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, homeFragment).commit();

                    }catch (Exception e){

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }else{
                ErrorFragment errorFragment = new ErrorFragment("Problem getting donor details \n Please try after a while !",ctx);
                getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();
                progress.setVisibility(View.GONE);
            }

        }

    }



    public void getInfo() {

        toolbar.setTitle("Sent Requests");

        RequestPackage request = new RequestPackage();
        request.setUri( Utils.BASE_URL + "android/get-receiver-data.php");
        request.setMethod("GET");
        request.setParam("user_id",receiver.getUserId());
        new WebTask().execute(request);

    }

    private void setInfo() {

        TextView name = (TextView) findViewById(R.id.receiver_name);
        TextView email = (TextView) findViewById(R.id.receiver_email);
        imageView = (ImageView) findViewById(R.id.receiver_image);

        name.setText(receiver.getName());
        email.setText(receiver.getEmail());
        progress.setVisibility(View.GONE);

        if(receiver.getImage() == null){
            Log.d("JEEVA", "PROFILE_URL: " + Utils.BASE_URL + receiver.getProfile() );
            new ProfileTask().execute(Utils.BASE_URL + receiver.getProfile());

        }else{
            imageView.setImageBitmap(receiver.getImage());
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
            receiver.setImage(b);


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

            Log.d("Jeeva","GCM_REGISTER_REQUEST" + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {



            String response = s;


            if(response.equalsIgnoreCase("ok")){

                response = response.trim();
                Log.d("Jeeva","GCM_REGISTER_RESPONSE" + response);

            }else{
                Log.d("Jeeva", "DEVICE GCM FAILED");
            }


            super.onPostExecute(s);
        }

    }

}
