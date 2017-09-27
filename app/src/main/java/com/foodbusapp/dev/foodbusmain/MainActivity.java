package com.foodbusapp.dev.foodbusmain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button donor;
    private Button receiver,register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
            }
        });


        donor = (Button) findViewById(R.id.main_button_donor);
        receiver = (Button) findViewById(R.id.main_button_receiver);
        register = (Button) findViewById(R.id.main_button_register);


        donor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                donorLogin();
            }
        });

        receiver.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                receiverLogin();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(Utils.isOnline(MainActivity.this)){
                    String url = Utils.BASE_URL + "app-register.php";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setTitle("Connection Failed");
                    builder.setMessage("No Internet Available. Please connect to a network and try again !");
                    builder.show();
                }


            }
        });

    }

    public void donorLogin(){

        if (Utils.isOnline(MainActivity.this)) {



            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.putExtra("type", "donor");
            startActivity(i);

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            builder.setTitle("Connection Failed");
            builder.setMessage("No Internet Available. Please connect to a network and try again !");
            builder.show();
        }
    }

    public void receiverLogin(){

        if (Utils.isOnline(MainActivity.this)) {

            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.putExtra("type", "receiver");
            startActivity(i);

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            builder.setTitle("Connection Failed");
            builder.setMessage("No Internet Available. Please connect to a network and try again !");
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_about) {

            Intent i = new Intent(MainActivity.this,AboutActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
