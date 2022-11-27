package com.project.coursesdatabase;

import android.app.NotificationChannel;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.messaging.FirebaseMessaging;

public class NotificationService extends Service {

    private FirebaseMessaging s;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void sendNotification(){
   
    }

}