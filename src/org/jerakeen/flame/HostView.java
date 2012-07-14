package org.jerakeen.flame;

import android.*;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class HostView extends ListActivity implements FlameListener {
    static String TAG = "HostList::HostView";

    String hostName;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "started!");

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }

        hostName = intent.getExtras().getString("hostName");
        if (hostName == null) {
            finish();
            return;
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        setListAdapter(arrayAdapter);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public void updatedHosts() {
        MyApplication app = (MyApplication)getApplication();
        FlameHost host = app.getHost(hostName);
        setTitle(host.getTitle());
        arrayAdapter.clear();
        for (FlameService service : host.getServices()) {
            arrayAdapter.add(service.toString());
        }
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication app = (MyApplication)getApplication();
        app.removeListener(this);
    }
}
