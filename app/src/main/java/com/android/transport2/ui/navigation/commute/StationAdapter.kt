package com.android.transport2.ui.navigation.commute

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.managers.TubeManager.*
import com.android.transport2.arch.managers.TubeManager.TubeDirection.PLATFORM_UNKNOWN
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime
import com.android.transport2.arch.utils.Utils.getGradientDrawable
import com.android.transport2.databinding.ElementTimetableHomeBinding

class StationAdapter(private val station: TubeStop,
                     private val context: Activity
) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    private var timetable = mutableListOf<Map.Entry<TubeDirection, List<TubeTime>>>()
    private var initialRadius: Float = Float.MIN_VALUE

    sealed class Status{
        object Normal: Status()
        object Loading: Status()
        object Error: Status()
    }

    private var status: Status = Status.Loading

    inner class StationViewHolder(val binding: ElementTimetableHomeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        return StationViewHolder(ElementTimetableHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        when(status) {
            is Status.Normal -> {
                with(holder) {
                    with(timetable[position]) {
                        if (initialRadius == Float.MIN_VALUE) {
                            initialRadius = (binding.stationTimetableElement.background as GradientDrawable).cornerRadius
                        }
                        val backgroundDrawable = getGradientDrawable(context, initialRadius, station.lines.map { it.color })
                        binding.stationTimetableElement.background = backgroundDrawable
                        binding.timetableList.background = backgroundDrawable
//                        (binding.tubeStatusElement.background as GradientDrawable).setColor(context.getColor(line.color))
//                        binding.stopText.text = name
//                        val layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW)
//                        layoutManager.justifyContent = JustifyContent.FLEX_START
                        binding.directionText.text = if (key.id.length == 1) """Platform ${key.id}""" else key.id
                        binding.timetableList.layoutManager = LinearLayoutManager(context)
                        binding.timetableList.itemAnimator = DefaultItemAnimator()
                        binding.timetableList.adapter = TimetableAdapter(context).apply {
                            showTimetableList(value)
                        }
                    }
                }
            }
            is Status.Loading -> {

            }
            is Status.Error -> {

            }
        }

    }

    fun showTimetable(timetable: Map<TubeDirection, List<TubeTime>>){
//        val platformEntryTimes = timetable[TubeDirection.PLATFORM]
//        val platforms = platformEntryTimes.orEmpty().groupBy { it.platform }
//        val mappedPlatforms = platforms.mapKeys { TubeDirection.PLATFORM.apply { id = it.key }}
//        timetable.filterNot { it.key == TubeDirection.PLATFORM }
        var newTimetable = timetable
        if (timetable.isEmpty()) {
            val emptyTimetable = timetable.toMutableMap()
            emptyTimetable[PLATFORM_UNKNOWN] = listOf(TubeTime.emptyTubeTime())
            newTimetable = emptyTimetable
        }

        this.timetable = newTimetable
            .flatMap { it.value }
            .distinctBy { it.timeToStation  }
            .groupBy { it.direction }
            .mapKeys { TubeDirection.stringToTubeDirection(it.key) }
            .entries.toList().sortedBy { it.key.id }.toMutableList()

//        timetable.entries.forEach { entry ->
//            entry.value.forEach { tubeTime ->
//                listOfTimes.add(entry.key to tubeTime)
//            }
//        }
//        this.timetable = listOfTimes
//            .distinctBy { it.second.timeToStation }
//            .associate { it.first to listOf(it.second) }.entries.toMutableList()
//        this.timetable = timetable.entries.toMutableList()
//        this.timetable.addAll(mappedPlatforms.entries)
        status = Status.Normal
        notifyDataSetChanged()
    }

    fun showError(){
        Status.Error
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return timetable.size
    }
}