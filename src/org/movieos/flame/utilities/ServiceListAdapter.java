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

import org.movieos.flame.models.FlameService;
import org.movieos.flame.R;

import javax.jmdns.ServiceEvent;

public class ServiceListAdapter extends BaseAdapter {
    static String TAG = "Flame::ServiceListAdapter";

    List<ServiceEvent> mServices;
    Context mContext;

    public ServiceListAdapter(Context c) {
        super();
        mServices = new ArrayList<>();
        mContext = c;
    }

    public void setServices(List<ServiceEvent> services) {
        this.mServices = services;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mServices.size();
    }

    @Override
    public ServiceEvent getItem(int i) {
        return mServices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ServiceEvent service = getItem(position);

        View rowView;
        if (view == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            rowView = inflater.inflate(R.layout.cell_with_image, viewGroup, false);
        } else {
            rowView = view;
        }

        ((TextView)rowView.findViewById(R.id.title)).setText(service.getName());
        ((TextView)rowView.findViewById(R.id.subtitle)).setText(service.getType());
        //((ImageView)rowView.findViewById(R.id.hostImage)).setImageResource(service.getImageResource());

        return rowView;
    }
}



