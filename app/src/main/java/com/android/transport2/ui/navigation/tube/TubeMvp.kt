package com.android.transport2.ui.navigation.tube

import com.android.transport2.arch.android.BaseMvp
import com.android.transport2.arch.models.Tube
import com.android.transport2.arch.models.TubeStop

interface TubeMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun showTube(lines: List<Tube>)
        fun showError()
    }
    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
        fun onRefresh()
    }
}