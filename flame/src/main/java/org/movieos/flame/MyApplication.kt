package org.movieos.flame

import android.app.Application

import org.movieos.flame.utilities.DiscoveryService
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        singleton = this
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        var singleton: MyApplication? = null
        val discoveryService: DiscoveryService by lazy { DiscoveryService(singleton!!) }
    }
}
