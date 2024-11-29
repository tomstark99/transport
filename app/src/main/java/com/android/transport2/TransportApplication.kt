package com.android.transport2

import android.app.Application
import com.android.transport2.arch.AppModule
import com.google.android.material.color.DynamicColors
import net.danlew.android.joda.JodaTimeAndroid

class TransportApplication : Application() {
    override fun onCreate() {
        AppModule.application = this
        DynamicColors.applyToActivitiesIfAvailable(this)
//        JodaTimeAndroid.init(this)
        super.onCreate()
    }
}