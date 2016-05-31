package org.movieos.flame.services;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.text.format.Formatter;

import org.movieos.flame.models.FlameHost;
import timber.log.Timber;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

public class DiscoveryService extends Service implements ServiceTypeListener, ServiceListener {

    private static HashMap<String, ServiceEvent> sServices;
    private static List<DiscoveryServiceListener> sListeners = new ArrayList<>();

    private JmDNS mJmDNSService;
    private WifiManager.MulticastLock mWifiLock;
    private Handler mHandler;

    public interface DiscoveryServiceListener {
        void onDiscoveredServicesChanged();
    }

    public class MyBinder extends Binder {
        public DiscoveryService getService() {
            return DiscoveryService.this;
        }
    }

    /// lifecycle

    @Override
    public void onCreate() {
        Timber.v("onCreate");

        sServices = new HashMap<>();

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
                    mJmDNSService = JmDNS.create(bindingAddress, "Android");
                    mJmDNSService.addServiceTypeListener(DiscoveryService.this);

                    // broadcast that we are running flame
                    ServiceInfo serviceInfo = ServiceInfo.create("_flame._tcp.local.", "Flame", 11812, mJmDNSService.getName());
                    mJmDNSService.registerService(serviceInfo);

                    Timber.v("Discovery started");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

        broadcastHostChange();
    }

    @Override
    public void onDestroy() {
        Timber.v("onDestroy");
        if (mWifiLock != null) {
            mWifiLock.release();
        }
        if (mJmDNSService != null) {
            mJmDNSService.unregisterAllServices();
            mJmDNSService.removeServiceTypeListener(this);
        }
    }

    private final IBinder mBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /// data accessors

    public List<FlameHost> getHosts() {
        return getHostsMatchingKey(null);
    }

    public List<FlameHost> getHostsMatchingKey(String filter) {
        Map<String, List<ServiceEvent>> grouped = new HashMap<>();
        for (ServiceEvent service : sServices.values()) {
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
        for (final Map.Entry<String, List<ServiceEvent>> entry : grouped.entrySet()) {
            FlameHost host = new FlameHost(entry.getValue());
            hosts.add(host);
        }

        Collections.sort(hosts, new Comparator<FlameHost>() {
            @Override
            public int compare(FlameHost lhs, FlameHost rhs) {
                return lhs.getTitle().compareToIgnoreCase(rhs.getTitle());
            }
        });

        return hosts;
    }

    /// actions

    private void broadcastHostChange() {
        for (DiscoveryServiceListener listener : sListeners) {
            listener.onDiscoveredServicesChanged();
        }
    }

    private String uniqueIdentifierForService(ServiceEvent service) {
        return String.format("%s %s", service.getName(), service.getType());
    }

    /// jmdns callbacks

    @Override
    public void serviceTypeAdded(ServiceEvent event) {
        //Timber.v("new type: " + event.getType());
        event.getDNS().addServiceListener(event.getType(), this);
    }

    @Override
    public void subTypeForServiceTypeAdded(ServiceEvent event) {
        //Timber.v("subTypeForServiceTypeAdded " + event);
    }

    @Override
    public void serviceAdded(ServiceEvent event) {
        //Timber.v("serviceAdded: " + event.getName() + " / " + event.getType());
        event.getDNS().requestServiceInfo(event.getType(), event.getName(), true);
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        Timber.v("serviceRemoved: %s / %s", event.getName(), event.getType());
        final String key = uniqueIdentifierForService(event);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (sServices.containsKey(key)) {
                    sServices.remove(key);
                    broadcastHostChange();
                }
            }
        });
    }

    @Override
    public void serviceResolved(final ServiceEvent event) {
        Timber.v("serviceResolved: %s / %s", event.getName(), event.getType());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                sServices.put(uniqueIdentifierForService(event), event);
                broadcastHostChange();
            }
        });
    }

    public static void registerListener(DiscoveryServiceListener listener) {
        sListeners.add(listener);
    }

    public static void unregisterListener(DiscoveryServiceListener listener) {
        sListeners.remove(listener);
    }

}
