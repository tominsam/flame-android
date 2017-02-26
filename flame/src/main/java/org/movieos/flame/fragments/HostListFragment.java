package org.movieos.flame.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.movieos.flame.MyApplication;
import org.movieos.flame.R;
import org.movieos.flame.databinding.CellWithImageBinding;
import org.movieos.flame.databinding.RecyclerViewFragmentBinding;
import org.movieos.flame.models.FlameHost;
import org.movieos.flame.utilities.BindingViewHolder;
import org.movieos.flame.utilities.DiscoveryService;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;


public class HostListFragment extends Fragment {

    @Nullable
    RecyclerViewFragmentBinding mBinding;

    private final HostListAdapter mAdapter = new HostListAdapter();

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = RecyclerViewFragmentBinding.inflate(inflater, container, false);
        mBinding.toolbar.setTitle(R.string.app_name);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mBinding.recyclerView.setAdapter(mAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider));
        mBinding.recyclerView.addItemDecoration(divider);
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
        mAdapter.setHosts(MyApplication.discoveryService().getHosts());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DiscoveryService.HostsChangedNotification event) {
        Timber.v("seen %d hosts", MyApplication.discoveryService().getHosts().size());
        mAdapter.setHosts(MyApplication.discoveryService().getHosts());
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

            holder.itemView.setOnClickListener(v -> {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragment = ServiceListFragment.build(host);
                activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, fragment)
                    .addToBackStack(null)
                    .commit();
            });
        }
    }

}

