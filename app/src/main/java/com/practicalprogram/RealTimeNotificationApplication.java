package com.practicalprogram;

import android.app.Application;

public class RealTimeNotificationApplication extends Application {

    private static RealTimeNotificationApplication appController;


    public static RealTimeNotificationApplication getInstance() {
        return appController;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        appController = this;

    }
}
