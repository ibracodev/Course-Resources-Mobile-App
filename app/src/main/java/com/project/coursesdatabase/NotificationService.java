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
//code apapted from https://www.youtube.com/watch?v=m8vUFO5mFIM
public class NotificationService extends FirebaseMessagingService {

    String TAG = "NotificationService";
    String courseName = "";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        String title=remoteMessage.getNotification().getTitle();
        String text=remoteMessage.getNotification().getBody();

        final String CHANNEL_ID="NOTIF";
        NotificationChannel channel=new NotificationChannel(
                CHANNEL_ID,"Notification",NotificationManager.IMPORTANCE_DEFAULT
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification=new Notification.Builder(this,CHANNEL_ID).setContentTitle(title)
                .setContentText(text).setSmallIcon(R.drawable.logoapp).setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1,notification.build());
        super.onMessageReceived(remoteMessage);
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

