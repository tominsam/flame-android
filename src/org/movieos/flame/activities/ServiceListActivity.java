package org.movieos.flame.activities;

import java.util.List;

import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.movieos.flame.R;
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
                setTitle(hosts.get(0).getTitle());
                mAdapter.setServices(hosts.get(0).getServices());
            } else {
                Toast.makeText(this, R.string.host_removed, 3000);
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ServiceEvent service = (ServiceEvent)l.getAdapter().getItem(position);
        Intent intent = new Intent(this, ServiceDetailActivity.class);
        List<FlameHost> hosts = getDiscoveryService().getHostsMatchingKey(mHostIdentifier);
        intent.putExtra("hostIdentifier", hosts.get(0).getIdentifer());
        intent.putExtra("serviceIdentifier", service.getType());
        startActivity(intent);
    }

}
