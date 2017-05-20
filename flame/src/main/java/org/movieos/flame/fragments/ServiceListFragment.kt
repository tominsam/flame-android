package org.movieos.flame.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import javax.jmdns.ServiceEvent

class ServiceListFragment : android.support.v4.app.Fragment() {

    private val adapter = ServiceListAdapter()
    var hostIdentifier: String? = null
    var binding: RecyclerViewFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hostIdentifier = arguments.getString(HOST_IDENTIFIER)
        if (hostIdentifier == null) {
            Timber.e("no identifier in bundle")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = RecyclerViewFragmentBinding.inflate(inflater, container, false)

        binding.toolbar.title = hostIdentifier
        val back = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back_black_24dp)
        back.setTint(0xFFFFFFFF.toInt())
        binding.toolbar.navigationIcon = back
        binding.toolbar.setNavigationOnClickListener { activity.onBackPressed() }

        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        val divider = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider))
        binding.recyclerView.addItemDecoration(divider)

        this.binding = binding
        return binding.root
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
        onMessageEvent(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DiscoveryService.HostsChangedNotification?) {
        val hosts = MyApplication.discoveryService.getHostsMatchingKey(hostIdentifier)
        if (!hosts.isEmpty()) {
            adapter.setServices(hosts[0].services)
        }
    }

    private class ServiceListAdapter internal constructor() : RecyclerView.Adapter<BindingViewHolder<CellWithImageBinding>>() {

        private val services = ArrayList<ServiceEvent>()

        internal fun setServices(services: List<ServiceEvent>) {
            this.services.clear()
            this.services.addAll(services)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int {
            return services.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<CellWithImageBinding> {
            return BindingViewHolder.build(parent, R.layout.cell_with_image)
        }

        override fun onBindViewHolder(holder: BindingViewHolder<CellWithImageBinding>, position: Int) {
            val service = services[position]
            holder.binding.text1.text = service.name
            holder.binding.text2.text = service.type

            holder.itemView.setOnClickListener({ v ->
                val activity = v.context as AppCompatActivity
                activity.supportFragmentManager
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        //                    .replace(R.id.main, ServiceListFragment.make(host))
                        .commit()
            })
        }

    }

    companion object {

        private val HOST_IDENTIFIER = "hostIdentifier"

        fun build(host: FlameHost): Fragment {
            val fragment = ServiceListFragment()
            fragment.arguments = Bundle()
            fragment.arguments.putString(HOST_IDENTIFIER, host.identifer)
            return fragment
        }
    }
}
