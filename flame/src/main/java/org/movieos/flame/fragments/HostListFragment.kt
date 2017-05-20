package org.movieos.flame.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.movieos.flame.MyApplication
import org.movieos.flame.R
import org.movieos.flame.databinding.CellWithImageBinding
import org.movieos.flame.databinding.RecyclerViewFragmentBinding
import org.movieos.flame.models.FlameHost
import org.movieos.flame.utilities.BindingViewHolder
import org.movieos.flame.utilities.DiscoveryService
import timber.log.Timber
import java.util.*


class HostListFragment : Fragment() {

    internal var binding: RecyclerViewFragmentBinding? = null

    private val adapter = HostListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = RecyclerViewFragmentBinding.inflate(inflater, container, false)
        binding!!.toolbar.setTitle(R.string.app_name)
        binding!!.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding!!.recyclerView.adapter = adapter
        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider))
        binding!!.recyclerView.addItemDecoration(divider)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (binding != null) {
            binding!!.unbind()
            binding = null
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.setHosts(MyApplication.discoveryService.hosts)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DiscoveryService.HostsChangedNotification) {
        Timber.v("seen %d hosts", MyApplication.discoveryService.hosts.size)
        adapter.setHosts(MyApplication.discoveryService.hosts)
    }

    private inner class HostListAdapter : RecyclerView.Adapter<BindingViewHolder<CellWithImageBinding>>() {
        private val mHosts = ArrayList<FlameHost>()

        internal fun setHosts(hosts: List<FlameHost>) {
            mHosts.clear()
            mHosts.addAll(hosts)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return mHosts.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<CellWithImageBinding> {
            return BindingViewHolder.build<CellWithImageBinding>(parent, R.layout.cell_with_image)
        }

        override fun onBindViewHolder(holder: BindingViewHolder<CellWithImageBinding>, position: Int) {
            val host = mHosts[position]
            holder.binding.text1.text = host.title
            holder.binding.text2.text = host.subTitle

            holder.itemView.setOnClickListener { v ->
                val activity = v.context as AppCompatActivity
                val fragment = ServiceListFragment.build(host)
                activity.supportFragmentManager
                        .beginTransaction()
                        .setTransition(TRANSIT_FRAGMENT_CLOSE)
                        .replace(R.id.main, fragment)
                        .addToBackStack(null)
                        .commit()
            }
        }
    }

}

