package com.android.transport2.ui.main

import com.android.transport2.arch.android.BasePresenter

class MainPresenter(mainView: MainMvp.View) : BasePresenter<MainMvp.View>(mainView), MainMvp.Presenter {

    override fun onCreate() {
        view?.addFragments()
        view?.loadTabs()
    }

    override fun onReload() {
        view?.reAddFragments()
        view?.loadTabs()
    }
}