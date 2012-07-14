package org.jerakeen.flame;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;


public class Flame extends ListActivity {
    static String TAG = "Flame::Flame";

    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> names;
    android.net.wifi.WifiManager.MulticastLock lock;

    // Need handler for callbacks to the UI thread
    final Handler handler = new Handler();
    final Runnable updateRunnable = new Runnable() {
        public void run() {
            updateFromBackground();
        }
    };
    FlameBackgroundThread task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "started!");

        names = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        setListAdapter(arrayAdapter);

        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager)getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("FlameWifiLock");
        lock.setReferenceCounted(true);
        lock.acquire();

        task = new FlameBackgroundThread(handler, updateRunnable);
        Thread t = new Thread(task);
        t.start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
    }

    private void updateFromBackground() {
        arrayAdapter.clear();
        for (FlameHost host : task.getHosts()) {
            arrayAdapter.add( host.getTitle() );
        }
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        task.stop();
        task = null;
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
        if (lock != null) {
            lock.release();
        }
    }
}

