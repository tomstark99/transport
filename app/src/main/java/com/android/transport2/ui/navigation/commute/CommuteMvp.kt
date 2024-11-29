package com.android.transport2.ui.navigation.commute

import com.android.transport2.arch.android.BaseMvp
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.TubeDirection
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime

interface CommuteMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun load()
        fun showDefaultTimetable(station: TubeStop, times: Map<TubeDirection, List<TubeTime>>)
        fun showError()
    }
    interface Presenter : BaseMvp.Presenter {
        fun onCreateForDefaultTimetable(lines: List<TubeManager.TubeLine>, stationId: String)
        fun onRefreshForDefaultTimetable(lines: List<TubeManager.TubeLine>, stationId: String)
    }
}