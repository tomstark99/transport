package com.android.transport2.ui.main

import com.android.transport2.arch.android.BaseMvp

interface MainMvp : BaseMvp {
    interface View : BaseMvp.View {
        fun loadTabs()
        fun addFragments()
        fun reAddFragments()
    }

    interface Presenter : BaseMvp.Presenter {
        fun onCreate()
        fun onReload()
    }
}