package com.android.transport2.ui.navigation.commute

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.android.BaseFragment
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime
import com.android.transport2.arch.utils.Utils.commuteStation
import com.android.transport2.databinding.FragmentMiddleBinding
import com.android.transport2.ui.main.PlaceholderAdapter
import com.android.transport2.ui.navigation.tube.TubeAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MiddleFragment : BaseFragment<CommuteMvp.Presenter>(), CommuteMvp.View {

    private var recyclerViewState: Parcelable? = null

    companion object {
        private val lines = listOf(TubeLine.CIRCLE, TubeLine.HAMMERSMITH)

        fun newInstance() : MiddleFragment {
            return MiddleFragment()
        }
    }

    private var _binding: FragmentMiddleBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMiddleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlaceholderAdapter()
        binding.commuteTimetableRecycler.layoutManager = LinearLayoutManager(activity)
        binding.commuteTimetableRecycler.itemAnimator = DefaultItemAnimator()
        binding.commuteTimetableRecycler.adapter = adapter

        activity?.let { activity ->
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        }

        binding.commuteSwipeRefresh.setOnRefreshListener {
            presenter.onRefreshForDefaultTimetable(lines, commuteStation())
        }

        presenter = CommutePresenter(this)
        presenter.onCreateForDefaultTimetable(lines, commuteStation())
    }

    override fun load() {
    }

    override fun showDefaultTimetable(
        station: TubeStop,
        times: Map<TubeManager.TubeDirection, List<TubeTime>>
    ) {
        if (binding.commuteSwipeRefresh.isRefreshing) {
            binding.commuteSwipeRefresh.isRefreshing = false
        }
        if (adapter is PlaceholderAdapter) {
            (adapter as PlaceholderAdapter).stopAnimation()
        }
        activity?.let { activity ->
            adapter = StationAdapter(station, activity)
            binding.commuteTimetableRecycler.adapter = adapter
            if (adapter is StationAdapter) {
                recyclerViewState = binding.commuteTimetableRecycler.layoutManager?.onSaveInstanceState()
                (adapter as StationAdapter).showTimetable(times)
                binding.commuteTimetableRecycler.layoutManager?.onRestoreInstanceState(recyclerViewState)
            }
        }

    }

    override fun showError() {
        if (binding.commuteSwipeRefresh.isRefreshing) {
            binding.commuteSwipeRefresh.isRefreshing = false
        }
        if (adapter is PlaceholderAdapter) {
            (adapter as PlaceholderAdapter).stopAnimation()
            (adapter as PlaceholderAdapter).clear()
        }
        activity?.let { activity ->
            adapter = StationAdapter(TubeStop.emptyTubeStop(), activity)
            binding.commuteTimetableRecycler.adapter = adapter
            if (adapter is StationAdapter) {
                (adapter as StationAdapter).showTimetable(emptyMap())
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun finish() {

    }
}