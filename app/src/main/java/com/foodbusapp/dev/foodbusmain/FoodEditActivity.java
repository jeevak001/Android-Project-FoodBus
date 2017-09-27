package com.foodbusapp.dev.foodbusmain;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class FoodEditActivity extends AppCompatActivity {

    Intent i;
    public Spinner spinner;
    public Spinner spinnerExpire;
    public SpinnerEditAdapter spinnerAdapter;
    public ExpireSpinnerAdapter spinnerExpireAdapter;
    ProgressDialog dialog;

    String name;
    String foodId;
    String amount;
    String status;
    String expire ="6";

    TextView tname;
    TextView tfoodId;
    TextView tamount;
    View editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_edit);
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

        name = i.getExtras().getString("name");
        foodId = i.getExtras().getString("food_id");
        amount = i.getExtras().getString("amount");
        status = i.getExtras().getString("status");

        Log.d("Jeeva","FOOD_EDIT_REQUEST_FOOD: " + name + " " + foodId  + " " + amount + " " + status );

        tname = (TextView) findViewById(R.id.food_edit_name);
        tamount = (TextView) findViewById(R.id.food_edit_amount);
        editButton = findViewById(R.id.edit_button);

        tname.setText(name);
        tamount.setText(amount);
        spinner = (Spinner) findViewById(R.id.food_edit_status);
        spinnerExpire = (Spinner) findViewById(R.id.food_edit_expire);
        spinnerAdapter = new SpinnerEditAdapter();
        spinnerExpireAdapter = new ExpireSpinnerAdapter();
        spinner.setAdapter(spinnerAdapter);
        spinnerExpire.setAdapter(spinnerExpireAdapter);

        if(status.equalsIgnoreCase("available")){
            spinner.setSelection(0);
        }else if(status.equalsIgnoreCase("claimed")){
            spinner.setSelection(1);
        }else{
            spinner.setSelection(2);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    status = "available";
                }else if(position == 1){
                    status = "claimed";
                }else{
                    status = "wasted";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerExpire.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    expire = "6";
                }else if(position == 1){
                    expire = "12";
                }else if(position == 2){
                    expire = "24";
                }else if(position == 3){
                    expire = "48";
                }else{
                    expire = "72";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestPackage requestPackage = new RequestPackage();
                requestPackage.setMethod("GET");
                requestPackage.setUri(Utils.BASE_URL + "android/donor-food-edit.php");
                requestPackage.setParam("name", tname.getText().toString());
                requestPackage.setParam("food_id",foodId);
                requestPackage.setParam("amount",tamount.getText().toString());
                requestPackage.setParam("status", status);
                requestPackage.setParam("expire", expire);

                new WebTask().execute(requestPackage);
            }
        });





    }


    public class ExpireSpinnerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rootView = convertView;
            if(rootView == null){


                LayoutInflater inflater = (LayoutInflater)LayoutInflater.from(FoodEditActivity.this);
                rootView = inflater.inflate(R.layout.spinner_status_expire_item, parent, false);

                if(position == 0){
                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_expire_item);
                    t.setText("3 Hours");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        t.setBackground(getResources().getDrawable(R.drawable.red_button));
                    }

                }else if(position == 1){

                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_expire_item);
                    t.setText("6 Hours");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        t.setBackground(getResources().getDrawable(R.drawable.red_button));
                    }

                }else if(position == 2){
                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_expire_item);
                    t.setText("12 Hours");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        t.setBackground(getResources().getDrawable(R.drawable.orange_button));
                    }

                }else if(position == 3){
                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_expire_item);
                    t.setText("1 Day");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        t.setBackground(getResources().getDrawable(R.drawable.orange_button));
                    }

                }else if(position == 4){
                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_expire_item);
                    t.setText("2 Days");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        t.setBackground(getResources().getDrawable(R.drawable.green_button));
                    }

                }else{
                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_expire_item);
                    t.setText("3 Days");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        t.setBackground(getResources().getDrawable(R.drawable.green_button));
                    }
                }

            }

            return rootView;
        }
    }


    public class WebTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = ProgressDialog.show(FoodEditActivity.this, "",
                    "Editing Food", true);
        }


        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            Log.d("Jeeva","FOOD_EDIT_REQUEST: " +  params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva","FOOD_EDIT_RESPONSE: " +  response);
                if(response.equalsIgnoreCase("ok")){

                    dialog.dismiss();
                    Toast.makeText(FoodEditActivity.this,"Food edited successfully",Toast.LENGTH_SHORT).show();
                    finish();

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(FoodEditActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.setTitle("Unknown Error");
                    builder.setMessage("The Food was not edited due to some unknown error.Please try after a while");
                    builder.show();
                }

                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }



            }else{
                ErrorFragment errorFragment = new ErrorFragment("Problem editing Food \n Please try after a while !",FoodEditActivity.this);
                getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();
                finish();
            }

        }

    }



    public class SpinnerEditAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View rootView = convertView;
            if(rootView == null){


                LayoutInflater inflater = (LayoutInflater)LayoutInflater.from(FoodEditActivity.this);
                rootView = inflater.inflate(R.layout.spinner_status_item, parent, false);

                if(position == 0){

                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_item);
                    t.setText("Available");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        t.setBackground(getResources().getDrawable(R.drawable.green_button));
                    }else{
                        t.setBackgroundColor(Color.parseColor("#468966"));
                    }


                }else if(position == 1){
                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_item);
                    t.setText("Claimed");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        t.setBackground(getResources().getDrawable(R.drawable.orange_button));
                    }else {
                        t.setBackgroundColor(Color.parseColor("#FF6138"));
                    }

                }else{
                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_item);
                    t.setText("Wasted");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        t.setBackground(getResources().getDrawable(R.drawable.red_button));
                    }else {
                        t.setBackgroundColor(Color.parseColor("#BD4932"));
                    }
                }

            }

            return rootView;
        }
    }

}
