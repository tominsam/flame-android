package org.jerakeen.flame;

import android.util.Log;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import java.net.InetAddress;
import java.util.ArrayList;

public class FlameService  {
    static String TAG = "Flame::FlameService";

    ServiceEvent service;
    ArrayList<String> hostIdentifiers;

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
            Log.v(TAG, "hostIdentifiers for " + this + " are " + hostIdentifiers);
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

    public String getIdentifier() {
        return service.getName();
    }

    public String getTitle() {
        return service.getName();
    }

    public String getSubTitle() {
        return service.getType();
    }
}
