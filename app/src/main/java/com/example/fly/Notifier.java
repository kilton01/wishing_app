package com.example.fly;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Random;

public class Notifier extends BroadcastReceiver {

    private String CHANNEL_ID = "0";

    @Override
    public void onReceive(Context context, Intent intent) {
        Random random = new Random();
        int notificationId = random.nextInt(1000);

        String message = intent.getStringExtra("message");
        String phone = intent.getStringExtra("Phone Number");
        phone.replace('+',' ').trim();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm Notification";
            String description = "Notifies user when alarm time is up";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        // Create an explicit intent for an Activity in your app
        Intent mainIntent = new Intent(context, Main2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);

        Uri uri =  Uri.parse("https://api.whatsapp.com/send?phone="+phone+"&text="+message);
        Intent sendMessage = new Intent(Intent.ACTION_VIEW, uri);
        PendingIntent pending_Message_Intent = PendingIntent.getActivity(context,0,sendMessage,0);

        Uri uri1 = Uri.parse("tel:"+phone);
        Intent callPerson = new Intent(Intent.ACTION_DIAL,uri1);
        PendingIntent pending_Call_Intent = PendingIntent.getActivity(context,0,callPerson,0);

        Uri uri2 = Uri.parse("smsto:" + phone);
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri2);
        smsIntent.putExtra("sms_body", message);
        PendingIntent pending_Send_SMS = PendingIntent.getActivity(context,0,smsIntent,0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_cake_black_24dp)
                .setContentTitle(" Happy Birthday!!! ")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_phone_black_24dp,"Call",pending_Call_Intent)
                .addAction(R.drawable.ic_send_black_24dp,"Send Wish",pending_Message_Intent)
                .addAction(R.drawable.ic_send_black_24dp,"Send SMS", pending_Send_SMS);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());

    }

//    Can write the code to check the date before it creates the pendingIntent else alarmManager.cancel(pendingIntent);


}
