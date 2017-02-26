package org.movieos.flame.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.movieos.flame.R;
import org.movieos.flame.fragments.HostListFragment;
import timber.log.Timber;

public class FlameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("onCreate");

        setContentView(R.layout.flame_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main, new HostListFragment())
                .commit();
        }
    }

}
