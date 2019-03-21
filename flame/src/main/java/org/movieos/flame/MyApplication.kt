package org.movieos.flame

import android.app.Application
import android.util.Log
import org.movieos.flame.utilities.DiscoveryService
import timber.log.Timber

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        singleton = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String?, throwable: Throwable?) {
                    if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                        return
                    }
                }
            })
        }

    }

    companion object {
        var singleton: MyApplication? = null
        val discoveryService: DiscoveryService by lazy { DiscoveryService(singleton!!) }
    }
}
