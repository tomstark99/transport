package com.android.transport2.ui.navigation.commute

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.models.TubeTime
import com.android.transport2.databinding.ElementTimetableTrainHomeBinding
import org.joda.time.Period
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

    inner class TimetableViewHolder(val binding: ElementTimetableTrainHomeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableViewHolder {
        return TimetableViewHolder(ElementTimetableTrainHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TimetableViewHolder, position: Int) {
        when(status) {
            is Status.Normal -> {
                with(holder) {
                    with(times[position]) {
                        binding.directionText.text = towards.ifEmpty { destination?.name?.replace(" Station", "", true) ?: "" }
//                        binding.platformText.text = if (platform.length == 1) """Platform $platform""" else platform
                        (binding.arrivalTimeText.background as GradientDrawable).setColor(context.getColor(line.colorSolid))
                        binding.arrivalTimeText.text = timeToStation(timeToStation)
                        binding.currentLocationText.text = if (currentLocation.isNotEmpty()) """Currently ${currentLocation.first().lowercase()}${currentLocation.removeRange(0,1)}""" else ""
                        binding.lineText.text = context.getString(line.commonName)
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
            time == -1 -> "Check station board"
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