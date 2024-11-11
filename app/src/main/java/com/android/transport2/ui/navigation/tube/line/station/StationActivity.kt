package com.android.transport2.ui.navigation.tube.line.station

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.android.BaseActivity
import com.android.transport2.arch.managers.TubeManager.*
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime
import com.android.transport2.databinding.ActivityStationBinding
import com.android.transport2.ui.main.PlaceholderAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class StationActivity : BaseActivity<StationMvp.Presenter>(), StationMvp.View {

    private var disposable: Disposable? = null
    private lateinit var binding: ActivityStationBinding

    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var line: TubeLine
    private lateinit var station: TubeStop

    companion object {
        const val LINE = "line"
        const val STATION = "station"

        fun start(context: Activity, line: TubeLine, station: TubeStop) {
            val intent = Intent(context, StationActivity::class.java)
            intent.putExtra(LINE, line)
            intent.putExtra(STATION, station)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        line = intent.getSerializableExtra(LINE) as TubeLine
        station = intent.getSerializableExtra(STATION)!! as TubeStop

        binding = ActivityStationBinding.inflate(layoutInflater)
        binding.toolbar.title = station.name
            .replace(" Underground", "", true)
            .replace(" Rail", "", true)
            .replace(" DLR", "", true)
        setContentView(binding.root)

        adapter = PlaceholderAdapter()
        binding.stationRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.stationRecyclerView.itemAnimator = DefaultItemAnimator()
        binding.stationRecyclerView.adapter = adapter

        binding.stationSwipeRefresh.setOnRefreshListener {
            presenter.onRefresh(line, station)
        }

        presenter = StationPresenter(this)
        presenter.onCreate(line, station)
    }

    override fun setClickables() {
        binding.close.setOnClickListener {
            finish()
        }
    }

    override fun setRefresh() {
        disposable = Observable.interval(5000, TimeUnit.MILLISECONDS)
            .repeat()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
//                binding.stationSwipeRefresh.isRefreshing = true
                presenter.onRefresh(line, station)
            }
    }

    override fun showTimetable(times: Map<TubeDirection, List<TubeTime>>) {
        if (binding.stationSwipeRefresh.isRefreshing) {
            binding.stationSwipeRefresh.isRefreshing = false
        }
        if (adapter is PlaceholderAdapter) {
            (adapter as PlaceholderAdapter).stopAnimation()
        }
        adapter = StationAdapter(line, station, this)
        binding.stationRecyclerView.adapter = adapter
        if (adapter is StationAdapter) {
            (adapter as StationAdapter).showTimetable(times)
        }
    }

    override fun showError() {
        if (binding.stationSwipeRefresh.isRefreshing) {
            binding.stationSwipeRefresh.isRefreshing = false
        }
        if (adapter is PlaceholderAdapter) {
            (adapter as PlaceholderAdapter).stopAnimation()
            (adapter as PlaceholderAdapter).clear()
        }
        adapter = StationAdapter(line, station, this)
        binding.stationRecyclerView.adapter = adapter
        if (adapter is StationAdapter) {
            (adapter as StationAdapter).showError()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}