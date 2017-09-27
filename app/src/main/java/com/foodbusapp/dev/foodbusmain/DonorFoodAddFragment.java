package com.foodbusapp.dev.foodbusmain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class DonorFoodAddFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    public Donor donor;
    public Spinner spinner;
    public Spinner spinnerExpire;
    public SpinnerAdapter spinnerAdapter;
    public ExpireSpinnerAdapter spinnerExpireAdapter;
    public Button addButton;
    public EditText name;
    public EditText amount;
    public String status = "available";
    private String expire = "72";
    Context ctx;
    ProgressDialog dialog;

    @SuppressLint("ValidFragment")
    public DonorFoodAddFragment(Donor donor,Context ctx){

        this.donor = donor;
        this.ctx = ctx;
        DonorHomeActivity.toolbar.setTitle("Add Food");
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donor_food_add, container, false);

        //spinner = (Spinner) view.findViewById(R.id.food_add_status);
        spinnerExpire = (Spinner) view.findViewById(R.id.food_add_expire);
        //spinnerAdapter = new SpinnerAdapter();
        spinnerExpireAdapter = new ExpireSpinnerAdapter();
        //spinner.setAdapter(spinnerAdapter);
        spinnerExpire.setAdapter(spinnerExpireAdapter);
        addButton = (Button) view.findViewById(R.id.add_button);
        name = (EditText) view.findViewById(R.id.food_add_name);
        amount = (EditText) view.findViewById(R.id.food_add_amount);

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




        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String foodName = name.getText().toString();
                String foodAmount = amount.getText().toString();
                String foodStatus = status;

                if(foodName.equalsIgnoreCase("")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.setTitle("Invalid Food name");
                    builder.setMessage("You must provide a name for the food");
                    builder.show();

                }else if(foodAmount.equalsIgnoreCase("")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.setTitle("Invalid Food quantity");
                    builder.setMessage("You must provide a valid quantity for the food");
                    builder.show();

                }else{

                    RequestPackage requestPackage = new RequestPackage();
                    requestPackage.setMethod("GET");
                    requestPackage.setUri(Utils.BASE_URL + "android/donor-food-add.php");
                    requestPackage.setParam("user_id", donor.getUserId());
                    requestPackage.setParam("name", foodName);
                    requestPackage.setParam("amount",foodAmount);
                    requestPackage.setParam("expire", expire);

                    new WebTask().execute(requestPackage);

                }






            }
        });

        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    public class SpinnerAdapter extends BaseAdapter{

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


                LayoutInflater inflater = (LayoutInflater)LayoutInflater.from(getActivity());
                rootView = inflater.inflate(R.layout.spinner_status_item, parent, false);

                if(position == 0){

                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_item);
                    t.setText("Available");
                    t.setBackgroundColor(Color.parseColor("#007766"));

                }else if(position == 1){
                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_item);
                    t.setText("Claimed");
                    t.setBackgroundColor(Color.parseColor("#FF6138"));

                }else{
                    TextView t = (TextView) rootView.findViewById(R.id.food_add_status_item);
                    t.setText("Wasted");
                    t.setBackgroundColor(Color.parseColor("#C9452A"));
                }

            }

            return rootView;
        }
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


                LayoutInflater inflater = (LayoutInflater)LayoutInflater.from(getActivity());
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
            dialog = ProgressDialog.show(getActivity(), "",
                    "Adding Food", true);
        }


        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            Log.d("Jeeva", "FOOD_ADD_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva","FOOD_ADD_RESPONSE: " +  response);


                if(response.equalsIgnoreCase("ok")){
                    DonorHomeFragment homeFragment = new DonorHomeFragment(donor,ctx);
                    FragmentActivity fragmentActivity = (FragmentActivity) ctx;
                    fragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment,homeFragment).commit();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.setTitle("Unknown Error");
                    builder.setMessage("The Food was not added due to some unknown error.Please try after a while");
                    builder.show();
                }


                dialog.dismiss();
                Toast.makeText(getActivity(),"Food added successfully",Toast.LENGTH_SHORT).show();

                try {


                } catch (Exception e) {
                    e.printStackTrace();

                }


            }else{
                ErrorFragment errorFragment = new ErrorFragment("Problem addding Food \n No Internet Connection Available \n Please try after connecting to a network !",ctx);
                FragmentActivity fragmentActivity = (FragmentActivity) ctx;
                fragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();
            }

        }

    }
}
