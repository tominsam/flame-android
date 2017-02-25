package org.movieos.flame.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.movieos.flame.R;
import org.movieos.flame.activities.FlameActivity;
import org.movieos.flame.databinding.CellWithImageBinding;
import org.movieos.flame.databinding.RecyclerViewFragmentBinding;
import org.movieos.flame.models.FlameHost;
import org.movieos.flame.utilities.BindingViewHolder;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

import javax.jmdns.ServiceEvent;

public class ServiceListFragment extends Fragment implements FlameActivity.DiscoveredServicesChanged {

    private static final String HOST_IDENTIFIER = "hostIdentifier";

    final ServiceListAdapter mAdapter = new ServiceListAdapter();
    String mHostIdentifier;

    @Nullable
    private RecyclerViewFragmentBinding mBinding;

    public static Fragment build(final FlameHost host) {
        Fragment fragment = new ServiceListFragment();
        fragment.setArguments(new Bundle());
        fragment.getArguments().putString(HOST_IDENTIFIER, host.getIdentifer());
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHostIdentifier = getArguments().getString(HOST_IDENTIFIER);
        if (mHostIdentifier == null) {
            Timber.e("no identifier in bundle");
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = RecyclerViewFragmentBinding.inflate(inflater, container, false);
        if (mBinding == null) {
            return null;
        }

        mBinding.toolbar.setTitle(mHostIdentifier);
        Drawable bsck = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back_black_24dp);
        bsck.setTint(0xffffffff);
        mBinding.toolbar.setNavigationIcon(bsck);
        mBinding.toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBinding != null) {
            mBinding.unbind();
            mBinding = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        onDiscoveredServicesChanged();
    }

    @Override
    public void onDiscoveredServicesChanged() {
        if (getDiscovery() == null) {
            return;
        }
        List<FlameHost> hosts = getDiscovery().getHostsMatchingKey(mHostIdentifier);
        if (!hosts.isEmpty()) {
            mAdapter.setServices(hosts.get(0).getServices());
        }
    }

    DiscoveryFragment getDiscovery() {
        return ((FlameActivity)getActivity()).getDiscovery();
    }

    private static class ServiceListAdapter extends RecyclerView.Adapter<BindingViewHolder<CellWithImageBinding>> {

        private final List<ServiceEvent> mServices = new ArrayList<>();

        ServiceListAdapter() {
            super();
        }

        void setServices(List<ServiceEvent> services) {
            mServices.clear();
            mServices.addAll(services);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mServices.size();
        }

        @Override
        public BindingViewHolder<CellWithImageBinding> onCreateViewHolder(final ViewGroup parent, final int viewType) {
            return BindingViewHolder.build(parent, R.layout.cell_with_image);
        }

        @Override
        public void onBindViewHolder(final BindingViewHolder<CellWithImageBinding> holder, final int position) {
            final ServiceEvent service = mServices.get(position);

            holder.getBinding().text1.setText(service.getName());
            holder.getBinding().text2.setText(service.getType());

            holder.itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                activity.getSupportFragmentManager()
                    .beginTransaction()
    //                    .replace(R.id.main, ServiceListFragment.make(host))
                    .commit();
            });
        }

    }
}
