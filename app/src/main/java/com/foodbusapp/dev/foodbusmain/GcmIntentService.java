package com.foodbusapp.dev.foodbusmain;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Jeeva on 3/13/2016.
 */
public class GcmIntentService extends IntentService{

    public static  int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService(){
        super("GcmIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        Log.d("Jeeva", "in gcm intent message " + messageType);
        Log.d("Jeeva","in gcm intent message bundle "+extras);

        if (!extras.isEmpty()){

            if(GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){

                sendNotification("Send error: " + extras.toString());

            }else if(GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){

                sendNotification("Deleted Messages on server: " + extras.toString());

            }else if(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){

                String foodName = intent.getStringExtra("food_name");
                String donorName = intent.getStringExtra("donor_name");
                String amountLeft = intent.getStringExtra("amount_left");

                String donorFoodName = intent.getStringExtra("food_name");
                String receiverName = intent.getStringExtra("receiver_name");
                String amountClaimed = intent.getStringExtra("amount_claimed");

                String dName = intent.getStringExtra("d_name");
                String fName = intent.getStringExtra("f_name");

                String donorAcceptedFood = intent.getStringExtra("food_accepted_name");
                String donorAccepteddonor = intent.getStringExtra("donor_accepted_name");
                String donorAcceptedAmount = intent.getStringExtra("donor_accepted_amount");

                String donorRejectedFood = intent.getStringExtra("food_rejected_name");
                String donorRejecteddonor = intent.getStringExtra("donor_rejected_name");
                String donorRejectedAmount = intent.getStringExtra("donor_rejected_amount");

                if(donorName != null){

                    sendNotification("Food Available :" + foodName + "\n" +
                            "Donor Name: " + donorName + "\n" +
                            "Amount Left: " + amountLeft);


                } else if(donorFoodName != null){

                    sendNotificationToDonor("Food Claimed :" + donorFoodName + "\n" +
                            "Receiver Name: " + receiverName + "\n" +
                            "Amount Claimed: " + amountClaimed);

                }else if(donorAcceptedFood != null){

                    sendNotificationAccepted("Food Name :" + donorAcceptedFood + "\n" +
                            "Donor Name: " + donorAccepteddonor + "\n" +
                            "Accepted Amount: " + donorAcceptedAmount );

                }else if(donorRejectedFood != null){

                    sendNotificationRejected("Food Name :" + donorRejectedFood + "\n" +
                            "Donor Name: " + donorRejecteddonor + "\n" +
                            "Accepted Amount: " + donorRejectedAmount );

                }else{

                    sendNotificationWaste("Food Name :" + fName + "\n" +
                            "Donor Name: " + dName );
                }







            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }


    private void sendNotification(String msg) {

        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("New Food Available")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        NOTIFICATION_ID += 1;

        Intent sendIntent =new Intent(this, LoginActivity.class);
        sendIntent.putExtra("type", "receiver");
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
        stopSelf();
    }

    private void sendNotificationWaste(String msg) {

        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Food Expired")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        NOTIFICATION_ID += 1;

        Intent sendIntent =new Intent(this, LoginActivity.class);
        sendIntent.putExtra("type", "receiver");
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
        stopSelf();
    }

    private void sendNotificationAccepted(String msg) {

        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Request Accepted")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        NOTIFICATION_ID += 1;

        Intent sendIntent =new Intent(this, LoginActivity.class);
        sendIntent.putExtra("type", "receiver");
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
        stopSelf();
    }

    private void sendNotificationRejected(String msg) {

        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Request Rejected")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        NOTIFICATION_ID += 1;

        Intent sendIntent =new Intent(this, LoginActivity.class);
        sendIntent.putExtra("type", "receiver");
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
        stopSelf();
    }


    private void sendNotificationToDonor(String msg) {

        mNotificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder =new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("New Food Request")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        NOTIFICATION_ID += 1;
        Intent sendIntent =new Intent(this, LoginActivity.class);
        sendIntent.putExtra("type", "donor");
        LocalBroadcastManager.getInstance(this).sendBroadcast(sendIntent);
        stopSelf();
    }
}
