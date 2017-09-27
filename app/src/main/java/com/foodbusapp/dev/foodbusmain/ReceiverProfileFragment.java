package com.foodbusapp.dev.foodbusmain;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


@SuppressLint("ValidFragment")
public class ReceiverProfileFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private Context ctx;
    Bitmap b;
    ImageView profile;

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Receiver receiver;
    private ArrayList<ReceiverRequestFood> requestFoodList;

    public ListView requestView;
    private RatingBar ratingBar;


    @SuppressLint("ValidFragment")
    public ReceiverProfileFragment(Context ctx, Receiver receiver){
        this.ctx = ctx;
        this.receiver = receiver;
        ReceiverHomeActivity.toolbar.setTitle("Profile");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_receiver_profile, container, false);

        TextView name = (TextView) view.findViewById(R.id.profile_name);
        TextView email = (TextView) view.findViewById(R.id.profile_email);
        TextView address = (TextView) view.findViewById(R.id.profile_address);
        TextView phone = (TextView) view.findViewById(R.id.profile_phone);
        profile = (ImageView) view.findViewById(R.id.profile_image);
        ratingBar = (RatingBar) view.findViewById(R.id.profile_rating);

        try{
            int average = (Integer.parseInt(ReceiverHomeActivity.receiver.getRating())/Integer.parseInt(ReceiverHomeActivity.receiver.getTimes()));
            ratingBar.setRating(average);
        }catch(Exception e){
            ratingBar.setRating(0);
        }

        Button checkLocation = (Button) view.findViewById(R.id.check_location);

        checkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LocationActivity.class);
                i.putExtra("lat", receiver.getLatitude());
                i.putExtra("lng", receiver.getLongitude());
                i.putExtra("name", receiver.getName());
                i.putExtra("phone",receiver.getPhone());
                i.putExtra("door",receiver.getDoor());
                i.putExtra("area",receiver.getArea());
                i.putExtra("street",receiver.getStreet());
                i.putExtra("village",receiver.getVillage());
                i.putExtra("city",receiver.getCity());
                i.putExtra("pin",receiver.getPin());

                startActivity(i);


            }
        });


        Log.d("Jeeva","PROFILE_IMAGE_REQUEST: " + Utils.BASE_URL + receiver.getProfile());

        if(receiver.getImage() == null){
            new ProfileTask().execute(Utils.BASE_URL + receiver.getProfile());
        }else{
            profile.setImageBitmap(receiver.getImage());
        }


        name.setText(receiver.getName());
        email.setText(receiver.getEmail());
        phone.setText(receiver.getPhone());
        address.setText("#" + receiver.getDoor() + ", " + receiver.getStreet() + ",\n" + receiver.getArea() + ",\n" + receiver.getVillage() + ",\n" + receiver.getCity() + ",\n"
                + receiver.getPin()+".");



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


    public class ProfileTask extends AsyncTask<String,String,Bitmap> {

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

                Log.d("Jeeva","PROFILE_IMAGE_RESPONSE: " + "Error");

                ErrorFragment errorFragment = new ErrorFragment("No Internet Connection Available \n Please try after connecting to a network !",ctx);
                FragmentActivity fa = (FragmentActivity) ctx;
                fa.getSupportFragmentManager().beginTransaction().replace(R.id.receiver_fragment, errorFragment).commit();

                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(Bitmap b) {

            super.onPostExecute(b);

            profile.setImageBitmap(b);
            ReceiverHomeActivity.progress.setVisibility(View.GONE);


        }

    }






}
