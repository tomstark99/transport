package com.android.transport2.ui.navigation.train.station

import android.util.Log
import com.android.transport2.arch.DataModule
import com.android.transport2.arch.android.BasePresenter
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StationPresenter(stationView : StationMvp.View, private val tubeManager: TubeManager = DataModule.tubeManager) : BasePresenter<StationMvp.View>(stationView), StationMvp.Presenter {

    override fun onCreate(station: TubeStop) {
        view?.setClickables()
        view?.setRefresh()
        getTimetableService(station)
    }

    override fun onRefresh(station: TubeStop) {
        getTimetableService(station)
    }


    private fun getTimetableService(station: TubeStop){
//        tubeManager.getTimetableService(line, station)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ timetable ->
//                view?.showTimetable(timetable)
//            }, { e ->
//                view?.showError()
//                Log.e("error", "something went wrong getting stops for $line", e) })


        Flowable.fromIterable(station.lines).flatMap { line ->
            tubeManager.getTimetableServiceWithoutGrouping(line, station.id).toFlowable()
        }.toList().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ status ->
                val flattenedTimes = status.flatten()
                val groupedTimetable = flattenedTimes.groupBy { TubeManager.TubeDirection.stringToTubeDirection(it.direction) }
                view?.showTimetable(groupedTimetable)
            }, { e ->
                view?.showError()
                Log.e("error", "something went wrong getting lines", e) })
    }
}