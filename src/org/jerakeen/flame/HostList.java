package org.jerakeen.flame;

import java.util.ArrayList;

import android.R;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class HostList extends ListActivity implements FlameListener {
    static String TAG = "Flame::HostList";

    PrettyHostListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        adapter = new PrettyHostListAdapter(this);
        setListAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        MyApplication app = (MyApplication)getApplication();
        app.addListener(this);
        updatedHosts();
    }

    public void updatedHosts() {
        Log.v(TAG, "updatedHosts");
        MyApplication app = (MyApplication)getApplication();
        adapter.setHosts(app.getHosts());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        MyApplication app = (MyApplication)getApplication();
        FlameHost host = app.getHosts().get(position);
        Intent intent = new Intent(this, HostView.class);
        Log.v(TAG, "tapped host " + host.getTitle());
        intent.putExtra("hostIdentifier", host.getIdentifier());
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        MyApplication app = (MyApplication)getApplication();
        app.removeListener(this);
        adapter.setHosts(new ArrayList<FlameHost>());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

