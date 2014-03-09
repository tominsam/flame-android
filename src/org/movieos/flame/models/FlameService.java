package org.movieos.flame.models;

import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.ArrayList;

import org.movieos.flame.R;
import org.movieos.flame.ServiceLookup;

public class FlameService  {
    static String TAG = "Flame::FlameService";

    NsdServiceInfo service;
    ArrayList<String> hostIdentifiers;
    ServiceLookup serviceLookup;

    public FlameService(NsdServiceInfo s) {
        if (s == null) {
            throw new RuntimeException("service must be set");
        }
        service = s;
        hostIdentifiers = new ArrayList<>();
//        if (service.getInfo() != null) {
//            if (!service.getInfo().getServer().isEmpty()) {
//                hostIdentifiers.add(service.getInfo().getServer());
//            }
//            for (String address : service.getInfo().getHostAddresses()) {
//                if (!address.isEmpty()) {
//                    hostIdentifiers.add(address);
//                }
//            }
//            hostIdentifiers.add(toString());
//
//            serviceLookup = ServiceLookup.get(service.getInfo().getApplication());
//        }
    }

    public ArrayList<String> getHostIdentifiers() {
        return hostIdentifiers;
    }

    public String toString() {
        return String.format("%s %s", service.getServiceName(), service.getServiceType());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FlameService)) {
            return false;
        }
        return ((FlameService) o).toString().equals(toString());
    }

    public NsdServiceInfo getInfo() {
        return service;
    }

    public String getIPAddress() {
        return service.getHost().getHostAddress();
    }

    public ServiceLookup getServiceLookup() {
        return serviceLookup;
    }

    public String getIdentifier() {
        // TODO probably not good enough
        return String.format("%s %s", service.getServiceName(), service.getServiceType());
    }

    public String getTitle() {
        return null;
//        Log.v(TAG, "app is " + service.getInfo().getApplication());
//        if (serviceLookup != null) {
//            return serviceLookup.getDescription();
//        }
//        return service.getType();
    }

    public String getSubTitle() {
        return null;
//        return service.getName();
    }

    public int getImageResource() {
        if (serviceLookup != null) {
            return serviceLookup.getDrawable();
        }
        return R.drawable.gear2;
    }

}
