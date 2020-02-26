package com.imvlabs.hbey;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.google.firebase.FirebaseApp;

/**
 * Created by Kautsar Fadly F on 19/03/2018.
 */

public class baseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(getApplicationContext());
        MultiDex.install(this);
    }
}
