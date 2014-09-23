package org.movieos.flame.utilities;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import org.movieos.flame.models.FlameHost;
import org.movieos.flame.R;

public class HostListAdapter extends BaseAdapter {
    static String TAG = "Flame::PrettyListAdapter";

    List<FlameHost> mHosts;
    Context mContext;

    public HostListAdapter(Context c) {
        super();
        mHosts = new ArrayList<>();
        mContext = c;
    }

    public void setHosts(List<FlameHost> hosts) {
        this.mHosts = hosts;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mHosts.size();
    }

    @Override
    public FlameHost getItem(int i) {
        return mHosts.get(i);
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
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
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



