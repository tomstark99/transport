package com.android.transport2.arch.android

import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BasePresenter<T: BaseMvp.View> constructor(var view: T?) : BaseMvp.Presenter {
    protected val subscription = CompositeDisposable()

    override fun onDestroy() {
        subscription.clear()
        view = null
    }
}