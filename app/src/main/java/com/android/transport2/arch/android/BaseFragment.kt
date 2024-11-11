package com.android.transport2.arch.android

import androidx.fragment.app.Fragment

abstract class BaseFragment<T : BaseMvp.Presenter> : Fragment(), BaseMvp.View {
    protected lateinit var presenter: T

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}