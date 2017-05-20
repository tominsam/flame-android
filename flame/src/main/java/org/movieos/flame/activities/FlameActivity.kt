package org.movieos.flame.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import org.movieos.flame.R
import org.movieos.flame.fragments.HostListFragment
import timber.log.Timber

class FlameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.v("onCreate")

        setContentView(R.layout.flame_activity)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.main, HostListFragment())
                    .commit()
        }
    }

}
