package org.movieos.flame;

import android.app.Application;
import android.support.annotation.NonNull;

import org.movieos.flame.utilities.DiscoveryService;
import timber.log.Timber;

public class MyApplication extends Application {

    private static DiscoveryService sDiscoveryService;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        //noinspection AssignmentToStaticFieldFromInstanceMethod
        sDiscoveryService = new DiscoveryService(this);
    }

    @NonNull
    public static DiscoveryService discoveryService() {
        return sDiscoveryService;
    }
}
