package org.jerakeen.flame;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

public class FlameService  {
    static String TAG = "HostList::FlameService";

    ServiceEvent service;
    ServiceInfo info;

    public FlameService(ServiceEvent s) {
        service = s;
    }

    public FlameService(ServiceEvent s, ServiceInfo i) {
        service = s;
        info = i;
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
