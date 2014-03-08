package org.movieos.flame;

import android.util.Log;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

import java.util.ArrayList;

public class FlameService  {
    static String TAG = "Flame::FlameService";

    ServiceEvent service;
    ArrayList<String> hostIdentifiers;
    ServiceLookup serviceLookup;

    public FlameService(ServiceEvent s) {
        service = s;
        hostIdentifiers = new ArrayList<String>();
        if (service.getInfo() != null) {
            if (!service.getInfo().getServer().isEmpty()) {
                hostIdentifiers.add(service.getInfo().getServer());
            }
            for (String address : service.getInfo().getHostAddresses()) {
                if (!address.isEmpty()) {
                    hostIdentifiers.add(address);
                }
            }
            hostIdentifiers.add(toString());

            serviceLookup = ServiceLookup.get(service.getInfo().getApplication());
        }
    }

    public ArrayList<String> getHostIdentifiers() {
        return hostIdentifiers;
    }

    public String toString() {
        return "" + service.getName() + " " + service.getType();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FlameService)) {
            return false;
        }
        return ((FlameService) o).toString().equals(toString());
    }

    public ServiceInfo getInfo() {
        return service.getInfo();
    }

    public String getIPAddress() {
        for (String address : service.getInfo().getHostAddresses()) {
            if (!address.isEmpty()) {
                return address;
            }
        }
        return "No address";
    }

    public ServiceLookup getServiceLookup() {
        return serviceLookup;
    }

    public String getIdentifier() {
        return service.getName();
    }

    public String getTitle() {
        Log.v(TAG, "app is " + service.getInfo().getApplication());
        if (serviceLookup != null) {
            return serviceLookup.description;
        }
        return service.getType();
    }

    public String getSubTitle() {
        return service.getName();
    }

    public int getImageResource() {
        if (serviceLookup != null) {
            return serviceLookup.drawable;
        }
        return R.drawable.gear2;
    }

}
