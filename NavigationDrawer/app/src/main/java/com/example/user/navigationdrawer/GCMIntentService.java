package com.example.user.navigationdrawer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import  com.google.android.gcm.GCMBaseIntentService;

/**
 * Created by user on 2015/10/10.
 */


public class GCMIntentService extends GCMBaseIntentService {

    // GCM google register account (GCM  sender ID)
    static final String SENDER_ID = "756678104108";
    private static final String TAG = "GCMIntentService";
    @SuppressWarnings("hiding")


    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {

    }
    /*
    private AlertDialog getAlertDialog(String title,String message){
        //產生一個Builder物件
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        //設定Dialog的標題
        builder.setTitle(title);
        //設定Dialog的內容
        builder.setMessage(message);
        //設定Positive按鈕資料
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
                Toast.makeText(getApplicationContext(), "您按下OK按鈕", Toast.LENGTH_SHORT).show();
            }
        });
        //設定Negative按鈕資料
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //按下按鈕時顯示快顯
                Toast.makeText(getApplicationContext(), "您按下Cancel按鈕", Toast.LENGTH_SHORT).show();
            }
        });
        //利用Builder物件建立AlertDialog
        return builder.create();
    }
    */
    @Override
    protected  void onMessage(Context context, Intent intent) {
                Log.i(TAG, "Received message");
        // 接收 GCM server 傳來的訊息
       Bundle bData = intent.getExtras();
        // 處理 bData 內含的訊息
        //  server node js will send those ( message, campaigndate, title, description ) types string data
        String distance = bData.getString("distance");
        String CurrentNumber = bData.getString("CurrentNumber");
        String title = bData.getString("title");
        String description = bData.getString("description");
        // notifies user

        if(distance !=   "'null") {
            generateNotification(context, bData);
            UserData userData = new UserData(context, "myFile");
            userData.setData("MyGCM", "true");
            System.out.println("2234");
        }

            System.out.println("distance=" + distance);
            System.out.println("CurrentNumber=" + CurrentNumber);

    }


    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */


    private static void generateNotification(Context context, Bundle data)
    {

        int icon = R.drawable.ic_clients;
    //    long when = System.currentTimeMillis();
    //     NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent ni = new Intent(context, MainActivity.class);
        ni.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ni.putExtra("Refuse", "No");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ni, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra ("Refuse", "Yes");
        PendingIntent refuseIntent = PendingIntent.getActivity(context, 40, intent, PendingIntent.FLAG_UPDATE_CURRENT);
              //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, ni, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context)
                .setSmallIcon(icon)
                .setContentTitle("GCM!" + data.getString("title"))
                .setContentText(data.getString("description"))
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_launcher, "開啟App", pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "關閉通知", refuseIntent)
                //   .setContentInfo(info)
                .setDefaults(Notification.DEFAULT_ALL);

        mBuilder.setContentIntent(pendingIntent);
        //mBuilder.setContentIntent(refuseIntent);


        mNotificationManager.notify(0, mBuilder.build());

    }


}
