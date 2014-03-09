package org.movieos.flame.models;

import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.movieos.flame.R;
import org.movieos.flame.ServiceLookup;

import javax.jmdns.ServiceEvent;

public class FlameHost {
    static String TAG = "Flame::FlameHost";

    private List<ServiceEvent> mServices;
    private String mIdentifer;
    private ServiceLookup mLookup;

    public static String hostIdentifierForService(ServiceEvent service) {
        return service.getInfo().getHostAddress();
    }

    public static ServiceLookup getServiceLookup(List<ServiceEvent> services) {
        ArrayList<ServiceLookup> lookups = new ArrayList<>();
        for (ServiceEvent service : services) {
            ServiceLookup lookup = ServiceLookup.get(service.getType());
            if (lookup != null && lookup.getPriority() > 0) {
                lookups.add(lookup);
            }
        }
        Log.v(TAG, "lookups for " + services + " are " + lookups);
        if (lookups.isEmpty()) {
            return null;
        }
        Collections.sort(lookups);
        return lookups.get(lookups.size()-1);
    }


    public FlameHost(List<ServiceEvent> services) {
        mServices = services;
        if (services.size() == 0) {
            return;
        }

        Collections.sort(services, new Comparator<ServiceEvent>() {
            @Override
            public int compare(ServiceEvent lhs, ServiceEvent rhs) {
                ServiceLookup left = ServiceLookup.get(lhs.getType());
                ServiceLookup right = ServiceLookup.get(rhs.getType());
                return Integer.valueOf(left.getPriority()).compareTo(right.getPriority());
            }
        });

        mIdentifer = hostIdentifierForService(services.get(0));
        mLookup = ServiceLookup.get(services.get(0).getType());
    }

    @Override
    public String toString() {
        return mIdentifer;
    }

    public List<ServiceEvent> getServices() {
        return mServices;
    }

    public String getTitle() {
        return mServices.get(0).getName();
    }

    public String getSubTitle() {
        return String.format("%s - %d services", mIdentifer, mServices.size());
    }

    public int getImageResource() {
        if (mLookup != null) {
            return mLookup.getDrawable();
        }
        return R.drawable.macbook;
    }

    public String getIdentifer() {
        return mIdentifer;
    }
}
