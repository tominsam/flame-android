package org.movieos.flame.activities;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import org.movieos.flame.models.FlameHost;
import org.movieos.flame.utilities.ServiceListAdapter;

import javax.jmdns.ServiceEvent;

public class ServiceListActivity extends FlameActivity {
    static String TAG = "Flame::ServiceListActivity";

    ServiceListAdapter mAdapter;
    private String mHostIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "started!");

        Intent intent = getIntent();
        if (intent == null) {
            Log.e(TAG, "no intent in activity");
            finish();
            return;
        }

        mHostIdentifier = intent.getExtras().getString("hostIdentifier");
        if (mHostIdentifier == null) {
            Log.e(TAG, "no identifier in bundle");
            finish();
            return;
        }

        mAdapter = new ServiceListAdapter(this);
        setListAdapter(mAdapter);

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        discoveryDataChanged();
    }

    @Override
    protected void discoveryDataChanged() {
        Log.i(TAG, "updateAdapter " + mAdapter + " from " + getDiscoveryService());
        if (mAdapter != null && getDiscoveryService() != null) {
            List<FlameHost> hosts = getDiscoveryService().getHostsMatchingKey(mHostIdentifier);
            if (hosts.size() > 0) {
                mAdapter.setServices(hosts.get(0).getServices());
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FlameHost host = (FlameHost)l.getAdapter().getItem(position);
        ServiceEvent service = host.getServices().get(position);
        Intent intent = new Intent(this, ServiceDetailActivity.class);
        intent.putExtra("service", service);
        startActivity(intent);
    }

}
