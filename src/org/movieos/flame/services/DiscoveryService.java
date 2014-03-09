package org.movieos.flame.services;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import org.movieos.flame.models.FlameHost;
import org.movieos.flame.models.FlameService;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

public class DiscoveryService extends Service implements ServiceTypeListener, ServiceListener {
    private static final String TAG = "DiscoveryService";

    public static final String BROADCAST_HOSTS_CHANGED = "broadcast_hosts_changed";

    private JmDNS mJmDNSService;
    private static HashMap<String, ServiceEvent> services;
    private WifiManager.MulticastLock mWifiLock;
    private Handler mHandler;

    public class MyBinder extends Binder {
        public DiscoveryService getService() {
            return DiscoveryService.this;
        }
    }

    /// lifecycle

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");

        services = new HashMap<>();

        final WifiManager wifi = (android.net.wifi.WifiManager)getSystemService(android.content.Context.WIFI_SERVICE);
        mWifiLock = wifi.createMulticastLock("Flame");
        mWifiLock.setReferenceCounted(true);
        mWifiLock.acquire();

        mHandler = new Handler();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String ip = Formatter.formatIpAddress(wifi.getConnectionInfo().getIpAddress());
                    InetAddress bindingAddress = InetAddress.getByName(ip);
                    mJmDNSService = JmDNS.create(bindingAddress, "wifi");
                    mJmDNSService.addServiceTypeListener(DiscoveryService.this);
                    Log.v(TAG, "Discovery started");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        if (mWifiLock != null) {
            mWifiLock.release();
        }
        if (mJmDNSService != null) {
            mJmDNSService.removeServiceTypeListener(this);
        }
    }

    private final IBinder mBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "onBind");
        return mBinder;
    }

    /// data accessors

    public List<FlameHost> getHosts() {
        return getHostsMatchingKey(null);
    }

    public List<FlameHost> getHostsMatchingKey(String filter) {
        Map<String, List<ServiceEvent>> grouped = new HashMap<>();
        for (ServiceEvent service : services.values()) {
            String key = FlameHost.hostIdentifierForService(service);
            if (filter != null && !TextUtils.equals(filter, key)) {
                continue;
            }
            if (grouped.get(key) == null) {
                grouped.put(key, new ArrayList<ServiceEvent>());
            }
            grouped.get(key).add(service);
        }

        List<FlameHost> hosts = new ArrayList<>();
        Iterator<Map.Entry<String, List<ServiceEvent>>> iterator = grouped.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<ServiceEvent>> entry = iterator.next();
            FlameHost host = new FlameHost(entry.getValue());
            hosts.add(host);
        }
        Log.i(TAG, "hosts is " + hosts);
        return hosts;
    }

//    public List<FlameService> getServices() {
//        List<FlameService> services = new ArrayList<>();
//
//        return services;
//    }


    /// actions

    private void broadcastHostChange() {
        Intent updated = new Intent(BROADCAST_HOSTS_CHANGED);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(updated);
    }

    private String uniqueIdentifierForService(ServiceEvent service) {
        return String.format("%s %s", service.getName(), service.getType());
    }

    /// jmdns callbacks

    @Override
    public void serviceTypeAdded(ServiceEvent event) {
        Log.v(TAG, "new type: " + event.getType());
        event.getDNS().addServiceListener(event.getType(), this);
    }

    @Override
    public void subTypeForServiceTypeAdded(ServiceEvent event) {
        Log.v(TAG, "subTypeForServiceTypeAdded " + event);
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        Log.v(TAG, "serviceAdded: " + event.getName() + " / " + event.getType());
        event.getDNS().requestServiceInfo(event.getType(), event.getName(), true);
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        Log.v(TAG, "serviceRemoved: " + event.getName() + " / " + event.getType());
        final String key = uniqueIdentifierForService(event);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (services.containsKey(key)) {
                    services.remove(key);
                    broadcastHostChange();
                }
            }
        });
    }

    @Override
    public void serviceResolved(final ServiceEvent event) {
        Log.v(TAG, "serviceRemoved: "+event.getName() + " on " + event.getType());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                services.put(uniqueIdentifierForService(event), event);
                broadcastHostChange();
            }
        });
    }


}
