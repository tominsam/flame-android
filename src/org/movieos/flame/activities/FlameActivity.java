package org.movieos.flame.activities;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.movieos.flame.services.DiscoveryService;

public abstract class FlameActivity extends ListActivity {
    private static final String TAG = "FlameActivity";

    private DiscoveryService mDiscoveryService;
    private BroadcastReceiver mBroadcastReceiver;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            DiscoveryService.MyBinder b = (DiscoveryService.MyBinder) binder;
            mDiscoveryService = b.getService();
            Log.v(TAG, "service connected");
            discoveryDataChanged();
        }
        public void onServiceDisconnected(ComponentName className) {
            Log.v(TAG, "service disconnected");
            mDiscoveryService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, getClass().toString() + " onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, getClass().toString() + " onStart");

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                discoveryDataChanged();
            }
        };
        broadcastManager.registerReceiver(mBroadcastReceiver, new IntentFilter(DiscoveryService.BROADCAST_HOSTS_CHANGED));

        Intent intent = new Intent(this, DiscoveryService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    abstract protected void discoveryDataChanged();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuItem reload = menu.add(0, Menu.NONE, 0, "Reload");
//        reload.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
//        reload.setIcon(R.drawable.menu_refresh);
//        reload.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        reload.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                Log.v(TAG, "reload"); // TODO
//                return true;
//            }
//        });
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, getClass().toString() + " onStop");
        unbindService(mConnection);
        if (mBroadcastReceiver != null) {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
            broadcastManager.unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // we can't call into here from outside at all, so finish() is fine.
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected DiscoveryService getDiscoveryService() {
        return mDiscoveryService;
    }
}
