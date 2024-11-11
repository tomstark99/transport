package com.android.transport2.ui.navigation.tube.line

import com.android.transport2.arch.android.BaseMvp
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop

interface LineMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun showStops(stops: List<TubeStop>)
        fun setClickables()
        fun showError()
    }
    interface Presenter : BaseMvp.Presenter {
        fun onCreate(line: TubeLine)
    }
}