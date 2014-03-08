package org.movieos.flame.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import org.movieos.flame.models.FlameHost;
import org.movieos.flame.models.FlameService;
import org.movieos.flame.MyApplication;
import org.movieos.flame.utilities.PrettyServiceListAdapter;
import org.movieos.flame.R;

public class HostView extends ListActivity implements MyApplication.FlameListener {
    static String TAG = "Flame::HostView";

    String hostName;
    PrettyServiceListAdapter adapter;

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

        adapter = new PrettyServiceListAdapter(this);
        setListAdapter(adapter);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
        if (host != null) {
            setTitle(host.getTitle());
            adapter.setServices(host.getServices());
        } else {
            setTitle(hostName);
            adapter.setServices(new ArrayList<FlameService>());
        }
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
        adapter.setServices(new ArrayList<FlameService>());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }
}
