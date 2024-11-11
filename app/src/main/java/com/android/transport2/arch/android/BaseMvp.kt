package com.android.transport2.arch.android

interface BaseMvp {
    interface View {
        fun finish()
    }
    interface Presenter {
        fun onDestroy()
    }
}