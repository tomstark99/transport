package com.android.transport2.ui.navigation.tube.line.station

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.transport2.arch.managers.TubeManager.*
import com.android.transport2.arch.models.Tube
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime
import com.android.transport2.databinding.ElementTimetableBinding

class StationAdapter(private val line: TubeLine,
                     private val station: TubeStop,
                     private val context: Activity
) : RecyclerView.Adapter<StationAdapter.StationViewHolder>() {

    private var timetable = mutableListOf<Map.Entry<TubeDirection, List<TubeTime>>>()

    sealed class Status{
        object Normal: Status()
        object Loading: Status()
        object Error: Status()
    }

    private var status: Status = Status.Loading

    inner class StationViewHolder(val binding: ElementTimetableBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        return StationViewHolder(ElementTimetableBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        when(status) {
            is Status.Normal -> {
                with(holder) {
                    with(timetable[position]) {
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
        this.timetable = timetable
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
        notifyItemRangeChanged(0, this.timetable.size-1)
    }

    fun showError(){
        Status.Error
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return timetable.size
    }
}