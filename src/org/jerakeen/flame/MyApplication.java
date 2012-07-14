package org.jerakeen.flame;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

interface FlameListener {
    public void updatedHosts();
}

public class MyApplication extends Application {
    static String TAG = "HostList::MyApplication";

    android.net.wifi.WifiManager.MulticastLock lock;
    FlameBackgroundThread task;
    ArrayList<FlameHost> hosts;
    ArrayList<FlameListener> listeners;

    // Need handler for callbacks to the UI thread
    final Handler handler = new Handler();
    final Runnable updateRunnable = new Runnable() {
        public void run() {
            updateFromBackground();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");

        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager)getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("FlameWifiLock");
        lock.setReferenceCounted(true);
        lock.acquire();

        hosts = new ArrayList<FlameHost>();
        listeners = new ArrayList<FlameListener>();

        task = new FlameBackgroundThread(handler, updateRunnable);
        Thread t = new Thread(task);
        t.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.v(TAG, "onConfigurationChanged");
    }

    private void updateFromBackground() {
        Log.v(TAG, "hosts updated");
        hosts = task.getHosts();
        for (FlameListener a : listeners) {
            a.updatedHosts();
        }
    }

    public void addListener(FlameListener l) {
        listeners.add(l);
    }

    public void removeListener(FlameListener l) {
        listeners.remove(l);
    }

    public ArrayList<FlameHost> getHosts() {
        return hosts;
    }

    public FlameHost getHost(String name) {
        for (FlameHost host : hosts) {
            if (host.getName().equals(name)) {
                return host;
            }
        }
        return new FlameHost(name);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.v(TAG, "onTerminate");
        task.stop();
        task = null;
        if (lock != null) {
            lock.release();
        }
    }
}
