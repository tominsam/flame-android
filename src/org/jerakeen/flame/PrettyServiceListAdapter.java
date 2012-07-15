package org.jerakeen.flame;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PrettyServiceListAdapter extends BaseAdapter {
    static String TAG = "Flame::PrettyListAdapter";

    ArrayList<FlameService> services;
    Context context;

    public PrettyServiceListAdapter(Context c) {
        super();
        services = new ArrayList<FlameService>();
        context = c;
    }

    public void setServices(ArrayList<FlameService> services) {
        this.services = services;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public FlameService getItem(int i) {
        return services.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        FlameService service = getItem(position);

        View rowView;
        if (view == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.cell_with_image, viewGroup, false);
        } else {
            rowView = view;
        }

        ((TextView)rowView.findViewById(R.id.title)).setText(service.getTitle());
        ((TextView)rowView.findViewById(R.id.subtitle)).setText(service.getSubTitle());
        ((ImageView)rowView.findViewById(R.id.hostImage)).setImageResource(service.getImageResource());

        return rowView;
    }
}



