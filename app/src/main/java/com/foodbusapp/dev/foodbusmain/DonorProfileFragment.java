package com.foodbusapp.dev.foodbusmain;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


@SuppressLint("ValidFragment")
public class DonorProfileFragment extends Fragment {

    Bitmap b;
    ImageView profile;
    Context ctx;
    RatingBar ratingBar;


    private OnFragmentInteractionListener mListener;
    private Donor donor;

    @SuppressLint("ValidFragment")
    public DonorProfileFragment(Donor d,Context ctx) {
        this.donor = d;
        this.ctx = ctx;
        DonorHomeActivity.toolbar.setTitle("Profile");

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donor_profile, container, false);


        TextView name = (TextView) view.findViewById(R.id.profile_name);
        TextView email = (TextView) view.findViewById(R.id.profile_email);
        TextView address = (TextView) view.findViewById(R.id.profile_address);
        TextView phone = (TextView) view.findViewById(R.id.profile_phone);
        profile = (ImageView) view.findViewById(R.id.profile_image);
        ratingBar = (RatingBar) view.findViewById(R.id.donor_profile_rating);

        try{
            int average = (Integer.parseInt(DonorHomeActivity.donor.getRating())/Integer.parseInt(DonorHomeActivity.donor.getTimes()));
            ratingBar.setRating(average);
        }catch(Exception e){
            ratingBar.setRating(0);
        }


        Button checkLocation = (Button) view.findViewById(R.id.check_location);

        checkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LocationActivity.class);
                i.putExtra("lat", donor.getLatitude());
                i.putExtra("lng", donor.getLongitude());
                i.putExtra("name", donor.getName());
                i.putExtra("phone",donor.getPhone());
                i.putExtra("door",donor.getDoor());
                i.putExtra("area",donor.getArea());
                i.putExtra("street",donor.getStreet());
                i.putExtra("village",donor.getVillage());
                i.putExtra("city", donor.getCity());
                i.putExtra("pin",donor.getPin());
                startActivity(i);
            }
        });


        Log.d("Jeeva","PROFILE_IMAGE_REQUEST: " + Utils.BASE_URL + donor.getProfile());

        if(donor.getImage() == null){
            new ProfileTask().execute(Utils.BASE_URL + donor.getProfile());
        }else{
            profile.setImageBitmap(donor.getImage());
        }


        name.setText(donor.getName());
        email.setText(donor.getEmail());
        phone.setText(donor.getPhone());
        address.setText("#" + donor.getDoor() + ", " + donor.getStreet() + ",\n" + donor.getArea() + ",\n" + donor.getVillage() + ",\n" + donor.getCity() + ",\n"
                + donor.getPin()+".");

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
            DonorHomeActivity.progress.setVisibility(View.VISIBLE);
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

                ErrorFragment errorFragment = new ErrorFragment("Problem getting Profile Image \n Please try after a while !",ctx);
                FragmentActivity fa = (FragmentActivity) ctx;
                fa.getSupportFragmentManager().beginTransaction().replace(R.id.donor_fragment, errorFragment).commit();

                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(Bitmap b) {

            super.onPostExecute(b);

            profile.setImageBitmap(b);
            DonorHomeActivity.progress.setVisibility(View.GONE);


        }

    }
}
