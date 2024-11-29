package com.android.transport2.ui.navigation.commute

import android.util.Log
import com.android.transport2.arch.DataModule
import com.android.transport2.arch.android.BasePresenter
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.TubeLine
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.schedulers.Schedulers

class CommutePresenter(trainView : CommuteMvp.View, private val tubeManager: TubeManager = DataModule.tubeManager) : BasePresenter<CommuteMvp.View>(trainView), CommuteMvp.Presenter {

    override fun onCreateForDefaultTimetable(lines: List<TubeLine>, stationId: String) {
//        getTimetableService(lines, stationId)
    }

    override fun onRefreshForDefaultTimetable(lines: List<TubeLine>, stationId: String) {
//        getTimetableService(lines, stationId)
    }

    // this needs to be so that it can take a list of lines AND a list of stations

    private fun getTimetableService(lines: List<TubeLine>, stationId: String){
        Flowable.fromIterable(lines).flatMap { line ->
            tubeManager.getTimetableServiceWithoutGrouping(line, stationId).toFlowable()
        }.toList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ status ->
                val flattenedTimes = status.flatten()
                val station = flattenedTimes.first().station
                val groupedTimetable = flattenedTimes.groupBy { TubeManager.TubeDirection.stringToTubeDirection(it.direction) }
                view?.showDefaultTimetable(station, groupedTimetable)
            }, { e ->
                view?.showError()
                Log.e("error", "something went wrong getting lines", e)
            })
    }

}