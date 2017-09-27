package com.foodbusapp.dev.foodbusmain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class ReceiverTrackRequestFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private Context ctx;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Receiver receiver;
    private ArrayList<TrackItem> requestTrackList;

    public ListView requestTrackView;


    @SuppressLint("ValidFragment")
    public ReceiverTrackRequestFragment(Context ctx, Receiver receiver){
        this.ctx = ctx;
        this.receiver = receiver;
        requestTrackList = new ArrayList<>();
        ReceiverHomeActivity.toolbar.setTitle("Track Requests");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_receiver_track_request, container, false);

        requestTrackView = (ListView) view.findViewById(R.id.receiver_track_request_list);

        getRequestTrackOptions();

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



    public void getRequestTrackOptions() {

        RequestPackage rp = new RequestPackage();
        rp.setMethod("GET");
        rp.setUri(Utils.BASE_URL + "android/receiver-get-approved-request.php");
        rp.setParam("user_id", receiver.getUserId());
        new RequestTrackTask().execute(rp);

    }



    public class ReceiverTrackRequestAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return requestTrackList.size();
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
            rootView = inflater.inflate(R.layout.receiver_request_track_list, parent, false);

            TextView requestName = (TextView) rootView.findViewById(R.id.receiver_request_track_food_name);
            TextView requestId = (TextView) rootView.findViewById(R.id.receiver_request_track_food_id);
            TextView requestDonor = (TextView) rootView.findViewById(R.id.receiver_request_track_donor_name);
            TextView requestAmountClaimed = (TextView) rootView.findViewById(R.id.receiver_request_track_food_amount_claimed);
            TextView requestAmountGot = (TextView) rootView.findViewById(R.id.receiver_request_track_food_amount_got);
            TextView requestAmountLeft = (TextView) rootView.findViewById(R.id.receiver_request_track_food_amount_left);
            TextView requestTime = (TextView) rootView.findViewById(R.id.receiver_request_track_time);
            TextView requestDate = (TextView) rootView.findViewById(R.id.receiver_request_track_date);
            TextView requestStatus = (TextView) rootView.findViewById(R.id.receiver_request_track_food_status);
            Button trackButton = (Button) rootView.findViewById(R.id.receiver_request_track_food_track);
            Button completeButton = (Button) rootView.findViewById(R.id.receiver_request_track_food_complete);

            final RatingBar rating = (RatingBar) rootView.findViewById(R.id.receiver_request_rating);


            trackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getActivity(),ReceiverMapTrackActivity.class);
                    i.putExtra("lat",requestTrackList.get(position).getActorLat());
                    i.putExtra("lng",requestTrackList.get(position).getActorLng());
                    i.putExtra("food_id",requestTrackList.get(position).getFoodId());
                    i.putExtra("name",requestTrackList.get(position).getActorName());
                    i.putExtra("door",requestTrackList.get(position).getActorDoor());
                    i.putExtra("street",requestTrackList.get(position).getActorStreet());
                    i.putExtra("area",requestTrackList.get(position).getActorArea());
                    i.putExtra("village",requestTrackList.get(position).getActorVillage());
                    i.putExtra("city", requestTrackList.get(position).getActorCity());
                    i.putExtra("pin", requestTrackList.get(position).getActorPin());
                    i.putExtra("phone", requestTrackList.get(position).getActorPhone());

                    startActivity(i);
                }
            });



            completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            RequestPackage rp = new RequestPackage();
                            rp.setParam("request_id", requestTrackList.get(position).getRequestId());
                            rp.setParam("rating", String.valueOf(rating.getRating()));
                            rp.setMethod("GET");
                            rp.setUri(Utils.BASE_URL + "android/receiver-request-complete.php");
                            new RequestCompleteTask().execute(rp);

                            Log.d("Jeeva", "DONOR_REQUEST_TRACK");
                            ReceiverTrackRequestFragment trackFragment = new ReceiverTrackRequestFragment(ctx, receiver);
                            FragmentActivity fragmentActivity = (FragmentActivity) ctx;
                            ((FragmentActivity) ctx).getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, trackFragment).commit();
                        }
                    });
                    builder.setTitle("Request Complete");
                    builder.setMessage("Do you want to mark the request as completed");
                    builder.show();


                }
            });

            if(requestTrackList.get(position).getStatus().equalsIgnoreCase("1")){

                requestStatus.setText("Wasted");
                requestStatus.setBackgroundColor(Color.parseColor("#b54630"));
            }else{

                requestStatus.setText("Available");
                requestStatus.setBackgroundColor(Color.parseColor("#007766"));
            }

            requestName.setText("Food: " + requestTrackList.get(position).getFoodName());
            requestDonor.setText("Donor: " + requestTrackList.get(position).getActorName());
            requestId.setText("Food ID: " + requestTrackList.get(position).getFoodId());
            requestId.setVisibility(View.GONE);
            requestAmountClaimed.setText("Requested Food Quantity: " + requestTrackList.get(position).getAmountClaimed());
            requestAmountLeft.setText("Remaining Food Quantity: " +requestTrackList.get(position).getAmountLeft());
            requestAmountLeft.setVisibility(View.GONE);
            requestAmountGot.setText("Approved Food Quantity: " +requestTrackList.get(position).getAmountGot());
            requestDate.setText(Utils.getDate(requestTrackList.get(position).getDate()));
            requestTime.setText(Utils.getTime(requestTrackList.get(position).getTime()));

            return rootView;
        }
    }



    public class RequestTrackTask extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva","RECEIVER_TRACK_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva", "RECEIVER_TRACK_RESPONSE: " + response);

                try {
                    JSONArray array = new JSONArray(response);
                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        TrackItem item = new TrackItem();
                        item.setFoodName(object.getString("name"));
                        item.setFoodId(object.getString("food_id"));
                        item.setTime(object.getString("time"));
                        item.setDate(object.getString("date"));
                        item.setAmountClaimed(object.getString("amount_claimed"));
                        item.setActorName(object.getString("donor_name"));
                        item.setActorLat(object.getString("donor_lat"));
                        item.setActorLng(object.getString("donor_lng"));
                        item.setAmountGot(object.getString("amount_got"));
                        item.setAmountLeft(object.getString("amount_left"));
                        item.setActorDoor(object.getString("donor_door"));
                        item.setActorArea(object.getString("donor_area"));
                        item.setActorVillage(object.getString("donor_village"));
                        item.setActorStreet(object.getString("donor_street"));
                        item.setActorCity(object.getString("donor_city"));
                        item.setActorPin(object.getString("donor_pin"));
                        item.setActorPhone(object.getString("donor_phone"));
                        item.setRequestId(object.getString("request_id"));
                        item.setStatus(object.getString("is_wasted"));
                        requestTrackList.add(item);
                    }



                    for (int i=0;i<requestTrackList.size();i++) {
                        Log.d("Jeeva", "RECEIVE_TRACK_RESPONSE_ITEM: "+ requestTrackList.get(i).getActorName());
                    }

                    ReceiverTrackRequestAdapter adapter  = new ReceiverTrackRequestAdapter();
                    requestTrackView.setAdapter(adapter);
                    ReceiverHomeActivity.progress.setVisibility(View.GONE);



                } catch (JSONException e) {
                    e.printStackTrace();

                    ErrorFragment errorFragment = new ErrorFragment("No Request to Track.",ctx);
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

    public class RequestCompleteTask extends AsyncTask<RequestPackage,String,String> {

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

            Log.d("Jeeva","RECEIVER_COMPLETE_REQUEST: " + params[0].getUri());

            String data = HttpManager.getData(params[0]);

            return data;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            String response = s;

            if(response != null){

                response = response.trim();

                Log.d("Jeeva", "RECEIVER_COMPLETE_RESPONSE: " + response);


            }else{
                ErrorFragment errorFragment = new ErrorFragment("Problem completing  Request  \n Please try after a while !",ctx);
                FragmentActivity fa = (FragmentActivity) ctx;
                fa.getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, errorFragment).commit();
            }

        }

    }






}
