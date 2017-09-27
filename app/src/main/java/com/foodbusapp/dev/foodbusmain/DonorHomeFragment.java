package com.foodbusapp.dev.foodbusmain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class DonorHomeFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private String userId;
    public ArrayList<Food> foodList;
    public Donor donor;
    ListView foodListView;
    FoodAdapter adapter;
    private Context ctx;
    ProgressDialog dialog;




    @SuppressLint("ValidFragment")
    public DonorHomeFragment(Donor donor,Context ctx){

        foodList = new ArrayList<>();
        this.donor = donor;
        this.ctx = ctx;
        DonorHomeActivity.toolbar.setTitle("My Food");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donor_home, container, false);
        foodListView = (ListView) view.findViewById(R.id.donor_home_list);

        RequestPackage request = new RequestPackage();
        request.setUri( Utils.BASE_URL + "android/get-donor-food-data.php");
        request.setMethod("GET");
        request.setParam("user_id",donor.getUserId());
        new WebTask().execute(request);

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


    public class FoodAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return foodList.size();
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

                LayoutInflater inflater = (LayoutInflater)LayoutInflater.from(getActivity());
                rootView = inflater.inflate(R.layout.donor_home_list_item, parent, false);

                final TextView texpire = (TextView) rootView.findViewById(R.id.donor_home_food_id);
                TextView name = (TextView) rootView.findViewById(R.id.donor_home_food_name);
                TextView amount = (TextView) rootView.findViewById(R.id.donor_home_food_amount);
                TextView amountLeft = (TextView) rootView.findViewById(R.id.donor_home_food_amount_left);
                TextView date = (TextView) rootView.findViewById(R.id.donor_home_food_date);
                TextView time = (TextView) rootView.findViewById(R.id.donor_home_food_time);
                TextView status = (TextView) rootView.findViewById(R.id.donor_home_food_status);
                Button b = (Button) rootView.findViewById(R.id.donor_home_food_edit);
                Button r = (Button) rootView.findViewById(R.id.donor_home_food_remove);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                editFood(position);
                            }
                        });
                        builder.setTitle("Edit Food");
                        builder.setMessage("Do you want to edit the food ?");
                        builder.show();


                    }

                });

                r.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        removeFood(position);


                    }

                });

                String sDate = Utils.getDate(foodList.get(position).getExpire().substring(0,10));
                String sTime = Utils.getTime(foodList.get(position).getExpire().substring(11, 19));

                texpire.setText("Food Expires: " + sTime + " " + sDate);
                name.setText(foodList.get(position).getName());
                amount.setText("Total Food Quantity: " + foodList.get(position).getAmount());
                date.setText(Utils.getDate(foodList.get(position).getDate()));
                time.setText(Utils.getTime(foodList.get(position).getTime()));
                amountLeft.setText("Remaining Food Quantity: " + foodList.get(position).getAmountLeft());
                status.setText(foodList.get(position).getStatus());

                if(foodList.get(position).getStatus().equalsIgnoreCase("available")){
                    status.setText("Available");
                    status.setBackgroundColor(Color.parseColor("#468966"));
                }else if(foodList.get(position).getStatus().equalsIgnoreCase("claimed")){
                    status.setText("Claimed");
                    status.setBackgroundColor(Color.parseColor("#FF6138"));
                }else{
                    status.setText("Wasted");
                    texpire.setText("Food Expired");
                    status.setBackgroundColor(Color.parseColor("#BD4932"));
                }


            return rootView;

        }
    }

    public void editFood(int position){


        Intent i = new Intent(getActivity(),FoodEditActivity.class);
        i.putExtra("user_id",donor.getUserId());
        i.putExtra("food_id",foodList.get(position).getFoodId());
        i.putExtra("name", foodList.get(position).getName());
        i.putExtra("amount",foodList.get(position).getAmount());
        i.putExtra("status", foodList.get(position).getStatus());
        startActivityForResult(i,200);

    }

    public void removeFood(int position){

        final String foodId = foodList.get(position).getFoodId();
        final String userId = donor.getUserId();


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                RequestPackage rp = new RequestPackage();
                rp.setMethod("GET");
                rp.setParam("food_id", foodId);
                rp.setParam("user_id", userId);
                rp.setUri(Utils.BASE_URL + "android/donor-food-remove.php");
                new FoodRemoveTask().execute(rp);
            }
        });
        builder.setTitle("Food Remove");
        builder.setMessage("This action will remove the food and cannot be reverted");
        builder.show();




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



            Log.d("Jeeva", "FOOD_DETAILS_REQUEST: " +  params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);



            String response = s;

            if(response != null){

                if(response == ""){

                    ErrorFragment errorFragment = new ErrorFragment("No Food added yet !",ctx);
                    FragmentActivity fa = (FragmentActivity) ctx;
                    fa.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();
                    DonorHomeActivity.progress.setVisibility(View.GONE);

                }else{
                    response = response.trim();

                    foodList.clear();

                    Log.d("Jeeva","FOOD_DETAILS_RESPONSE: " + response);

                    try {
                        JSONArray array = new JSONArray(response);

                        for(int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            Food food = new Food();
                            food.setName(object.getString("name"));
                            food.setAmount(object.getString("amount"));
                            food.setTime(object.getString("time"));
                            food.setDate(object.getString("date"));
                            food.setAmountLeft(object.getString("amount_left"));
                            food.setFoodId(object.getString("food_id"));
                            food.setStatus(object.getString("status"));
                            food.setExpire(object.getString("expire"));
                            foodList.add(food);
                        }

                        for (int i=0;i<foodList.size();i++) {
                            Log.d("Jeeva","FOOD_DETAILS_RESPONSE_ITEM: " + foodList.get(i).getName());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    adapter  = new FoodAdapter();
                    foodListView.setAdapter(adapter);
                    DonorHomeActivity.progress.setVisibility(View.GONE);
                }



            }else{
                ErrorFragment errorFragment = new ErrorFragment("No Internet Connection Available \n Please try after connecting to a network !",ctx);
                FragmentActivity fa = (FragmentActivity) ctx;
                fa.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();
                DonorHomeActivity.progress.setVisibility(View.GONE);
            }

        }

    }



    public class FoodRemoveTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = ProgressDialog.show(getActivity(), "",
                    "Removing Food", true);

        }


        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            Log.d("Jeeva","FOOD_REMOVE_REQUEST: " +  params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva","FOOD_REMOVE_RESPONSE: " + response);

                if(response.equalsIgnoreCase("ok")){
                    dialog.dismiss();
                    Toast.makeText(getActivity(),"Food Removed Successfully",Toast.LENGTH_SHORT).show();
                    DonorHomeFragment homeFragment = new DonorHomeFragment(donor,ctx);
                    FragmentActivity fragmentActivity = (FragmentActivity) ctx;
                    fragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, homeFragment).commit();
                }else if(response.equalsIgnoreCase("error")){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.setTitle("Requested Food");
                    builder.setMessage("You cannot remove a food which is requested by user.");
                    builder.show();

                }else{

                    adapter  = new FoodAdapter();
                    foodListView.setAdapter(adapter);
                }



            }else{
                ErrorFragment errorFragment = new ErrorFragment("Problem removing Food item \n Please try after a while !",ctx);
                FragmentActivity fa = (FragmentActivity) ctx;
                fa.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();
            }

        }

    }






}
