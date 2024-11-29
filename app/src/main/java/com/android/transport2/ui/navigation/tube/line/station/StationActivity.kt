package com.android.transport2.ui.navigation.tube.line.station

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.R
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
    private var recyclerViewState: Parcelable? = null

    private lateinit var binding: ActivityStationBinding
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var line: TubeLine
    private lateinit var station: TubeStop
    private lateinit var refreshDrawable: Drawable

    @Suppress("DEPRECATION")
    private val handler = Handler()
    private val runnable = object : Runnable {
        override fun run() {
            animationRunnable()
            handler.postDelayed(this, 10000)
        }
    }

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
        stopAnimation()
        setConnectionStatus(Colour.GREY) // adding this here because too lazy to make setActivityDefaults (or something like that)

        disposable = Observable.interval(10000, TimeUnit.MILLISECONDS)
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
            recyclerViewState = binding.stationRecyclerView.layoutManager?.onSaveInstanceState()
            (adapter as StationAdapter).showTimetable(times)
            binding.stationRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
        }
        setConnectionStatus(Colour.LIVE)
        startAnimation()
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
            (adapter as StationAdapter).showTimetable(emptyMap())
        }
        stopAnimation()
        setConnectionStatus(Colour.RED)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    private fun startAnimation() {
        refreshDrawable = try {
            binding.connectionStatusDot.drawable as AnimationDrawable
        } catch (e: ClassCastException) {
            binding.connectionStatusDot.drawable as VectorDrawable
        }
        handler.post(runnable)
    }

    private fun stopAnimation() {
        handler.removeCallbacks(runnable)
    }

    private fun animationRunnable() {
        val animationRefreshDrawable = refreshDrawable as AnimationDrawable
        if (animationRefreshDrawable.isRunning) {
            animationRefreshDrawable.stop()
        }
        animationRefreshDrawable.start()
    }

    private fun setConnectionStatus(colour: Colour) {
        val drawableStatusElement = binding.connectionStatusElement.background as GradientDrawable
        drawableStatusElement.setColor(getColor(colour.background))
        binding.connectionStatusElement.background = drawableStatusElement
        binding.connectionStatusText.setTextColor(getColor(colour.text))
        binding.connectionStatusDot.setImageResource(colour.dot)
    }

    private enum class Colour(@DrawableRes val dot: Int, @ColorRes val text: Int, @ColorRes val background: Int){
        RED(R.drawable.ic_disconnected, R.color.red, R.color.red_30),
        LIVE(R.drawable.live_refresh_animation, R.color.green, R.color.green_30),
        GREY(R.drawable.ic_outline_radio_button_unchecked_24, R.color.grey, R.color.grey_30)
    }
}