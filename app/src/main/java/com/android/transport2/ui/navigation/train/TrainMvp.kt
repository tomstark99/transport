package com.android.transport2.ui.navigation.train

import com.android.transport2.arch.android.BaseMvp

interface TrainMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun load()
    }
    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
    }
}