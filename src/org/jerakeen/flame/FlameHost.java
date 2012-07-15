package org.jerakeen.flame;

import android.nfc.Tag;
import android.util.Log;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

public class FlameHost {
    static String TAG = "Flame::FlameHost";

    ArrayList<FlameService> services;
    ArrayList<String> identifiers;

    public FlameHost(FlameService service) {
        identifiers = new ArrayList<String>();
        services = new ArrayList<FlameService>();
        addService(service);
    }

    public boolean matchesService(FlameService service) {
        ArrayList<String> serviceIdentifiers = service.getHostIdentifiers();
        for (String identifier : identifiers) {
            if (serviceIdentifiers.contains(identifier)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getTitle() + " / " + getSubTitle();
    }

    public String getIdentifier() {
        if (identifiers.isEmpty()) {
            return "<unknown>";
        }
        return identifiers.get(0);
    }

    public void addService(FlameService s) {
        identifiers.addAll(s.getHostIdentifiers());
        services.add(s);
    }

    public ArrayList<FlameService> getServices() {
        return services;
    }

    public String getTitle() {
        return getIdentifier();
    }

    public String getSubTitle() {
        if (services.isEmpty()) {
            return "";
        }
        return services.get(0).getIPAddress();
    }

    public ServiceLookup getServiceLookup() {
        ArrayList<ServiceLookup> lookups = new ArrayList<ServiceLookup>();
        for (FlameService service : getServices()) {
            ServiceLookup lookup = service.getServiceLookup();
            if (lookup != null && lookup.priority > 0) {
                lookups.add(lookup);
            }
        }
        Log.v(TAG, "lookups for " + this + " are " + lookups);
        if (lookups.isEmpty()) {
            return null;
        }
        Collections.sort(lookups);
        return lookups.get(lookups.size()-1);
    }

    public int getImageResource() {
        ServiceLookup lookup = getServiceLookup();
        if (lookup != null) {
            return lookup.drawable;
        }
        return R.drawable.macbook;
    }

}
