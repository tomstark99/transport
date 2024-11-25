package com.android.transport2.ui.navigation.tube.line

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.android.BaseActivity
import com.android.transport2.arch.android.Load
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.databinding.ActivityLineBinding
import com.android.transport2.ui.main.PlaceholderAdapter
import com.android.transport2.ui.navigation.tube.line.station.StationActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LineActivity : BaseActivity<LineMvp.Presenter>(), LineMvp.View, Load {

    private lateinit var binding: ActivityLineBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var line: TubeLine

    companion object {
        const val LINE = "line"
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100

        fun start(context: Activity, line: TubeLine) {
            val intent = Intent(context, LineActivity::class.java)
            intent.putExtra(LINE, line)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        line = intent.getSerializableExtra(LINE) as TubeLine

        binding = ActivityLineBinding.inflate(layoutInflater)
        binding.toolbar.title = """${getString(line.commonName)} line"""
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(binding.root)

        adapter = PlaceholderAdapter()
        binding.lineRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.lineRecyclerView.itemAnimator = DefaultItemAnimator()
        binding.lineRecyclerView.adapter = adapter

        binding.lineSwipeRefresh.setOnRefreshListener {
            presenter.onRefresh(line)
        }

        presenter = LinePresenter(this)
        presenter.onCreate(line)
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
//            when (grantResults[0]) {
//                PackageManager.PERMISSION_GRANTED -> getLocation()
//                PackageManager.PERMISSION_DENIED -> //Tell to user the need of grant permission
//            }
//        }
//    }
//
//    private fun getLocation()

    override fun setClickables() {
        binding.close.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    override fun showStops(stops: List<TubeStop>) {
        if (binding.lineSwipeRefresh.isRefreshing) {
            binding.lineSwipeRefresh.isRefreshing = false
        }
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
                    location.distanceTo(stopLocation) < 2500
                }
                val filteredSorted = filtered.sortedBy { stop ->
                    val stopLocation = Location("").apply { latitude = stop.lat!!; longitude = stop.lon!! }
                    location.distanceTo(stopLocation)
                }
                if (adapter is PlaceholderAdapter) {
                    (adapter as PlaceholderAdapter).stopAnimation()
                }
                adapter = LineAdapter(this, this)
                binding.lineRecyclerView.adapter = adapter
                if (filteredSorted.isEmpty()) {
                    (adapter as LineAdapter).showStops(stops.sortedBy { it.name })
                } else {
                    (adapter as LineAdapter).showStops(filteredSorted)
                }
            }
            .addOnFailureListener {
                if (adapter is PlaceholderAdapter) {
                    (adapter as PlaceholderAdapter).stopAnimation()
                }
                adapter = LineAdapter(this, this)
                binding.lineRecyclerView.adapter = adapter
                (adapter as LineAdapter).showStops(stops.sortedBy { it.name })
            }
//        }
    }

    override fun showError() {
        if (binding.lineSwipeRefresh.isRefreshing) {
            binding.lineSwipeRefresh.isRefreshing = false
        }
        if (adapter is PlaceholderAdapter) {
            (adapter as PlaceholderAdapter).stopAnimation()
            (adapter as PlaceholderAdapter).clear()
        }
        adapter = LineAdapter(this, this)
        binding.lineRecyclerView.adapter = adapter
        if (adapter is LineAdapter) {
            (adapter as LineAdapter).showError()
        }
    }

    override fun onLineClicked(line: TubeLine) {
        throw NotImplementedError("Not implemented")
    }

    override fun onStationClicked(line: TubeLine, station: TubeStop) {
        StationActivity.start(this, line, station)
    }
}