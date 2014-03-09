package org.movieos.flame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.movieos.flame.models.FlameHost;
import org.movieos.flame.utilities.HostListAdapter;


public class AllHostsActivity extends FlameActivity {
    static String TAG = "Flame::AllHostsActivity";

    private HostListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        mAdapter = new HostListAdapter(this);
        setListAdapter(mAdapter);
    }

    @Override
    protected void discoveryDataChanged() {
        Log.i(TAG, "updateAdapter " + mAdapter + " from " + getDiscoveryService());
        if (mAdapter != null && getDiscoveryService() != null) {
            mAdapter.setHosts(getDiscoveryService().getHosts());
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FlameHost host = (FlameHost)l.getAdapter().getItem(position);
        Intent intent = new Intent(this, ServiceListActivity.class);
        Log.v(TAG, "tapped host " + host.getTitle());
        intent.putExtra("hostIdentifier", host.getIdentifer());
        startActivity(intent);
    }

}

