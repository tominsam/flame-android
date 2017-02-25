package org.movieos.flame.utilities;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class BindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public static <T extends ViewDataBinding> BindingViewHolder<T> build(ViewGroup container, @LayoutRes int layoutRes) {
        T binding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext()), layoutRes, container, false);
        return new BindingViewHolder<>(binding);
    }

    @NonNull
    private T mBinding;

    private BindingViewHolder(@NonNull final T binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    @NonNull
    public T getBinding() {
        return mBinding;
    }
}
