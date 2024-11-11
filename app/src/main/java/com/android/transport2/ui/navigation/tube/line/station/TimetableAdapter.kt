package com.android.transport2.ui.navigation.tube.line.station

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime
import com.android.transport2.databinding.ElementConnectionBinding
import com.android.transport2.databinding.ElementTimetableTrainBinding
import org.joda.time.Period
import org.joda.time.PeriodType
import org.joda.time.Seconds
import org.joda.time.format.PeriodFormatterBuilder

class TimetableAdapter(private val context: Activity) : RecyclerView.Adapter<TimetableAdapter.TimetableViewHolder>() {

    private var times = mutableListOf<TubeTime>()

    sealed class Status{
        object Normal: Status()
        object Loading: Status()
        object Error: Status()
    }

    private var status: Status = Status.Loading

    inner class TimetableViewHolder(val binding: ElementTimetableTrainBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
        return TimetableViewHolder(ElementTimetableTrainBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
        when(status) {
            is Status.Normal -> {
                with(holder) {
                    with(times[position]) {
                        binding.directionText.text = towards.ifEmpty { destination?.name?.replace(" Station", "", true) ?: "" }
//                        binding.platformText.text = if (platform.length == 1) """Platform $platform""" else platform
                        (binding.arrivalTimeText.background as GradientDrawable).setColor(context.getColor(line!!.color))
                        binding.arrivalTimeText.text = timeToStation(timeToStation)
                        binding.currentLocationText.text = if (currentLocation.isNotEmpty()) """Currently ${currentLocation.first().lowercase()}${currentLocation.removeRange(0,1)}""" else ""
                        binding.trainIdText.text = """ID-$vehicleId"""
                    }
                }
            }
            is Status.Loading -> {

            }
            is Status.Error -> {

            }
        }

    }

    private fun timeToStation(time: Int): String {
        return when {
            time > 60 -> {
                val seconds = Seconds.seconds(time)
                val period = Period(seconds)
                val formatter = PeriodFormatterBuilder()
                    .appendMinutes()
                    .appendSuffix(" min", " mins")
                    .toFormatter()
                formatter.print(period.normalizedStandard())
            }
            else -> "Due"
        }
    }

    fun showTimetableList(times: List<TubeTime>){
        this.times = times
            .asSequence()
            .distinctBy { it.timeToStation }
            .sortedBy { it.timeToStation }
            .distinctBy { it.vehicleId }
            .take(5).toMutableList()
        status = Status.Normal
        notifyDataSetChanged()
    }

    fun showError(){
        Status.Error
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return times.size
    }
}