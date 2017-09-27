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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiverClaimActivity extends AppCompatActivity {

    TextView tname;
    TextView tdonorName;
    TextView tfoodId;
    TextView tamountLeft;
    Button claimButton;
    EditText claimText;
    ProgressDialog dialog;
    Intent i;

    String name;
    String foodId;
    String amountLeft;
    String receiverId;
    String donorId;
    String donorName;
    String foodExpire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_claim);
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
        amountLeft = i.getExtras().getString("amount_left");
        receiverId = i.getExtras().getString("receiver_id");
        donorId = i.getExtras().getString("donor_id");
        donorName = i.getExtras().getString("donor_name");
        foodId = i.getExtras().getString("food_id");
        foodExpire = i.getExtras().getString("food_expire");

        tname = (TextView) findViewById(R.id.claim_name);
        tdonorName = (TextView) findViewById(R.id.claim_donor_name);
        tfoodId = (TextView) findViewById(R.id.claim_id);
        tamountLeft = (TextView) findViewById(R.id.claim_amount_left);
        claimText = (EditText) findViewById(R.id.claim_amount_text);
        claimButton = (Button) findViewById(R.id.claim_button);

        claimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(claimText.getText().toString().equalsIgnoreCase("")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverClaimActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.setTitle("Invalid Claim Amount");
                    builder.setMessage("Please Enter a valid Number");
                    builder.show();

                }else if(Integer.parseInt(claimText.getText().toString()) > Integer.parseInt(amountLeft)){

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverClaimActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.setTitle("More Claim Amount");
                    builder.setMessage("You cannot claim more than the amount available");
                    builder.show();

                }else if(Integer.parseInt(claimText.getText().toString()) <= 0){

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverClaimActivity.this);
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
                    requestPackage.setParam("receiver_id", receiverId);
                    requestPackage.setParam("donor_id",donorId);
                    requestPackage.setParam("name",name);
                    requestPackage.setParam("food_id",foodId);
                    requestPackage.setParam("amount_claimed",claimText.getText().toString());
                    new ReceiverClaimTask().execute(requestPackage);
                }


            }
        });

        String sDate = Utils.getDate(foodExpire.substring(0,10));
        String sTime = Utils.getTime(foodExpire.substring(11,19));

        tname.setText(name);
        tdonorName.setText(donorName);
        tfoodId.setText("Food Expires: " + sTime + " " + sDate);
        tamountLeft.setText("Remaining Food Quantity: " + amountLeft);




    }



    public class ReceiverClaimTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = ProgressDialog.show(ReceiverClaimActivity.this, "",
                    "Claiming Food", true);
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
                    Toast.makeText(ReceiverClaimActivity.this,"Food claimed successfully",Toast.LENGTH_SHORT).show();


                }else if(response.equalsIgnoreCase("present")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverClaimActivity.this);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


                        }
                    });
                    builder.setTitle("Wait For Previous Approval");
                    builder.setMessage("This food was requested previously by you and is pending for approval");
                    builder.show();

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverClaimActivity.this);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(ReceiverClaimActivity.this);
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
