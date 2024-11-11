package com.android.transport2.ui.navigation.train

import com.android.transport2.arch.android.BasePresenter

class TrainPresenter(trainView : TrainMvp.View) : BasePresenter<TrainMvp.View>(trainView), TrainMvp.Presenter {

    override fun onCreate() {
        view?.load()
    }
}