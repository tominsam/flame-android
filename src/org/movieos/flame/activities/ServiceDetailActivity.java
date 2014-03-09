package org.movieos.flame.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import org.movieos.flame.models.FlameService;
import org.movieos.flame.MyApplication;

public class ServiceDetailActivity extends Activity {
    static String TAG = "Flame::ServiceDetailActivity";

    String serviceName;
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

        serviceName = intent.getExtras().getString("serviceName");
        if (serviceName == null) {
            finish();
            return;
        }

        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        MyApplication app = (MyApplication)getApplication();
//        app.addListener(this);
//        updatedHosts();
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

    FlameService getService() {
        MyApplication app = (MyApplication)getApplication();
//        return app.getService(serviceName);
        return null;
    }

//    @Override
//    public void updatedHosts() {
//        FlameService service = getService();
//        if (service == null) {
//            setTitle(serviceName);
//            return;
//        }
//        setTitle(service.getTitle());
//    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }


}
