package com.android.transport2.ui.navigation.tube

import android.util.Log
import com.android.transport2.arch.DataModule
import com.android.transport2.arch.android.BasePresenter
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.*
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers

class TubePresenter(tubeView : TubeMvp.View, private val tubeManager: TubeManager = DataModule.tubeManager) : BasePresenter<TubeMvp.View>(tubeView), TubeMvp.Presenter {

    override fun onCreate() {
        getLineService(TubeLine.validTubeLines())
    }

    override fun onRefresh() {
        getLineService(TubeLine.validTubeLines())
    }

    private fun getLineService(lines: List<TubeLine>){
        Flowable.fromIterable(lines).flatMap { line ->
            tubeManager.getLineService(line).toFlowable()
        }.toList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ status ->
                view?.showTube(status)
            }, { e ->
                view?.showError()
                Log.e("error", "something went wrong getting lines", e) }).addTo(subscription)
    }
}