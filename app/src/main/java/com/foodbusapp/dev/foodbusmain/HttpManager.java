package com.foodbusapp.dev.foodbusmain;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpManager {

    public static String getData(RequestPackage request){

        BufferedReader reader = null;

        String uri = request.getUri();

        if(request.getMethod().equals("GET")){
            uri += "?" + request.getEncodedParameters();
        }

        Log.d("Jeeva","REQUEST_PARAMS: " + uri);

        String method = request.getMethod();

        try{

            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod(method);

            if(request.getMethod() .equals("POST")){
                con.setDoOutput(true);
                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
                writer.write(request.getEncodedParameters());
                writer.flush();
            }

            StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;

            while((line = reader.readLine()) != null){

                builder.append(line + "\n");
            }

            return builder.toString().trim();


        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
