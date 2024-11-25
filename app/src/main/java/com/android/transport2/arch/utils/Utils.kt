package com.android.transport2.arch.utils

import com.android.transport2.arch.PreferenceModule
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants


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
}