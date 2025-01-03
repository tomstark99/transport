package com.android.transport2.ui.navigation.train

import com.android.transport2.arch.android.BaseMvp
import com.android.transport2.arch.models.TubeStop

interface TrainMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun load()
        fun showNearbyStops(stops: List<TubeStop>)
        fun showError()
    }
    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
        fun onRefresh()
    }
}