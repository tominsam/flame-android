package org.movieos.flame.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import org.movieos.flame.R;
import org.movieos.flame.services.DiscoveryService;
import timber.log.Timber;

public class FlameActivity extends AppCompatActivity {

    private DiscoveryService mDiscoveryService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            DiscoveryService.MyBinder b = (DiscoveryService.MyBinder) binder;
            mDiscoveryService = b.getService();
            Timber.v("service connected");
        }
        public void onServiceDisconnected(ComponentName className) {
            Timber.v("service disconnected");
            mDiscoveryService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.v("onCreate");

        Intent intent = new Intent(this, DiscoveryService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.flame_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main, new HostListFragment())
                .commit();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    protected DiscoveryService getDiscoveryService() {
        return mDiscoveryService;
    }
}
