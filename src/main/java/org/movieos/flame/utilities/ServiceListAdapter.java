package org.movieos.flame.utilities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.movieos.flame.R;

import java.util.ArrayList;
import java.util.List;

import javax.jmdns.ServiceEvent;

public class ServiceListAdapter extends RecyclerView.Adapter {
    static String TAG = "Flame::ServiceListAdapter";

    final List<ServiceEvent> mServices = new ArrayList<>();

    public ServiceListAdapter() {
        super();
    }

    public void setServices(List<ServiceEvent> services) {
        mServices.clear();
        mServices.addAll(services);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mServices.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_with_image, parent, false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ServiceEvent service = mServices.get(position);

        ((TextView)holder.itemView.findViewById(android.R.id.text1)).setText(service.getName());
        ((TextView)holder.itemView.findViewById(android.R.id.text2)).setText(service.getType());
        //((ImageView)holder.itemView.findViewById(android.R.id.icon)).setImageResource(service.getImageResource());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                    .beginTransaction()
//                    .replace(R.id.main, ServiceListFragment.make(host))
                    .commit();
            }
        });
    }

}



