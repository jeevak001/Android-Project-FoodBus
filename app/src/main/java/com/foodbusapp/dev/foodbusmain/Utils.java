package com.foodbusapp.dev.foodbusmain;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;

/**
 * Created by Jeeva on 3/7/2016.
 */
public class Utils {

    public static String USER_ID = "";
    public static String LAT = "";
    public static String LNG = "";
    public static String NAME = "";
    public static String DOOR = "";
    public static String STREET = "";
    public static String AREA = "";
    public static String VILLAGE = "";
    public static String CITY = "";
    public static String PIN = "";
    public static String PHONE = "";


    static final String APP_SERVER_URL = "http://foodbusapp.com/FoodWebApp/gcm/";
    static final String SENDER_ID = "599060432739";
    static final String MSG_KEY = "m";

    public static final String BASE_URL = "http://foodbusapp.com/FoodWebApp/";

    public static boolean isOnline(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null && info.isConnectedOrConnecting()){
            return true;
        }else{
            return false;
        }
    }

    public static String getDate(String date){

        String[] months = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        String year = date.substring(0, 4);
        String month = date.substring(5,7);
        String day = date.substring(8,10);

        int a = Integer.parseInt(month);
        month = months[a - 1];

        return day + "," + month + " " + year;
    }


    // 22-25-96
    public static String getTime(String time){

        String hrs = time.substring(0,2);
        String min = time.substring(3,5);
        String type = "AM";

        if(Integer.parseInt(hrs) > 12){
            hrs = String.valueOf(Integer.parseInt(hrs) - 12);
            type = "PM";
        }

        return hrs + ":" + min + " " + type;

    }


    public static String boolStatus(String t){

        if(t.equalsIgnoreCase("1")){
            return "Yes";
        }else
            return "No";
    }

    public static Double sToD(String tmp){

        return Double.parseDouble(tmp);
    }
}
