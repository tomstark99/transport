package com.android.transport2.arch

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences

object PreferenceModule {

    val allStopsLastSaved by lazy {
        rxSharedPrefs.getString("all_stops_last_saved")
    }

    val allStops by lazy {
        rxSharedPrefs.getString("all_stops_json_string")
    }

    private val sharedPrefs by lazy {
        PreferenceManager.getDefaultSharedPreferences(AppModule.application)
    }

    private val rxSharedPrefs by lazy {
        RxSharedPreferences.create(sharedPrefs)
    }
}