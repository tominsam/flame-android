package org.movieos.flame.models

import android.net.nsd.NsdServiceInfo
import org.movieos.flame.utilities.ServiceLookup
import java.util.*

class FlameService(s: NsdServiceInfo?) {

    var info: NsdServiceInfo
        internal set
    var hostIdentifiers: ArrayList<String>
        internal set
    var serviceLookup: ServiceLookup? = null
        internal set

    init {
        if (s == null) {
            throw RuntimeException("service must be set")
        }
        info = s
        hostIdentifiers = ArrayList<String>()
        //        if (service.getInfo() != null) {
        //            if (!service.getInfo().getServer().isEmpty()) {
        //                hostIdentifiers.add(service.getInfo().getServer());
        //            }
        //            for (String address : service.getInfo().getHostAddresses()) {
        //                if (!address.isEmpty()) {
        //                    hostIdentifiers.add(address);
        //                }
        //            }
        //            hostIdentifiers.add(toString());
        //
        //            serviceLookup = ServiceLookup.get(service.getInfo().getApplication());
        //        }
    }

    override fun equals(o: Any?): Boolean {
        if (o !is FlameService) {
            return false
        }
        return o.toString() == toString()
    }

    override fun toString(): String {
        return "${info.serviceName} ${info.serviceType}"
    }

    val ipAddress: String
        get() = info.host.hostAddress

    // TODO probably not good enough
    val identifier: String
        get() = "${info.serviceName} ${info.serviceType}"

    //        Log.v(TAG, "app is " + service.getInfo().getApplication());
    //        if (serviceLookup != null) {
    //            return serviceLookup.getDescription();
    //        }
    //        return service.getType();
    val title: String?
        get() = null

    //        return service.getName();
    val subTitle: String?
        get() = null

    val imageResource: Int
        get() {
            if (serviceLookup != null) {
                return serviceLookup!!.drawable
            }
            return 0
        }

}
