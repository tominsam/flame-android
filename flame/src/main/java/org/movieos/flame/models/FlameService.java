package org.movieos.flame.models;

import android.net.nsd.NsdServiceInfo;

import org.movieos.flame.utilities.ServiceLookup;

import java.util.ArrayList;

public class FlameService {

    NsdServiceInfo mService;
    ArrayList<String> mHostIdentifiers;
    ServiceLookup mServiceLookup;

    public FlameService(NsdServiceInfo s) {
        if (s == null) {
            throw new RuntimeException("service must be set");
        }
        mService = s;
        mHostIdentifiers = new ArrayList<>();
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
        return mHostIdentifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FlameService)) {
            return false;
        }
        return ((FlameService) o).toString().equals(toString());
    }

    public String toString() {
        return String.format("%s %s", mService.getServiceName(), mService.getServiceType());
    }

    public NsdServiceInfo getInfo() {
        return mService;
    }

    public String getIPAddress() {
        return mService.getHost().getHostAddress();
    }

    public ServiceLookup getServiceLookup() {
        return mServiceLookup;
    }

    public String getIdentifier() {
        // TODO probably not good enough
        return String.format("%s %s", mService.getServiceName(), mService.getServiceType());
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
        if (mServiceLookup != null) {
            return mServiceLookup.getDrawable();
        }
        return 0;
    }

}
