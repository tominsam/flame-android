package org.movieos.flame.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.movieos.flame.R;
import org.movieos.flame.fragments.HostListFragment;
import org.movieos.flame.fragments.DiscoveryFragment;
import timber.log.Timber;

public class FlameActivity extends AppCompatActivity {

    private static final String DISCOVERY_FRAGMENT_TAG = "DiscoveryFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("onCreate");

        setContentView(R.layout.flame_activity);

        if (savedInstanceState == null) {

            // We're going to put the service browser in a UI-less fragment with
            // retain instance state turned on so that it survives rotation but
            // not activity destruction.
            getSupportFragmentManager()
                .beginTransaction()
                .add(new DiscoveryFragment(), DISCOVERY_FRAGMENT_TAG)
                .commitNow();

            // Default root UI fragment
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main, new HostListFragment())
                .commit();
        }
    }

    public DiscoveryFragment getDiscovery() {
        return (DiscoveryFragment) getSupportFragmentManager().findFragmentByTag(DISCOVERY_FRAGMENT_TAG);
    }

    public void onDiscoveredServicesChanged() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main);
        if (fragment instanceof DiscoveredServicesChanged) {
            ((DiscoveredServicesChanged) fragment).onDiscoveredServicesChanged();
        }
    }

    public interface DiscoveredServicesChanged {
        void onDiscoveredServicesChanged();
    }
}
