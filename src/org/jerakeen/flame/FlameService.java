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
            hostIdentifiers.add(toString());
            hostIdentifiers.add(service.getInfo().getServer());
            for (String address : service.getInfo().getHostAddresses()) {
                hostIdentifiers.add(address);
            }
            for (InetAddress address : service.getInfo().getInetAddresses()) {
                hostIdentifiers.add(address.toString());
            }
            for (String url : service.getInfo().getURLs()) {
                hostIdentifiers.add(url);
            }
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

    public String getName() {
        return service.getName();
    }
}
