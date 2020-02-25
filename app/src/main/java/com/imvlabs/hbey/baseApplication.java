package com.imvlabs.hbey;

import android.app.Application;
import android.support.multidex.MultiDex;

/**
 * Created by Kautsar Fadly F on 19/03/2018.
 */

public class baseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
