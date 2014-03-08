package org.movieos.flame.activities;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import org.movieos.flame.models.FlameHost;
import org.movieos.flame.MyApplication;
import org.movieos.flame.utilities.PrettyHostListAdapter;
import org.movieos.flame.R;


public class HostList extends ListActivity implements MyApplication.FlameListener {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem reload = menu.add(0, Menu.NONE, 0, "Reload");
        reload.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        reload.setIcon(R.drawable.menu_refresh);
        reload.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        reload.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.v(TAG, "reload");
                MyApplication app = (MyApplication)getApplication();
                app.reload();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu);
        return true;
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

