package org.jerakeen.flame;

import java.util.ArrayList;

public class FlameHost {
    private String name;
    ArrayList<FlameService> services;

    public FlameHost(String n) {
        name = n;
        services = new ArrayList<FlameService>();
    }

    public void addService(FlameService s) {
        services.add(s);
    }

    public void removeService(FlameService s) {
        services.remove(s);
    }

    public ArrayList<FlameService> getServices() {
        return services;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return name;
    }

}
