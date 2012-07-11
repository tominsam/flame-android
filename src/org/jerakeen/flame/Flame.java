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
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;


public class Flame extends ListActivity implements ServiceTypeListener, ServiceListener {
    
    ArrayAdapter<String> arrayAdapter;
    Vector<JmDNS> resolvers;
    ArrayList<String> names;

    android.net.wifi.WifiManager.MulticastLock lock;

    static String TAG = "flame";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "started!");
        
        names = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
        setListAdapter(arrayAdapter);
//        getListView().setTextFilterEnabled(true);

        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager)getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("FlameWifiLock");
        lock.setReferenceCounted(true);
        lock.acquire();

        resolvers = new Vector<JmDNS>();
        
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            Log.v(TAG, "Can't enumerate interfaces: " + e);
        }
        while (interfaces != null && interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (!networkInterface.getName().equals("lo") && addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                Log.v(TAG, "Listening on " + networkInterface.getDisplayName() + " / " + address.toString());
                try {
                    JmDNS jmdns = JmDNS.create(address);
                    jmdns.addServiceTypeListener(this);

//                    ServiceInfo flameService = new ServiceInfo("_flame._tcp.", "Flame", 0, "flame-android");
//                    jmdns.registerService( flameService );

                    resolvers.add(jmdns);
                    Log.v(TAG, "added resolver " + jmdns);

                } catch (IOException e) {
                    Log.v(TAG, "Can't listen on interface " + address + ": " + e);
                }
            }
        }

    }

    public void serviceTypeAdded(ServiceEvent event) {
        Log.v(TAG, "new type: " +event.getType());
        final String type = event.getType();
        event.getDNS().addServiceListener( type, this );
    }

    @Override
    public void subTypeForServiceTypeAdded(ServiceEvent serviceEvent) {
        Log.v(TAG, "subTypeFroservicetypeadded " + serviceEvent);
    }

    public void serviceAdded(ServiceEvent event) {
        Log.v(TAG, "serviceAdded: "+event.getName());
        event.getDNS().requestServiceInfo(event.getType(), event.getName(), 0);
        String string = event.getType() + " - " + event.getName();
        Log.v(TAG, "stringified to " + string);
        arrayAdapter.add( string );
        arrayAdapter.notifyDataSetChanged();
    }

    public void serviceRemoved(ServiceEvent event) {
        Log.v(TAG, "serviceRemoved: "+event.getName());
    }

    public void serviceResolved(ServiceEvent event) {
        Log.v(TAG, "serviceResolved: "+event.getName());
    }

    @Override
    protected void onDestroy() {
        if (lock != null) lock.release();
        super.onDestroy();
    }
}

