package com.android.transport2.ui.navigation.tube

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
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.Tube
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.databinding.FragmentSecondBinding
import com.android.transport2.ui.main.PlaceholderAdapter
import com.android.transport2.ui.navigation.tube.line.LineActivity
import io.reactivex.disposables.Disposable

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : BaseFragment<TubeMvp.Presenter>(), TubeMvp.View, Load {

    companion object {
        fun newInstance() : SecondFragment {
            return SecondFragment()
        }
    }

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PlaceholderAdapter()
        binding.tubeRecycler.layoutManager = LinearLayoutManager(activity)
        binding.tubeRecycler.itemAnimator = DefaultItemAnimator()
        binding.tubeRecycler.adapter = adapter

        binding.tubeSwipeRefresh.setOnRefreshListener {
            presenter.onRefresh()
        }

        presenter = TubePresenter(this)
        presenter.onCreate()
    }

    override fun showTube(lines: List<Tube>) {
        if (binding.tubeSwipeRefresh.isRefreshing) {
            binding.tubeSwipeRefresh.isRefreshing = false
        }
        if (adapter is PlaceholderAdapter) {
            (adapter as PlaceholderAdapter).stopAnimation()
        }
        adapter = TubeAdapter(activity!!, this)
        binding.tubeRecycler.adapter = adapter
        if (adapter is TubeAdapter) {
            (adapter as TubeAdapter).showTube(lines.sortedBy { it.name })
        }
    }

    override fun showError() {
        if (binding.tubeSwipeRefresh.isRefreshing) {
            binding.tubeSwipeRefresh.isRefreshing = false
        }
        if (adapter is PlaceholderAdapter) {
            (adapter as PlaceholderAdapter).stopAnimation()
            (adapter as PlaceholderAdapter).clear()
        }
        adapter = TubeAdapter(activity!!, this)
        binding.tubeRecycler.adapter = adapter
        if (adapter is TubeAdapter) {
            (adapter as TubeAdapter).showError()
        }
    }

    override fun onLineClicked(line: TubeLine) {
        LineActivity.start(activity!!, line)
    }

    override fun onStationClicked(line: TubeLine, station: TubeStop) {
        throw NotImplementedError("Not implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun finish() {

    }
}