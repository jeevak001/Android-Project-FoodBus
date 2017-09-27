package com.foodbusapp.dev.foodbusmain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class ReceiverRequestEditFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private Context ctx;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Receiver receiver;
    private ArrayList<ReceiverRequestFood> requestFoodList;

    public ListView requestView;


    @SuppressLint("ValidFragment")
    public ReceiverRequestEditFragment(Context ctx,Receiver receiver){
        this.ctx = ctx;
        this.receiver = receiver;
        requestFoodList = new ArrayList<>();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_receiver_request_edit, container, false);

        requestView = (ListView) view.findViewById(R.id.receiver_request_list);

        getRequestFoods();

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



    public void getRequestFoods() {

        RequestPackage rp = new RequestPackage();
        rp.setMethod("GET");
        rp.setUri(Utils.BASE_URL + "android/receiver-request-data.php");
        rp.setParam("user_id", receiver.getUserId());
        new RequestTask().execute(rp);

    }



    public class ReceiverRequestAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return requestFoodList.size();
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
            rootView = inflater.inflate(R.layout.receiver_request_list_item, parent, false);

            TextView foodName = (TextView) rootView.findViewById(R.id.receiver_request_food_name);
            TextView foodId = (TextView) rootView.findViewById(R.id.receiver_request_food_id);
            TextView foodDonor = (TextView) rootView.findViewById(R.id.receiver_request_donor_name);
            TextView foodAmountClaimed = (TextView) rootView.findViewById(R.id.receiver_request_food_amount_claimed);
            TextView foodAmountGot = (TextView) rootView.findViewById(R.id.receiver_request_food_amount_got);
            TextView foodAmountLeft = (TextView) rootView.findViewById(R.id.receiver_request_food_amount_left);
            TextView foodTime = (TextView) rootView.findViewById(R.id.receiver_request_time);
            TextView foodDate = (TextView) rootView.findViewById(R.id.receiver_request_date);
            TextView claimed = (TextView) rootView.findViewById(R.id.receiver_request_claimed);
            TextView approved = (TextView) rootView.findViewById(R.id.receiver_request_approved);
            TextView declined = (TextView) rootView.findViewById(R.id.receiver_request_declined);
            TextView status = (TextView) rootView.findViewById(R.id.receiver_request_food_status);
            Button revoke = (Button) rootView.findViewById(R.id.receiver_request_food_revoke);

            if(requestFoodList.get(position).getStatus().equalsIgnoreCase("1")){

                status.setText("Wasted");
                status.setBackgroundColor(Color.parseColor("#b54630"));
            }else{

                status.setText("Available");
                status.setBackgroundColor(Color.parseColor("#007766"));
            }


            revoke.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            RequestPackage requestPackage  = new RequestPackage();
                            requestPackage.setMethod("GET");
                            requestPackage.setUri(Utils.BASE_URL + "android/receiver-revoke-request.php");
                            requestPackage.setParam("request_id", requestFoodList.get(position).getRequestId());
                            new RequestRevokeTask().execute(requestPackage);


                        }
                    });
                    builder.setTitle("Delete Request");
                    builder.setMessage("This action will delete your request. You will not be able to get the requested food from the donor");
                    builder.show();

                }
            });

            foodName.setText(requestFoodList.get(position).getName());
            foodId.setText("Food ID: " + requestFoodList.get(position).getFoodId());
            foodId.setVisibility(View.GONE);
            foodAmountClaimed.setText("Requested Food Quantity: " + requestFoodList.get(position).getAmountClaimed());
            foodAmountLeft.setText("Remaining Food Quantity: " + requestFoodList.get(position).getAmountLeft());
            foodAmountLeft.setVisibility(View.GONE);
            foodAmountGot.setText("Approved Food Quantity: " + requestFoodList.get(position).getAmountGot());
            foodDate.setText(Utils.getDate(requestFoodList.get(position).getDate()));
            foodTime.setText(Utils.getTime(requestFoodList.get(position).getTime()));

            foodDonor.setText("Donor: " + requestFoodList.get(position).getDonorName());

            claimed.setText(Utils.boolStatus(requestFoodList.get(position).getClaimRequest()));
            approved.setText(Utils.boolStatus(requestFoodList.get(position).getApproved()));
            declined.setText(Utils.boolStatus(requestFoodList.get(position).getDeclined()));



            return rootView;
        }
    }









    public class RequestTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            ReceiverHomeActivity.progress.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onProgressUpdate(String... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            Log.d("Jeeva","RECEIVER_REQUEST_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva","RECEIVER_REQUEST_RESPONSE: " + response);

                try {
                    JSONArray array = new JSONArray(response);

                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        ReceiverRequestFood requestFood = new ReceiverRequestFood();
                        requestFood.setName(object.getString("name"));
                        requestFood.setFoodId(object.getString("food_id"));
                        requestFood.setDonorId(object.getString("donor_id"));
                        requestFood.setTime(object.getString("time"));
                        requestFood.setDate(object.getString("date"));
                        requestFood.setAmountClaimed(object.getString("amount_claimed"));
                        requestFood.setAmountGot(object.getString("amount_got"));
                        requestFood.setRequestId(object.getString("request_id"));
                        requestFood.setClaimRequest(object.getString("claim_request"));
                        requestFood.setApproved(object.getString("approved"));
                        requestFood.setDeclined(object.getString("declined"));
                        requestFood.setAmountLeft(object.getString("amount_left"));
                        requestFood.setDonorName(object.getString("donor_name"));
                        requestFood.setStatus(object.getString("is_wasted"));
                        requestFoodList.add(requestFood);
                    }

                    for (int i=0;i<requestFoodList.size();i++) {
                        Log.d("Jeeva", "RECEIVER_FOOD_REQUEST_RESPONSE_ITEM: "+ requestFoodList.get(i).getName());
                    }

                    ReceiverRequestAdapter adapter  = new ReceiverRequestAdapter();
                    requestView.setAdapter(adapter);
                    ReceiverHomeActivity.progress.setVisibility(View.GONE);



                } catch (JSONException e) {
                    e.printStackTrace();

                    ErrorFragment errorFragment = new ErrorFragment("No Requests Available",ctx);
                    FragmentActivity fa = (FragmentActivity) ctx;
                    fa.getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, errorFragment).commit();
                    ReceiverHomeActivity.progress.setVisibility(View.GONE);

                }



            }else{
                ErrorFragment errorFragment = new ErrorFragment("No Internet Connection Available \n Please try after connecting to a network !",ctx);
                FragmentActivity fa = (FragmentActivity) ctx;
                fa.getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, errorFragment).commit();
                ReceiverHomeActivity.progress.setVisibility(View.GONE);
            }

        }

    }


    public class RequestRevokeTask extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva","RECEIVER_REQUEST_REVOKE_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva","RECEIVER_REQUEST_REVOKE_RESPONSE: " + response);

                ReceiverRequestEditFragment homeFragment = new ReceiverRequestEditFragment(ctx,receiver);
                FragmentActivity fragmentActivity = (FragmentActivity) ctx;
                fragmentActivity.getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, homeFragment).commit();



            }else{
                ErrorFragment errorFragment = new ErrorFragment("No Internet Connection Available \n Please try after connecting to a network !",ctx);
                FragmentActivity fa = (FragmentActivity) ctx;
                fa.getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, errorFragment).commit();
            }

        }

    }



}
