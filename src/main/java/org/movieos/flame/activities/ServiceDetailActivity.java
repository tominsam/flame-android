package org.movieos.flame.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.movieos.flame.R;

public class ServiceDetailActivity extends FlameActivity {
    static String TAG = "Flame::ServiceDetailActivity";

    ArrayAdapter<String> mAdapter;
    private String mHostIdentifier;
    private String mServiceIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "started!");

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        mHostIdentifier = intent.getExtras().getString("hostIdentifier");
        mServiceIdentifier = intent.getExtras().getString("serviceIdentifier");
        if (mHostIdentifier == null || mServiceIdentifier == null) {
            Log.e(TAG, "no identifier in bundle");
            finish();
            return;
        }

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new ArrayAdapter<>(this, R.layout.cell_with_image);
        //setListAdapter(mAdapter);
        //discoveryDataChanged();
    }

//    @Override
//    protected void discoveryDataChanged() {
//        if (getDiscoveryService() == null) {
//            return;
//        }
//        List<FlameHost> hosts = getDiscoveryService().getHostsMatchingKey(mHostIdentifier);
//        for (ServiceEvent service : hosts.get(0).getServices()) {
//            if (TextUtils.equals(service.getType(), mServiceIdentifier)) {
//                setTitle(service.getName());
//                return;
//            }
//        }
//        Toast.makeText(this, R.string.service_removed, 3000).show();
//        finish();
//    }

}
