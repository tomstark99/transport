package com.android.transport2.ui.navigation.train

import android.util.Log
import com.android.transport2.arch.DataModule
import com.android.transport2.arch.android.BasePresenter
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.utils.Utils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers

class TrainPresenter(trainView : TrainMvp.View, private val tubeManager: TubeManager = DataModule.tubeManager) : BasePresenter<TrainMvp.View>(trainView), TrainMvp.Presenter {

    override fun onCreate() {
//        view?.load()
        getLineService(TubeLine.validTubeLines())
    }

    override fun onRefresh() {
        getLineService(TubeLine.validTubeLines())
    }

    private fun getLineService(lines: List<TubeLine>){
        if (Utils.timeToSave()) {
            getStopsFromApi(lines)
        } else {
            getStopsFromCache()
        }
    }

    private fun getStopsFromApi(lines: List<TubeLine>) {
        Flowable.fromIterable(lines).flatMap { line ->
            tubeManager.getStopsService(line).toFlowable()
        }.toList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ status ->
                view?.showNearbyStops(status.flatten())
                Utils.saveOrUpdateAllTubeStops(status.flatten())
            }, { e ->
                view?.showError()
                Log.e("error", "something went wrong getting lines", e) }).addTo(subscription)
    }

    private fun getStopsFromCache() {
        view?.showNearbyStops(Utils.getCachedAllTubeStops())
    }


}