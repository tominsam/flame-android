package org.movieos.flame.models;

import org.movieos.flame.utilities.ServiceLookup;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.jmdns.ServiceEvent;

public class FlameHost {
    private final List<ServiceEvent> mServices = new ArrayList<>();
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
        Timber.v("lookups for %s are %s",services, lookups);
        if (lookups.isEmpty()) {
            return null;
        }
        Collections.sort(lookups);
        return lookups.get(lookups.size()-1);
    }


    public FlameHost(List<ServiceEvent> services) {
        mServices.clear();
        mServices.addAll(services);
        if (services.size() == 0) {
            return;
        }

        Collections.sort(services, (lhs, rhs) -> {
            ServiceLookup left = ServiceLookup.get(lhs.getType());
            ServiceLookup right = ServiceLookup.get(rhs.getType());
            if (left != null && right != null) {
                return Integer.valueOf(left.getPriority()).compareTo(right.getPriority());
            } else if (left != null) {
                // left wins
                return -1;
            } else {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
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
        return String.format(Locale.getDefault(), "%s - %d services", mIdentifer, mServices.size());
    }

    public int getImageResource() {
        if (mLookup != null) {
            return mLookup.getDrawable();
        }
        return 0;
    }

    public String getIdentifer() {
        return mIdentifer;
    }
}
