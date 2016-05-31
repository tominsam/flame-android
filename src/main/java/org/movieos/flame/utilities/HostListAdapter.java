package org.movieos.flame.utilities;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.movieos.flame.R;
import org.movieos.flame.activities.ServiceListFragment;
import org.movieos.flame.models.FlameHost;

import java.util.ArrayList;
import java.util.List;

public class HostListAdapter extends RecyclerView.Adapter {
    final List<FlameHost> mHosts = new ArrayList<>();

    public void setHosts(List<FlameHost> hosts) {
        mHosts.clear();
        mHosts.addAll(hosts);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mHosts.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cell_with_image, parent, false);
        return new RecyclerView.ViewHolder(view) {};
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final FlameHost host = mHosts.get(position);
        ((TextView)holder.itemView.findViewById(android.R.id.text1)).setText(host.getTitle());
        ((TextView)holder.itemView.findViewById(android.R.id.text2)).setText(host.getSubTitle());
//        ((ImageView)holder.itemView.findViewById(android.R.id.icon)).setImageResource(host.getImageResource());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, ServiceListFragment.make(host))
                    .addToBackStack(null)
                    .commit();
            }
        });
    }
}



