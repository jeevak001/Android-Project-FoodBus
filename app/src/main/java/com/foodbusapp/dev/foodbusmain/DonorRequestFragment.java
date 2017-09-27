package com.foodbusapp.dev.foodbusmain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class DonorRequestFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private String userId;
    public ArrayList<RequestFood> foodRequestList;
    public Donor donor;
    ListView foodRequestView;
    FoodRequestAdapter adapter;
    Context ctx;
    ProgressDialog dialogRequest;

    @SuppressLint("ValidFragment")
    public DonorRequestFragment(Donor donor,Context ctx){

        foodRequestList = new ArrayList<>();
        this.donor = donor;
        this.ctx = ctx;
        DonorHomeActivity.toolbar.setTitle("View Requests");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donor_request, container, false);
        foodRequestView = (ListView) view.findViewById(R.id.donor_request_list);

        RequestPackage request = new RequestPackage();
        request.setUri(Utils.BASE_URL + "android/donor-request.php");
        request.setMethod("GET");
        request.setParam("user_id",donor.getUserId());
        new FoodRequestTask().execute(request);

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


    public class FoodRequestAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return foodRequestList.size();
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
                rootView = inflater.inflate(R.layout.donor_request_list_item, parent, false);

                final TextView fid = (TextView) rootView.findViewById(R.id.donor_request_food_id);
                Button rname = (Button) rootView.findViewById(R.id.donor_request_receiver_name);
                TextView name = (TextView) rootView.findViewById(R.id.donor_request_food_name);
                TextView amount = (TextView) rootView.findViewById(R.id.donor_request_food_amount);
                TextView tamount = (TextView) rootView.findViewById(R.id.donor_request_food_amount_total);
                TextView date = (TextView) rootView.findViewById(R.id.donor_request_food_date);
                TextView time = (TextView) rootView.findViewById(R.id.donor_request_food_time);
                Button a = (Button) rootView.findViewById(R.id.donor_request_accept);
                Button d = (Button) rootView.findViewById(R.id.donor_request_decline);

                rname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(getActivity(),ReceiverProfileCheckActivity.class);
                        i.putExtra("lat", foodRequestList.get(position).getLatitude());
                        i.putExtra("id", foodRequestList.get(position).getReceiverId());
                        i.putExtra("lng", foodRequestList.get(position).getLongitude());
                        i.putExtra("name",foodRequestList.get(position).getReceiverName());
                        i.putExtra("phone",foodRequestList.get(position).getPhone());
                        i.putExtra("door",foodRequestList.get(position).getDoor());
                        i.putExtra("area",foodRequestList.get(position).getArea());
                        i.putExtra("street",foodRequestList.get(position).getStreet());
                        i.putExtra("village",foodRequestList.get(position).getVillage());
                        i.putExtra("city",foodRequestList.get(position).getCity());
                        i.putExtra("pin",foodRequestList.get(position).getPin());
                        i.putExtra("email",foodRequestList.get(position).getEmail());
                        i.putExtra("profile",foodRequestList.get(position).getProfile());
                        i.putExtra("rating",foodRequestList.get(position).getRating());
                        i.putExtra("times",foodRequestList.get(position).getTimes());
                        startActivity(i);
                    }
                });

                a.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Log.d("Jeeva", "FOOD_ITEM_BUTTON_ACCEPT: " + fid.getText().toString());

                                RequestPackage r = new RequestPackage();
                                r.setMethod("GET");
                                r.setUri(Utils.BASE_URL + "android/donor-request-response.php");
                                r.setParam("receiver_id", foodRequestList.get(position).getReceiverId());
                                r.setParam("food_id", foodRequestList.get(position).getFoodId());
                                r.setParam("status", "yes");
                                r.setParam("donor_id", donor.getUserId());
                                r.setParam("amount", foodRequestList.get(position).getAmount());
                                dialogRequest = ProgressDialog.show(getActivity(), "",
                                        "Allocating Food", true);
                                new WebResponseTask().execute(r);
                            }
                        });
                        builder.setTitle("Accept Request");
                        builder.setMessage("Do you really want to accept the request ?");
                        builder.show();


                    }

                });

                d.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Log.d("Jeeva", "FOOD_ITEM_BUTTON_DECLINE: " + fid.getText().toString());

                                RequestPackage r = new RequestPackage();
                                r.setMethod("GET");
                                r.setUri(Utils.BASE_URL + "android/donor-request-response.php");
                                r.setParam("receiver_id", foodRequestList.get(position).getReceiverId());
                                r.setParam("food_id", foodRequestList.get(position).getFoodId());
                                r.setParam("status", "no");
                                dialogRequest = ProgressDialog.show(getActivity(), "",
                                        "Rejecting Food", true);
                                new WebResponseTask().execute(r);
                            }
                        });
                        builder.setTitle("Reject Request");
                        builder.setMessage("Do you really want to reject the request ?");
                        builder.show();


                    }

                });

                fid.setText("Food ID : " + foodRequestList.get(position).getFoodId());
                fid.setVisibility(View.GONE);
                name.setText(foodRequestList.get(position).getName());
                rname.setText(foodRequestList.get(position).getReceiverName());
                amount.setText("Requested Food Quantity: " + foodRequestList.get(position).getAmount());
                tamount.setText("Total Food Quantity: " + foodRequestList.get(position).getTotal());
                date.setText(Utils.getDate(foodRequestList.get(position).getDate()));
                time.setText(Utils.getTime(foodRequestList.get(position).getTime()));


            return rootView;

        }
    }






    public class FoodRequestTask extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva","FOOD_REQUEST_REQUEST: " +  params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                foodRequestList.clear();

                Log.d("Jeeva:", "FOOD_REQUEST_RESPONSE: " + response);

                try {
                    JSONArray array = new JSONArray(response);

                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        RequestFood food = new RequestFood();
                        food.setName(object.getString("name"));
                        food.setAmount(object.getString("amount_claimed"));
                        food.setReceiverId(object.getString("receiver_id"));
                        food.setReceiverName(object.getString("receiver_name"));
                        food.setTime(object.getString("time"));
                        food.setDate(object.getString("date"));
                        food.setFoodId(object.getString("food_id"));
                        food.setTotal(object.getString("total_amount"));

                        food.setLatitude(object.getString("lat"));
                        food.setLongitude(object.getString("lng"));
                        food.setPhone(object.getString("r_phone"));
                        food.setDoor(object.getString("r_door"));
                        food.setArea(object.getString("r_area"));
                        food.setStreet(object.getString("r_street"));
                        food.setVillage(object.getString("r_village"));
                        food.setCity(object.getString("r_city"));
                        food.setPin(object.getString("r_pin"));
                        food.setProfile(object.getString("r_profile"));
                        food.setRating(object.getString("r_rating"));
                        food.setTimes(object.getString("r_times"));
                        food.setEmail(object.getString("r_email"));


                        foodRequestList.add(food);
                    }

                    for (int i=0;i<foodRequestList.size();i++) {
                        Log.d("Jeeva", "FOOD_REQUEST_RESPONSE_ITEM: "+ foodRequestList.get(i).getName());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    ErrorFragment errorFragment = new ErrorFragment("No Food Requests Available !",ctx);
                    FragmentActivity fa = (FragmentActivity) ctx;
                    fa.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();

                }

                adapter  = new FoodRequestAdapter();
                foodRequestView.setAdapter(adapter);
                DonorHomeActivity.progress.setVisibility(View.GONE);

            }else{
                ErrorFragment errorFragment = new ErrorFragment("No Internet Connection Available \n Please try after connecting to a network !",ctx);
                FragmentActivity fa = (FragmentActivity) ctx;
                fa.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();
                DonorHomeActivity.progress.setVisibility(View.GONE);
            }

        }

    }

    public class WebResponseTask extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva","FOOD_ITEM_AD_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva","FOOD_ITEM_AD_RESPONSE: " + response);

                try {

                    if(response.equalsIgnoreCase("ok")){

                        Toast.makeText(getActivity(),"Request Responded Successfully" , Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialogRequest.dismiss();
                adapter  = new FoodRequestAdapter();
                foodRequestView.setAdapter(adapter);

                DonorRequestFragment requestFragment = new DonorRequestFragment(donor,ctx);
                FragmentActivity fragmentActivity = (FragmentActivity) ctx;
                fragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, requestFragment).commit();

            }else{
                ErrorFragment errorFragment = new ErrorFragment("Problem getting Food Requests \n Please try after a while !",ctx);
                FragmentActivity fa = (FragmentActivity) ctx;
                fa.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();
            }

        }

    }



}
