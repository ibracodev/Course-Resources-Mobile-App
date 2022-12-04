package com.project.coursesdatabase;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;


public class BackgroundService extends Service {
    private Timer timer;
    private static final String CHANNEL_ID = "1";
    @Override
    public void onCreate() {
        startTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags,
                              int startId) {



        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
         stopTimer();
    }
    private void startTimer() {
        // create task
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sendNotification("Select to view resources for your favourite courses");
            }
        };

        // create and start timer
        timer = new Timer(true);
        int delay = 1000 ;
        int interval = 1000 * 28800; // Every 8 hours
        timer.schedule(task, delay, interval);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void sendNotification(String text)

    {
        Intent notificationIntent = new Intent (this , MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT ;

        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0 ,
                notificationIntent , PendingIntent.FLAG_IMMUTABLE) ;

        int icon = R.drawable.logoapp ;
        CharSequence tickerText = "Course Resources Available" ;
        CharSequence contentTitle = "View Course Resources" ;

        CharSequence contentText = text;
        NotificationChannel notificationChannel = new
                NotificationChannel("CHANNEL_ID" , "My notification " ,
                NotificationManager.IMPORTANCE_DEFAULT );
        NotificationManager manager = (NotificationManager)
                getSystemService(this.NOTIFICATION_SERVICE) ;
        manager.createNotificationChannel(notificationChannel);
        Notification notifacton = new NotificationCompat
                .Builder(this , "CHANNEL_ID")
                .setSmallIcon(R.drawable.uploadicon)
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId("CHANNEL_ID")
                .build();

        Notification notifacton2 = new NotificationCompat
                .Builder(this , "CHANNEL_ID")
                .setSmallIcon(R.drawable.course_icon)
                .setTicker("Please Do Not Use Previouses During Your Exams :)")
                .setContentTitle("PSA")
                .setContentText("Please Do Not Use Previouses During Your Exams :)")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setChannelId("CHANNEL_ID")
                .build();

        final int NOTIFICATION_ID =1;
        final int NOTIFICATION_ID2 =2;
        manager.notify(NOTIFICATION_ID , notifacton);
        manager.notify(NOTIFICATION_ID2 , notifacton2);

    }


}
