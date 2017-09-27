package com.foodbusapp.dev.foodbusmain;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button login;
    private ProgressBar progress;
    private SharedPreferences preferences;

    boolean isDonor = false;
    String type = "receiver";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login_button);
        progress = (ProgressBar) findViewById(R.id.login_progress);
        progress.setVisibility(View.INVISIBLE);

        preferences = getSharedPreferences("Pref", Context.MODE_PRIVATE);

        try{
            Intent intent = getIntent();
            type = intent.getExtras().getString("type");

            Log.d("Jeeva","INTENT LOGIN RECEIVED " + intent.getExtras().getString("type"));

        }catch(Exception e){

        }

        if(type.equalsIgnoreCase("donor")){
            Log.d("Jeeva", "LOGIN_TYPE: " + "Donor");
            isDonor = true;
        }else if(type.equalsIgnoreCase("receiver")){
            Log.d("Jeeva", "LOGIN_TYPE: " + "Receiver");
            isDonor = false;
        }

        if(isDonor){

            String id = preferences.getString("donor_id","");
            if(id.isEmpty()){

            } else {
                Log.d("Jeeva", "DONOR_SESSION_ENABLED");
                Intent i = new Intent(LoginActivity.this,DonorHomeActivity.class);
                i.putExtra("userId", id);
                Log.d("ID PUT",id);
                finish();
                startActivity(i);
            }


        }else {

            String id = preferences.getString("receiver_id", "");
            if (id.isEmpty()) {

            } else {
                Log.d("Jeeva", "RECEIVER_SESSION_ENABLED");
                Intent i = new Intent(LoginActivity.this, ReceiverHomeActivity.class);
                i.putExtra("userId", id);
                finish();
                startActivity(i);
            }

        }


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                RequestPackage request = new RequestPackage();
                request.setUri( Utils.BASE_URL + "android/login-check.php");
                request.setMethod("GET");
                request.setParam("username", usernameText);
                request.setParam("password", passwordText);
                request.setParam("type", type);


                if(isDonor){

                    String id = preferences.getString("donor_id","");
                    if(id.isEmpty()){

                    }else{
                        Intent i = new Intent(LoginActivity.this,DonorHomeActivity.class);
                        i.putExtra("userId", id);
                        finish();
                        startActivity(i);

                    }


                }else{

                    String id = preferences.getString("receiver_id","");
                    if(id.isEmpty()){

                    }else{
                        Intent i = new Intent(LoginActivity.this,ReceiverHomeActivity.class);
                        i.putExtra("userId",id);
                        finish();
                        startActivity(i);
                    }

                }


                if (isDonor) {
                    Log.d("Jeeva" , "LOGIN_REQUEST_ATTEMPT: " + "Donor");
                    new WebDonorLoginCheck().execute(request);

                } else {
                    Log.d("Jeeva", "LOGIN_REQUEST_ATTEMPT: " + "Receiver");
                    new WebReceiverLoginCheck().execute(request);


                }
            }
        });


    }

    public class WebDonorLoginCheck extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva", "LOGIN_DONOR_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            progress.setVisibility(View.INVISIBLE);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva", "LOGIN_DONOR_RESPONSE: " +  response);

                if(!response.toString().trim().equalsIgnoreCase("0")){

                    String id = response;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("donor_id",id);
                    editor.commit();
                    Intent i = new Intent(LoginActivity.this,DonorHomeActivity.class);
                    i.putExtra("userId",id);
                    finish();
                    startActivity(i);

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(LoginActivity.this, "OK Clicked", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setTitle("Login Failed");
                    builder.setMessage("The Credentials you provided is not a valid one !");
                    builder.show();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(LoginActivity.this, "OK Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setTitle("Server Error");
                builder.setMessage("The Server is not available at given Domain Name");
                builder.show();
            }





        }

    }


    public class WebReceiverLoginCheck extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva", "LOGIN_RECEIVER_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            progress.setVisibility(View.INVISIBLE);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva", "LOGIN_RECEIVER_RESPONSE: " +  response);

                if(!response.toString().trim().equalsIgnoreCase("0")){

                    String id = response;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("receiver_id",id);
                    editor.commit();
                    Intent i = new Intent(LoginActivity.this,ReceiverHomeActivity.class);
                    Utils.USER_ID = response;
                    i.putExtra("userId",response);
                    finish();
                    startActivity(i);


                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(LoginActivity.this, "OK Clicked", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setTitle("Login Failed");
                    builder.setMessage("The Credentials you provided is not a valid one !");
                    builder.show();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(LoginActivity.this, "OK Clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setTitle("Server Error");
                builder.setMessage("The Server is not available at given Domain Name");
                builder.show();
            }

        }

    }



}
