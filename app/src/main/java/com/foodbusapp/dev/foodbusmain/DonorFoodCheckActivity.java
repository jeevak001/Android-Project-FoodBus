package com.foodbusapp.dev.foodbusmain;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DonorFoodCheckActivity extends AppCompatActivity {

    ListView foodCheck;
    static ProgressBar progress;
    FoodCheckAdapter adapter;
    ArrayList<FoodCheckItem> foodCheckList;
    ProgressDialog dialog;

    String id;
    String donorName;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_food_check);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progress = (ProgressBar) findViewById(R.id.food_check_progress);
        progress.setVisibility(View.GONE);
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
        id = i.getExtras().getString("id");
        donorName = i.getExtras().getString("donor_name");

        foodCheckList =  new ArrayList<>();

        foodCheck = (ListView) findViewById(R.id.donor_food_check_from_receiver);


        RequestPackage requestPackage = new RequestPackage();
        requestPackage.setMethod("GET");
        requestPackage.setParam("user_id",id);
        requestPackage.setUri(Utils.BASE_URL + "android/get-food-from-donor.php");
        new DonorFoodCheckTask().execute(requestPackage);
    }

    public class FoodCheckAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return foodCheckList.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rootView = convertView;

            LayoutInflater inflater = (LayoutInflater)LayoutInflater.from(DonorFoodCheckActivity.this);
            rootView = inflater.inflate(R.layout.receiver_donor_food_check_list_item, parent, false);

            final TextView name = (TextView) rootView.findViewById(R.id.receiver_donor_food_check_food_name);
            final TextView fid = (TextView) rootView.findViewById(R.id.receiver_donor_food_check_food_id);
            TextView time = (TextView) rootView.findViewById(R.id.receiver_donor_food_check_time);
            TextView date = (TextView) rootView.findViewById(R.id.receiver_donor_food_check_date);
            final TextView amountLeft = (TextView) rootView.findViewById(R.id.receiver_donor_food_check_amount_left);
            final Button claimButton = (Button) rootView.findViewById(R.id.receiver_donor_food_check_claim);
            final EditText claimText = (EditText) rootView.findViewById(R.id.claim_amount_text);

            String sDate = Utils.getDate(foodCheckList.get(position).getExpire().substring(0,10));
            String sTime = Utils.getTime(foodCheckList.get(position).getExpire().substring(11,19));

            name.setText(foodCheckList.get(position).getName());
            fid.setText("Food Expires on: " + sTime + " " + sDate);
            time.setText(Utils.getTime(foodCheckList.get(position).getTime()));
            date.setText(Utils.getDate(foodCheckList.get(position).getDate()));
            amountLeft.setText("Remaining Food Quantity: " + foodCheckList.get(position).getAmountLeft());

            claimButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    /*Intent i = new Intent(DonorFoodCheckActivity.this,ReceiverClaimActivity.class);
                    i.putExtra("name",foodCheckList.get(position).getName());
                    i.putExtra("food_expire",foodCheckList.get(position).getExpire());
                    i.putExtra("food_id",foodCheckList.get(position).getFoodId());
                    i.putExtra("receiver_id",Utils.USER_ID);
                    i.putExtra("donor_id",id);
                    i.putExtra("amount_left",foodCheckList.get(position).getAmountLeft());
                    i.putExtra("donor_name",donorName);
                    startActivityForResult(i,902);*/

                    if(claimText.getText().toString().equalsIgnoreCase("")){

                        AlertDialog.Builder builder = new AlertDialog.Builder(DonorFoodCheckActivity.this);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        builder.setTitle("Invalid Claim Amount");
                        builder.setMessage("Please Enter a valid Number");
                        builder.show();

                    }else if(Integer.parseInt(claimText.getText().toString()) > Integer.parseInt(foodCheckList.get(position).getAmountLeft())){

                        AlertDialog.Builder builder = new AlertDialog.Builder(DonorFoodCheckActivity.this);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        builder.setTitle("More Claim Amount");
                        builder.setMessage("You cannot claim more than the amount available");
                        builder.show();

                    }else if(Integer.parseInt(claimText.getText().toString()) <= 0){

                        AlertDialog.Builder builder = new AlertDialog.Builder(DonorFoodCheckActivity.this);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                        builder.setTitle("Invalid Claim Amount");
                        builder.setMessage("Please Enter a valid Number");
                        builder.show();

                    }else{
                        RequestPackage requestPackage = new RequestPackage();
                        requestPackage.setMethod("GET");
                        requestPackage.setUri(Utils.BASE_URL + "android/receiver-request-food.php");
                        requestPackage.setParam("receiver_id", Utils.USER_ID);
                        requestPackage.setParam("donor_id",id);
                        requestPackage.setParam("name",foodCheckList.get(position).getName());
                        requestPackage.setParam("food_id",foodCheckList.get(position).getFoodId());
                        requestPackage.setParam("amount_claimed",claimText.getText().toString());
                        new ReceiverClaimTask().execute(requestPackage);
                    }



                }
            });

            return rootView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == 902 && resultCode == RESULT_OK){
            finish();
        }

    }


    public class DonorFoodCheckTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            DonorFoodCheckActivity.progress.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            Log.d("Jeeva", "DONOR_FOOD_CHECK_FROM_RECEIVER_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                    response = response.trim();

                    Log.d("Jeeva", "DONOR_FOOD_CHECK_FROM_RECEIVER_RESPONSE: " + response);



                        try {
                            JSONArray array = new JSONArray(response);

                            for(int i=0;i<array.length();i++){
                                JSONObject object = array.getJSONObject(i);
                                FoodCheckItem item = new FoodCheckItem();
                                item.setFoodId(object.getString("food_id"));
                                item.setAmountLeft(object.getString("amount_left"));
                                item.setDate(object.getString("date"));
                                item.setTime(object.getString("time"));
                                item.setName(object.getString("name"));
                                item.setExpire(object.getString("expire"));
                                foodCheckList.add(item);

                            }

                            for (int i=0;i<foodCheckList.size();i++) {
                                Log.d("Jeeva","FOOD_CHECK_FROM_RECEIVER_RESPONSE_ITEM: " + foodCheckList.get(i).getName());
                            }

                            adapter = new FoodCheckAdapter();
                            foodCheck.setAdapter(adapter);
                            DonorFoodCheckActivity.progress.setVisibility(View.GONE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DonorFoodCheckActivity.this, "No Food Available", Toast.LENGTH_SHORT).show();
                            DonorFoodCheckActivity.progress.setVisibility(View.GONE);
                        }


                }
                else{

                Toast.makeText(DonorFoodCheckActivity.this, "Error getting available food data from Donor", Toast.LENGTH_SHORT).show();
                DonorFoodCheckActivity.progress.setVisibility(View.GONE);
            }

        }

    }


    public class ReceiverClaimTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = ProgressDialog.show(DonorFoodCheckActivity.this, "",
                    "Requesting Food", true);
        }


        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            Log.d("Jeeva", "RECEIVER_CLAIM_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva", "RECEIVER_CLAIM_RESPONSE: " + response);


                if(response.equalsIgnoreCase("ok")){

                    dialog.dismiss();
                    Toast.makeText(DonorFoodCheckActivity.this, "Food Requested successfully", Toast.LENGTH_SHORT).show();
                    finish();

                }else if(response.equalsIgnoreCase("present")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(DonorFoodCheckActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            finish();
                        }
                    });
                    builder.setTitle("Wait For Previous Approval");
                    builder.setMessage("This food was requested previously by you and is pending for approval");
                    builder.show();

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(DonorFoodCheckActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.setTitle("Unknown Error");
                    builder.setMessage("Unable to claim food.Please try after a while");
                    builder.show();
                }





                try {


                } catch (Exception e) {
                    e.printStackTrace();

                }


            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(DonorFoodCheckActivity.this);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.setTitle("Network Error");
                builder.setMessage("\"No Internet Connection Available \\n Please try after connecting to a network !\"");
                builder.show();
            }

        }

    }

}
