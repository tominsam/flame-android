package org.jerakeen.flame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
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
        for (FlameService service : task.getServices()) {
            arrayAdapter.add( service.toString() );
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

