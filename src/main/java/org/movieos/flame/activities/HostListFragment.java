package org.movieos.flame.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.movieos.flame.R;
import org.movieos.flame.services.DiscoveryService;
import org.movieos.flame.utilities.HostListAdapter;
import timber.log.Timber;


public class HostListFragment extends Fragment implements DiscoveryService.DiscoveryServiceListener {

    private HostListAdapter mAdapter;

    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle("Flame");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mAdapter = new HostListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        onDiscoveredServicesChanged();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DiscoveryService.registerListener(this);
    }

    @Override
    public void onStop() {
        DiscoveryService.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onDiscoveredServicesChanged() {
        FlameActivity activity = (FlameActivity) getActivity();
        if (activity != null && mAdapter != null && activity.getDiscoveryService() != null) {
            Timber.v("seen %d hosts", activity.getDiscoveryService().getHosts().size());
            mAdapter.setHosts(activity.getDiscoveryService().getHosts());
        }
    }
}

