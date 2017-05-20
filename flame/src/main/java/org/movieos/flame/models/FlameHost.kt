package org.movieos.flame.models

import org.movieos.flame.utilities.ServiceLookup
import timber.log.Timber
import java.util.*
import javax.jmdns.ServiceEvent

class FlameHost(val services: List<ServiceEvent>) {

    val identifer: String
        get() = hostIdentifierForService(services[0])

    val lookup: ServiceLookup?
        get() = ServiceLookup[services[0].type]

    val title: String
        get() = services[0].name

    val subTitle: String
        get() = String.format(Locale.getDefault(), "%s - %d services", identifer, this.services.size)

    val imageResource: Int
        get() = lookup?.drawable ?: 0

    init {
        Collections.sort(services) { lhs, rhs ->
            val left = ServiceLookup[lhs.type]
            val right = ServiceLookup[rhs.type]
            if (left?.priority != right?.priority) {
                Integer.valueOf(left?.priority ?: 0).compareTo(right?.priority ?: 0)
            } else {
                lhs.name.compareTo(rhs.name, true)
            }
        }
    }

    override fun toString(): String {
        return identifer
    }

    companion object {

        fun hostIdentifierForService(service: ServiceEvent): String {
            return service.info.hostAddress
        }

        fun getServiceLookup(services: List<ServiceEvent>): ServiceLookup? {
            val lookups = services
                    .mapNotNull { ServiceLookup[it.type] }
                    .filter { it.priority > 0 }
                    .sortedByDescending { it.priority }

            Timber.v("lookups for %s are %s", services, lookups)
            return lookups.lastOrNull()
        }
    }
}
