package org.movieos.flame.fragments;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.Formatter;

import org.movieos.flame.activities.FlameActivity;
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

public class DiscoveryFragment extends Fragment implements ServiceTypeListener, ServiceListener {

    private static final String FLAME_SERVICE_NAME = "_flame._tcp.local.";
    private static final int FLAME_SERVICE_PORT = 11812;

    private HashMap<String, ServiceEvent> mServices;

    private JmDNS mJmDNSService;
    private WifiManager.MulticastLock mWifiLock;
    private Handler mHandler;

    public DiscoveryFragment() {
        super();
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        Timber.v("onCreate");
        super.onCreate(savedInstanceState);

        mServices = new HashMap<>();

        final WifiManager wifi = (android.net.wifi.WifiManager)getContext().getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
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
                    mJmDNSService.addServiceTypeListener(DiscoveryFragment.this);

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

        broadcastHostChange();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    private void broadcastHostChange() {
        if (getActivity() != null) {
            ((FlameActivity) getActivity()).onDiscoveredServicesChanged();
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
        mHandler.post(() -> {
            if (mServices.containsKey(key)) {
                mServices.remove(key);
                broadcastHostChange();
            }
        });
    }

    @Override
    public void serviceResolved(final ServiceEvent event) {
        Timber.v("serviceResolved: %s / %s", event.getName(), event.getType());
        mHandler.post(() -> {
            mServices.put(uniqueIdentifierForService(event), event);
            broadcastHostChange();
        });
    }

}
