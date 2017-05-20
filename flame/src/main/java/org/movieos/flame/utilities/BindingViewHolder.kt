package org.movieos.flame.utilities

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class BindingViewHolder<out T : ViewDataBinding> private constructor(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    companion object {

        fun <T : ViewDataBinding> build(container: ViewGroup, @LayoutRes layoutRes: Int): BindingViewHolder<T> {
            val binding = DataBindingUtil.inflate<T>(LayoutInflater.from(container.context), layoutRes, container, false)
            return BindingViewHolder(binding)
        }
    }
}
