package org.movieos.flame.utilities

import android.content.Context
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.text.TextUtils
import android.text.format.Formatter
import org.greenrobot.eventbus.EventBus
import org.movieos.flame.models.FlameHost
import timber.log.Timber
import java.io.IOException
import java.net.InetAddress
import java.util.*
import javax.jmdns.*

class DiscoveryService(tempContext: Context) : ServiceTypeListener, ServiceListener {

    private val services: HashMap<String, ServiceEvent> = HashMap()
    private val context: Context = tempContext.applicationContext
    private var jmDNSService: JmDNS? = null
    private val wifiLock: WifiManager.MulticastLock?

    init {
        val wifi = context.applicationContext.getSystemService(android.content.Context.WIFI_SERVICE) as android.net.wifi.WifiManager
        wifiLock = wifi.createMulticastLock("Flame")
        wifiLock.setReferenceCounted(true)
        wifiLock.acquire()

        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void): Void? {
                try {
                    val ip = Formatter.formatIpAddress(wifi.connectionInfo.ipAddress)
                    val bindingAddress = InetAddress.getByName(ip)
                    jmDNSService = JmDNS.create(bindingAddress, "Android")
                    jmDNSService!!.addServiceTypeListener(this@DiscoveryService)

                    // broadcast that we are running flame
                    val serviceInfo = ServiceInfo.create(FLAME_SERVICE_NAME, "Flame", FLAME_SERVICE_PORT, jmDNSService!!.name)
                    jmDNSService!!.registerService(serviceInfo)

                    Timber.v("Discovery started")
                } catch (e: IOException) {
                    Timber.e(e, "Failed discovery")
                }

                return null
            }
        }.execute()

        EventBus.getDefault().post(HostsChangedNotification())
    }

    fun stop() {
        Timber.v("onDestroy")
        wifiLock?.release()
        if (jmDNSService != null) {
            jmDNSService!!.unregisterAllServices()
            jmDNSService!!.removeServiceTypeListener(this)
        }
    }

    /// data accessors

    val hosts: List<FlameHost>
        get() = getHostsMatchingKey(null)

    fun getHostsMatchingKey(filter: String?): List<FlameHost> {
        val grouped = HashMap<String, MutableList<ServiceEvent>>()
        for (service in services.values) {
            val key = FlameHost.hostIdentifierForService(service)
            if (filter != null && !TextUtils.equals(filter, key)) {
                continue
            }
            if (grouped[key] == null) {
                grouped[key] = ArrayList()
            }
            grouped[key]?.add(service)
        }

        val hosts = ArrayList<FlameHost>()
        for ((_, value) in grouped) {
            val host = FlameHost(value)
            hosts.add(host)
        }

        Collections.sort(hosts) { lhs, rhs -> lhs.title.compareTo(rhs.title, ignoreCase = true) }

        return hosts
    }

    /// actions

    private fun uniqueIdentifierForService(service: ServiceEvent): String {
        return String.format("%s %s", service.name, service.type)
    }

    /// jmdns callbacks

    override fun serviceTypeAdded(event: ServiceEvent) {
        //Timber.v("new type: " + event.getType());
        event.dns.addServiceListener(event.type, this)
    }

    override fun subTypeForServiceTypeAdded(event: ServiceEvent) {
        //Timber.v("subTypeForServiceTypeAdded " + event);
    }

    override fun serviceAdded(event: ServiceEvent) {
        //Timber.v("serviceAdded: " + event.getName() + " / " + event.getType());
        event.dns.requestServiceInfo(event.type, event.name, true)
    }

    override fun serviceRemoved(event: ServiceEvent) {
        Timber.v("serviceRemoved: %s / %s", event.name, event.type)
        val key = uniqueIdentifierForService(event)
        if (services.containsKey(key)) {
            services.remove(key)
            EventBus.getDefault().post(HostsChangedNotification())
        }
    }

    override fun serviceResolved(event: ServiceEvent) {
        Timber.v("serviceResolved: ${event.name} / ${event.type}")
        services.put(uniqueIdentifierForService(event), event)
        EventBus.getDefault().post(HostsChangedNotification())
    }

    class HostsChangedNotification

    companion object {
        private val FLAME_SERVICE_NAME = "_flame._tcp.local."
        private val FLAME_SERVICE_PORT = 11812
    }

}
