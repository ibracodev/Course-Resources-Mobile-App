package com.project.coursesdatabase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends FirebaseMessagingService {

    String TAG = "NotificationService";
    String courseName = "";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(@NonNull String msgId) {
        super.onMessageSent(msgId);


    }
}

//DOCUMENTATION FOR NOTIS
//https://firebase.google.com/docs/reference/android/com/google/firebase/messaging/RemoteMessage.Builder
//https://firebase.google.com/docs/cloud-messaging/android/send-multiple#java_3



    /*
    final String CHANNEL_ID = "Firebase Notification";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        String title = message.getNotification().getTitle();
        String text = message.getNotification().getBody();

        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,"Extra Test", NotificationManager.IMPORTANCE_HIGH);

        getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true);

        NotificationManagerCompat.from(this).notify(1, notification.build());

        super.onMessageReceived(message);
    }
}*/



//    private Timer timer;
//    private app app;
//    int noti_id =0;
//    String courseName = "none";
//    boolean course = false;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        app = (app) getApplication();
////        Intent intent = new Intent(this, UploadFiles.class);
////        courseName = intent.getStringExtra("CourseName");
////        Log.d("Service", courseName + "on create");
////        if (courseName.equals(intent.getStringExtra("CourseName"))) {
////            course = true;
////        }
//        startTimer();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        stopTimer();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    public void startTimer(){
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                Log.d("Service", "timer started");
//                if (true) {
//                    sendNotification(courseName);
//                }
//            }
//        };
//        timer = new Timer(true);
//        int delay = 1000;
//        int interval = 1000;
//        timer.schedule(task,delay,interval);
//    }
//
//    public void stopTimer(){
//        if (timer != null ){
//            timer.cancel();
//        }
//    }
//
//    private void sendNotification(String coursename){
//
//        NotificationChannel serviceChannel = new NotificationChannel(
//                "Service Noti","Update Channel",
//                NotificationManager.IMPORTANCE_DEFAULT
//        );
//
//        Log.d("Service", coursename);
//
//        NotificationManager manager = getSystemService(NotificationManager.class);
//        manager.createNotificationChannel(serviceChannel);
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, PendingIntent.FLAG_MUTABLE);
//
//        Notification notification = new NotificationCompat.Builder(this, "Service Noti")
//                .setContentTitle("test title")
//                .setContentText("test text")
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentIntent(pendingIntent)
//                .build();
//
//        manager.notify(noti_id, notification);
//        noti_id++;
//    }