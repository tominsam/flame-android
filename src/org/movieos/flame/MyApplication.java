package org.movieos.flame;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.movieos.flame.models.FlameHost;
import org.movieos.flame.models.FlameService;

public class MyApplication extends Application {
    static String TAG = "Flame::MyApplication";

    public interface FlameListener {
        public void updatedHosts();
    }

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
        lock.setReferenceCounted(false);
        hosts = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.v(TAG, "onConfigurationChanged");
    }

    private void updateFromBackground() {
        Log.v(TAG, "services updated");
        hosts = task.getHosts();

        // alpha-sort by title
        Collections.sort(hosts, new Comparator<FlameHost>() {
            @Override
            public int compare(FlameHost flameHost, FlameHost flameHost1) {
                return flameHost.getTitle().toLowerCase().compareTo(flameHost1.getTitle().toLowerCase());
            }
        });

        for (FlameListener a : listeners) {
            a.updatedHosts();
        }
    }

    public void addListener(FlameListener l) {
        Log.v(TAG, "addListener " + l);
        if (listeners.isEmpty()) {
            listeners.add(l);
            Log.v(TAG, "acquiring wifi lock and starting search");
            lock.acquire();
            task = new FlameBackgroundThread(handler, updateRunnable);
            Thread t = new Thread(task);
            t.start();

        } else {
            listeners.add(l);
        }
    }

    public void removeListener(FlameListener l) {
        Log.v(TAG, "removeListener " + l);
        listeners.remove(l);
        if (listeners.isEmpty()) {
            Log.v(TAG, "dropping wifi lock and stopping task");
            lock.release();
            task.stop();
            task = null;
        }
    }

    public ArrayList<FlameHost> getHosts() {
        return hosts;
    }

    public FlameHost getHost(String identifier) {
        for (FlameHost host : hosts) {
            if (host.getIdentifier().equals(identifier)) {
                return host;
            }
        }
        return null;
    }

    public FlameService getService(String identifier) {
        for (FlameHost host : hosts) {
            for (FlameService service : host.getServices()) {
                if (service.toString().equals(identifier)) {
                    return service;
                }
            }
        }
        return null;
    }

    public void reload() {
        task.stop();

        task = new FlameBackgroundThread(handler, updateRunnable);

        // will send a blank list out
        updateFromBackground();

        Thread t = new Thread(task);
        t.start();
    }
}
