package org.movieos.flame;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.movieos.flame.models.FlameHost;
import org.movieos.flame.models.FlameService;
import org.movieos.flame.services.DiscoveryService;

public class MyApplication extends Application {
    static String TAG = "Flame::MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
//        Intent discoveryService = new Intent(this, DiscoveryService.class);
//        startService(discoveryService);
    }

}
