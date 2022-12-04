package com.project.coursesdatabase;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;


public class app extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test","Application Started");
        Intent service = new Intent(this, BackgroundService.class);
        startService(service);



    }
}
