package org.movieos.flame.utilities;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.Formatter;

import org.greenrobot.eventbus.EventBus;
import org.movieos.flame.models.FlameHost;
import timber.log.Timber;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import javax.jmdns.ServiceTypeListener;

public class DiscoveryService implements ServiceTypeListener, ServiceListener {

    private static final String FLAME_SERVICE_NAME = "_flame._tcp.local.";
    private static final int FLAME_SERVICE_PORT = 11812;

    private HashMap<String, ServiceEvent> mServices;

    private Context mContext;
    private JmDNS mJmDNSService;
    private WifiManager.MulticastLock mWifiLock;

    public DiscoveryService(Context context) {
        super();
        mContext = context.getApplicationContext();

        mServices = new HashMap<>();

        final WifiManager wifi = (android.net.wifi.WifiManager)mContext.getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
        mWifiLock = wifi.createMulticastLock("Flame");
        mWifiLock.setReferenceCounted(true);
        mWifiLock.acquire();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String ip = Formatter.formatIpAddress(wifi.getConnectionInfo().getIpAddress());
                    InetAddress bindingAddress = InetAddress.getByName(ip);
                    mJmDNSService = JmDNS.create(bindingAddress, "Android");
                    mJmDNSService.addServiceTypeListener(DiscoveryService.this);

                    // broadcast that we are running flame
                    ServiceInfo serviceInfo = ServiceInfo.create(FLAME_SERVICE_NAME, "Flame", FLAME_SERVICE_PORT, mJmDNSService.getName());
                    mJmDNSService.registerService(serviceInfo);

                    Timber.v("Discovery started");
                } catch (IOException e) {
                    Timber.e(e, "Failed discovery");
                }
                return null;
            }
        }.execute();

        EventBus.getDefault().post(new HostsChangedNotification());
    }

    public void stop() {
        Timber.v("onDestroy");
        if (mWifiLock != null) {
            mWifiLock.release();
        }
        if (mJmDNSService != null) {
            mJmDNSService.unregisterAllServices();
            mJmDNSService.removeServiceTypeListener(this);
        }
    }

    /// data accessors

    @NonNull
    public List<FlameHost> getHosts() {
        return getHostsMatchingKey(null);
    }

    @NonNull
    public List<FlameHost> getHostsMatchingKey(String filter) {
        Map<String, List<ServiceEvent>> grouped = new HashMap<>();
        for (ServiceEvent service : mServices.values()) {
            String key = FlameHost.hostIdentifierForService(service);
            if (filter != null && !TextUtils.equals(filter, key)) {
                continue;
            }
            grouped.putIfAbsent(key, new ArrayList<>());
            grouped.get(key).add(service);
        }

        List<FlameHost> hosts = new ArrayList<>();
        for (final Map.Entry<String, List<ServiceEvent>> entry : grouped.entrySet()) {
            FlameHost host = new FlameHost(entry.getValue());
            hosts.add(host);
        }

        Collections.sort(hosts, (lhs, rhs) -> lhs.getTitle().compareToIgnoreCase(rhs.getTitle()));

        return hosts;
    }

    /// actions

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
        if (mServices.containsKey(key)) {
            mServices.remove(key);
            EventBus.getDefault().post(new HostsChangedNotification());
        }
    }

    @Override
    public void serviceResolved(final ServiceEvent event) {
        Timber.v("serviceResolved: %s / %s", event.getName(), event.getType());
        mServices.put(uniqueIdentifierForService(event), event);
        EventBus.getDefault().post(new HostsChangedNotification());
    }

    public static class HostsChangedNotification {

    }

}
