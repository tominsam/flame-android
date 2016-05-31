package org.movieos.flame.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.movieos.flame.R;
import org.movieos.flame.models.FlameHost;
import org.movieos.flame.services.DiscoveryService;
import org.movieos.flame.utilities.ServiceListAdapter;
import timber.log.Timber;

import java.util.List;

public class ServiceListFragment extends Fragment implements DiscoveryService.DiscoveryServiceListener {

    ServiceListAdapter mAdapter;
    String mHostIdentifier;
    RecyclerView mRecyclerView;

    public static Fragment make(final FlameHost host) {
        Fragment fragment = new ServiceListFragment();
        fragment.setArguments(new Bundle());
        fragment.getArguments().putString("hostIdentifier", host.getIdentifer());
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHostIdentifier = getArguments().getString("hostIdentifier");
        if (mHostIdentifier == null) {
            Timber.e("no identifier in bundle");
        }
        mAdapter = new ServiceListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view_fragment, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(mHostIdentifier);
        Drawable bsck = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back_black_24dp);
        bsck.setTint(0xffffffff);
        toolbar.setNavigationIcon(bsck);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getActivity().onBackPressed();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
            final List<FlameHost> hosts = activity.getDiscoveryService().getHostsMatchingKey(mHostIdentifier);
            mAdapter.setServices(hosts.get(0).getServices());
        }
    }
}
