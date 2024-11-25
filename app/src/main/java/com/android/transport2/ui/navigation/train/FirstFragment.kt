package com.android.transport2.ui.navigation.train

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.android.BaseFragment
import com.android.transport2.arch.android.Load
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime
import com.android.transport2.databinding.FragmentFirstBinding
import com.android.transport2.ui.main.PlaceholderAdapter
import com.android.transport2.ui.navigation.tube.line.LineActivity
import com.android.transport2.ui.navigation.tube.line.station.StationActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : BaseFragment<TrainMvp.Presenter>(), TrainMvp.View, Load {

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100

        fun newInstance() : FirstFragment {
            return FirstFragment()
        }
    }

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlaceholderAdapter()
        binding.homeNearbyRecycler.layoutManager = LinearLayoutManager(activity)
        binding.homeNearbyRecycler.itemAnimator = DefaultItemAnimator()
        binding.homeNearbyRecycler.adapter = adapter

        activity?.let { activity ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        }

        binding.homeSwipeRefresh.setOnRefreshListener {
            presenter.onRefresh()
        }

        presenter = TrainPresenter(this)
        presenter.onCreate()
    }

    override fun load() {
    }

    @SuppressLint("MissingPermission")
    override fun showNearbyStops(stops: List<TubeStop>) {
        if (binding.homeSwipeRefresh.isRefreshing) {
            binding.homeSwipeRefresh.isRefreshing = false
        }
//        if (adapter is PlaceholderAdapter) {
//            (adapter as PlaceholderAdapter).stopAnimation()
//        }
//        adapter = TubeAdapter(activity!!, this)
//        binding.homeNearbyRecycler.adapter = adapter
//        if (adapter is TubeAdapter) {
//            (adapter as TubeAdapter).showTube(lines.sortedBy { it.name })
//        }

        //        if (adapter is PlaceholderAdapter) {
//            (adapter as PlaceholderAdapter).stopAnimation()
//        }
//        adapter = LineAdapter(line, this, this)
//        binding.lineRecyclerView.adapter = adapter
//        if (adapter is LineAdapter) {
        fusedLocationClient.getCurrentLocation(PERMISSION_REQUEST_ACCESS_FINE_LOCATION, null)
            .addOnSuccessListener { location ->
                val filtered = stops.filter { stop ->
                    val stopLocation = Location("").apply { latitude = stop.lat!!; longitude = stop.lon!! }
                    location.distanceTo(stopLocation) < 1000
                }.distinctBy { it.id }
                val filteredSorted = filtered.sortedBy { stop ->
                    val stopLocation = Location("").apply { latitude = stop.lat!!; longitude = stop.lon!! }
                    location.distanceTo(stopLocation)
                }
                if (adapter is PlaceholderAdapter) {
                    (adapter as PlaceholderAdapter).stopAnimation()
                }
                activity?.let { activity ->
                    adapter = HomeLineAdapter(activity, this)
                    binding.homeNearbyRecycler.adapter = adapter
                    if (filteredSorted.isEmpty()) {
                        (adapter as HomeLineAdapter).showStops(stops.sortedBy { it.name })
                    } else {
                        (adapter as HomeLineAdapter).showStops(filteredSorted)
                    }
                }
            }
            .addOnFailureListener {
                if (adapter is PlaceholderAdapter) {
                    (adapter as PlaceholderAdapter).stopAnimation()
                }
                activity?.let { activity ->
                    adapter = HomeLineAdapter(activity, this)
                    binding.homeNearbyRecycler.adapter = adapter
                    (adapter as HomeLineAdapter).showStops(stops.sortedBy { it.name })
                }
            }
    }

    override fun showDefaultTimetable(times: Map<TubeManager.TubeDirection, List<TubeTime>>) {
        TODO("Not yet implemented")
    }

    override fun showError() {
        TODO("Not yet implemented")
    }

    override fun onLineClicked(line: TubeManager.TubeLine) {
        LineActivity.start(activity!!, line)
    }

    override fun onStationClicked(line: TubeManager.TubeLine, station: TubeStop) {
        StationActivity.start(activity!!, line, station)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun finish() {

    }
}