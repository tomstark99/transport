package com.android.transport2.arch.utils

import android.app.Activity
import android.graphics.drawable.GradientDrawable
import com.android.transport2.arch.PreferenceModule
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime


object Utils {

    fun getContextTimeString(): String {
        val now = DateTime()
        return if (now.hourOfDay > 17) "Evening"
        else if (now.hourOfDay in 0..11) "Morning"
        else "Afternoon"
    }

    fun saveOrUpdateAllTubeStops(newStops: List<TubeStop>) {
        PreferenceModule.allStops.set(Gson().toJson(newStops))
        PreferenceModule.allStopsLastSaved.set(DateTime.now().toString())
    }

    fun timeToSave(): Boolean {
        return try {
            DateTime.parse(PreferenceModule.allStopsLastSaved.get()).plusWeeks(2).isBeforeNow
        } catch (e: Exception) {
            true
        }
    }

    fun getCachedAllTubeStops(): List<TubeStop> {
        val type = object : TypeToken<List<TubeStop>>() {}.type
        return Gson().fromJson(PreferenceModule.allStops.get(), type)
    }

    fun getCachedStopsForLine(line: TubeLine): List<TubeStop> {
        return getCachedAllTubeStops().filter { stop ->
            stop.origin == line
        }
    }

    fun commuteStation(): String {
        val now = DateTime()
        return if (now.hourOfDay >= 12) TubeStop.LIVERPOOL_STREET
        else TubeStop.LATIMER_ROAD
    }


    fun List<TubeTime>.guessPlatform(): List<TubeTime> {
        if(this.any { it.destination?.id == TubeManager.TubeDirection.PLATFORM_UNKNOWN.id }) {
            val destinations = this.groupBy { it.destination?.id }
            // TODO: make this flatten into Map<String?, TubeTime.platform> so that each direction string has an associated platform
//            val platforms = destinations.flatMap { it? }
            return this
        } else { return this }
    }

    fun List<Map<TubeManager.TubeDirection, List<TubeTime>>>.mergeTimeTables(): Map<TubeManager.TubeDirection, List<TubeTime>> {
        return this.flatMap { map ->
            map.entries.map { (direction, times) ->
                direction to times
            }
        }.groupBy({ it.first }) { it.second }.mapValues { (_, timeList) ->
            timeList.flatten()
        }
    }

    fun getGradientDrawable(
        context: Activity,
        initialRadius: Float,
        colours: List<Int>
    ): GradientDrawable {
        val drawable: GradientDrawable
        if (colours.size > 1) {
            drawable = GradientDrawable(GradientDrawable.Orientation.TL_BR, colours.map { context.getColor(it) }.toIntArray())
        } else {
            drawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, null)
            drawable.setColor(context.getColor(colours.first()))
        }
        drawable.cornerRadius = initialRadius
        return drawable
    }
}