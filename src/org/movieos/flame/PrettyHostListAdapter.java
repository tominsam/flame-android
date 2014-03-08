package org.movieos.flame;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PrettyHostListAdapter extends BaseAdapter {
    static String TAG = "Flame::PrettyListAdapter";

    ArrayList<FlameHost> hosts;
    Context context;

    public PrettyHostListAdapter(Context c) {
        super();
        hosts = new ArrayList<FlameHost>();
        context = c;
    }

    public void setHosts(ArrayList<FlameHost> hosts) {
        this.hosts = hosts;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return hosts.size();
    }

    @Override
    public FlameHost getItem(int i) {
        return hosts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        FlameHost host = getItem(position);

        View rowView;
        if (view == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.cell_with_image, viewGroup, false);
        } else {
            rowView = view;
        }

        ((TextView)rowView.findViewById(R.id.title)).setText(host.getTitle());
        ((TextView)rowView.findViewById(R.id.subtitle)).setText(host.getSubTitle());
        ((ImageView)rowView.findViewById(R.id.hostImage)).setImageResource(host.getImageResource());

        return rowView;
    }
}



