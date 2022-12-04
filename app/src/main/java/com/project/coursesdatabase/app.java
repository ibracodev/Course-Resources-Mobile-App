package com.project.coursesdatabase;

import android.app.Application;
import android.content.Intent;

public class app extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /*Intent intent = new Intent(this, NotificationService.class);
        this.startForegroundService(intent);*/
    }
}
