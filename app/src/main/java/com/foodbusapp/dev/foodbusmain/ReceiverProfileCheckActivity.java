package com.foodbusapp.dev.foodbusmain;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReceiverProfileCheckActivity extends AppCompatActivity {

    TextView tname;
    TextView tphone;
    TextView temail;
    TextView taddress;
    ImageView image;
    Button bphone;
    Button bemail;
    Button bclaim;
    RatingBar ratingBar;
    Bitmap bitmp;

    String name;
    String id;
    String phone;
    String email;
    String door;
    String street;
    String area;
    String village;
    String city;
    String pin;
    String address;
    String profile;
    String rating;
    String times;
    String claimVisible = "no";
    Toolbar toolbar;

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_check);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
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
        name = i.getExtras().getString("name");
        id = i.getExtras().getString("id");
        profile = i.getExtras().getString("profile");
        phone = i.getExtras().getString("phone");
        email = i.getExtras().getString("email");
        door = i.getExtras().getString("door");
        street = i.getExtras().getString("street");
        area = i.getExtras().getString("area");
        village = i.getExtras().getString("village");
        city = i.getExtras().getString("city");
        pin = i.getExtras().getString("pin");
        rating = i.getExtras().getString("rating");
        times = i.getExtras().getString("times");
        claimVisible = i.getExtras().getString("visible","no");

        address = "#" + door + ", " + street + ",\n"
                + area + ",\n" + village + ",\n"
                + city + ",\n" + pin;
        new ProfileTask().execute(Utils.BASE_URL + profile);

        tname = (TextView) findViewById(R.id.check_name);
        temail = (TextView) findViewById(R.id.check_email_text);
        tphone = (TextView) findViewById(R.id.check_phone_text);
        taddress = (TextView) findViewById(R.id.check_address);
        image = (ImageView) findViewById(R.id.check_image);
        bphone = (Button) findViewById(R.id.check_phone);
        bclaim = (Button) findViewById(R.id.check_claim_button);
        bemail = (Button) findViewById(R.id.check_email);

        ratingBar = (RatingBar) findViewById(R.id.check_rating);
        if(Integer.parseInt(rating) == 0 && Integer.parseInt(times) == 0){
            ratingBar.setRating(0);
        }else{
            int average = (Integer.parseInt(rating)/Integer.parseInt(times));
            ratingBar.setRating(average);
        }


        bphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                startActivity(intent);


            }
        });

        if(claimVisible.equals("no")){
            bclaim.setVisibility(View.GONE);
            toolbar.setTitle("Recevier Details");
        }


        bclaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ReceiverProfileCheckActivity.this,DonorFoodCheckActivity.class);
                i.putExtra("id",id);
                i.putExtra("donor_name",name);
                startActivity(i);
            }
        });

        bemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, email);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

                startActivity(Intent.createChooser(intent, "Send Email"));

            }
        });

        tname.setText(name);
        tphone.setText(phone);
        temail.setText(email);
        taddress.setText(address);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 901 && resultCode == RESULT_OK){
            finish();
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

            image.setImageBitmap(b);


        }

    }

}
