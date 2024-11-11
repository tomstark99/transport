package com.android.transport2.arch.android

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BaseActivity<T: BaseMvp.Presenter>: AppCompatActivity() {
    lateinit var presenter: T
    protected val subscription = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
        subscription.clear()
    }
}