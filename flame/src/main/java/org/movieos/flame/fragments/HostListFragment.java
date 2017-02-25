package org.movieos.flame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
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


public class HostListFragment extends Fragment implements FlameActivity.DiscoveredServicesChanged {

    @Nullable
    RecyclerViewFragmentBinding mBinding;

    private final HostListAdapter mAdapter = new HostListAdapter();

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = RecyclerViewFragmentBinding.inflate(inflater, container, false);
        mBinding.toolbar.setTitle(R.string.app_name);
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
        mAdapter.setHosts(getDiscovery().getHosts());
    }

    @Override
    public void onDiscoveredServicesChanged() {
        Timber.v("seen %d hosts", getDiscovery().getHosts().size());
        mAdapter.setHosts(getDiscovery().getHosts());
    }

    DiscoveryFragment getDiscovery() {
        return ((FlameActivity)getActivity()).getDiscovery();
    }

    private class HostListAdapter extends RecyclerView.Adapter<BindingViewHolder<CellWithImageBinding>> {
        private final List<FlameHost> mHosts = new ArrayList<>();

        void setHosts(List<FlameHost> hosts) {
            mHosts.clear();
            mHosts.addAll(hosts);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mHosts.size();
        }

        @Override
        public BindingViewHolder<CellWithImageBinding> onCreateViewHolder(final ViewGroup parent, final int viewType) {
            return BindingViewHolder.build(parent, R.layout.cell_with_image);
        }

        @Override
        public void onBindViewHolder(final BindingViewHolder<CellWithImageBinding> holder, final int position) {
            FlameHost host = mHosts.get(position);
            holder.getBinding().text1.setText(host.getTitle());
            holder.getBinding().text2.setText(host.getSubTitle());

            setExitTransition(TransitionInflater.from(getContext()).inflateTransition(R.transition.transition));

            holder.itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragment = ServiceListFragment.build(host);
                fragment.setEnterTransition(TransitionInflater.from(v.getContext()).inflateTransition(R.transition.transition));
                fragment.setReturnTransition(TransitionInflater.from(v.getContext()).inflateTransition(R.transition.transition));
                activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
            });
        }
    }

}

