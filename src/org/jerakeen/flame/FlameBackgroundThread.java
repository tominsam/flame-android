package org.jerakeen.flame;

import android.os.Handler;
import android.util.Log;

import javax.jmdns.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FlameBackgroundThread implements Runnable, ServiceTypeListener, ServiceListener {
    static String TAG = "Flame::FlameBackgroundTask";

    // my services resolver
    JmDNS jmdns;

    // talk to the UI thread
    Handler handler;
    Runnable update;

    // services list and a thread-safety lock for accesses
    ArrayList<FlameService> services;
    Lock serviceListLock;

    // I don't really know what I'm doing in terms of java concurrency.
    boolean running;
    CountDownLatch latch;


    public FlameBackgroundThread(Handler h, Runnable u) {
        super();
        handler = h;
        update = u;
        services = new ArrayList<FlameService>();
        serviceListLock = new ReentrantLock();
        latch = new CountDownLatch(1);
        running = true;
    }

    public void run() {
        try {
            jmdns = JmDNS.create();
            jmdns.addServiceTypeListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.post(update);

        // have to keep the thread alive now.

        Log.v(TAG, "holding thread on latch");
        while (running) {
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.v(TAG, "latch released");

        // cleanup
        jmdns.unregisterAllServices();
        update = null;
        handler = null;
        serviceListLock.lock();
        services.clear();
        serviceListLock.unlock();
    }

    public ArrayList<FlameService> getServices() {
        // thread-safe way of getting a copy of the services array.
        // TODO - really thread safe? how deep is clone?
        serviceListLock.lock();
        ArrayList<FlameService> copy = (ArrayList<FlameService>)this.services.clone();
        serviceListLock.unlock();
        return copy;
    }

    public void serviceTypeAdded(ServiceEvent event) {
        Log.v(TAG, "new type: " + event.getType());
        final String type = event.getType();
        event.getDNS().addServiceListener(type, this);
    }

    public void subTypeForServiceTypeAdded(ServiceEvent event) {
        Log.v(TAG, "subTypeForServiceTypeAdded " + event);
        final String type = event.getType();
        event.getDNS().addServiceListener(type, this);
    }

    public void serviceAdded(ServiceEvent event) {
        Log.v(TAG, "serviceAdded: " + event.getName() + " / " + event.getType());
        ServiceInfo info = event.getDNS().getServiceInfo(event.getType(), event.getName(), 0);
        serviceListLock.lock();
        services.add(new FlameService(event, info));
        serviceListLock.unlock();
        handler.post(update);
    }

    public void serviceRemoved(ServiceEvent event) {
        Log.v(TAG, "serviceRemoved: "+event.getName() + " on " + event.getType());
        serviceListLock.lock();
        services.remove(new FlameService(event));
        serviceListLock.unlock();
        handler.post(update);
    }

    public void serviceResolved(ServiceEvent event) {
        Log.v(TAG, "serviceResolved: "+event.getName() + " / " + event.getType());
    }

    public void stop() {
        running = false;
        latch.countDown();
    }
}
