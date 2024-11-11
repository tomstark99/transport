package com.android.transport2.ui.navigation.tube.line

import android.util.Log
import com.android.transport2.arch.DataModule
import com.android.transport2.arch.android.BasePresenter
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.TubeLine
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LinePresenter(lineView : LineMvp.View, private val tubeManager: TubeManager = DataModule.tubeManager) : BasePresenter<LineMvp.View>(lineView), LineMvp.Presenter {

    override fun onCreate(line: TubeLine) {
        view?.setClickables()
        getStopsService(line)
    }

    private fun getStopsService(line: TubeLine){
        tubeManager.getStopsService(line)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ stops ->
                view?.showStops(stops)
            }, { e ->
                view?.showError()
                Log.e("error", "something went wrong getting stops for $line", e) })
    }
}