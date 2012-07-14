package org.jerakeen.flame;

import android.*;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HostView extends ListActivity implements FlameListener {
    static String TAG = "Flame::HostView";

    String hostName;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "started!");

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        hostName = intent.getExtras().getString("hostIdentifier");
        if (hostName == null) {
            finish();
            return;
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        setListAdapter(arrayAdapter);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        MyApplication app = (MyApplication)getApplication();
        app.addListener(this);
        updatedHosts();
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

    FlameHost getHost() {
        MyApplication app = (MyApplication)getApplication();
        FlameHost host = app.getHost(hostName);
        return host;
    }

    @Override
    public void updatedHosts() {
        FlameHost host = getHost();
        arrayAdapter.clear();
        if (host != null) {
            setTitle(host.getTitle());
            for (FlameService service : host.getServices()) {
                arrayAdapter.add(service.toString());
            }
        } else {
            setTitle(hostName);
        }
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FlameHost host = getHost();
        FlameService service = host.getServices().get(position);
        Intent intent = new Intent(this, ServiceView.class);
        Log.v(TAG, "tapped service " + service);
        intent.putExtra("serviceName", service.toString());
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        MyApplication app = (MyApplication)getApplication();
        app.removeListener(this);
        arrayAdapter.clear();
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }
}
