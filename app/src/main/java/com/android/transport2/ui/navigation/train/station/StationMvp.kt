package com.android.transport2.ui.navigation.train.station

import com.android.transport2.arch.android.BaseMvp
import com.android.transport2.arch.managers.TubeManager.TubeDirection
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime

interface StationMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun showTimetable(times: Map<TubeDirection, List<TubeTime>>)
        fun setClickables()
        fun setRefresh()
        fun showError()
    }
    interface Presenter : BaseMvp.Presenter {
        fun onCreate(station: TubeStop)
        fun onRefresh(station: TubeStop)
    }
}