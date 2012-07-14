package org.jerakeen.flame;

import android.nfc.Tag;
import android.util.Log;

import java.util.ArrayList;

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

}
