package com.android.transport2.ui.navigation.tube.line.station

import android.util.Log
import com.android.transport2.arch.DataModule
import com.android.transport2.arch.android.BasePresenter
import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class StationPresenter(stationView : StationMvp.View, private val tubeManager: TubeManager = DataModule.tubeManager) : BasePresenter<StationMvp.View>(stationView), StationMvp.Presenter {

    override fun onCreate(line: TubeLine, station: TubeStop) {
        view?.setClickables()
        view?.setRefresh()
        getTimetableService(line, station)
    }

    override fun onRefresh(line: TubeLine, station: TubeStop) {
        getTimetableService(line, station)
    }


    private fun getTimetableService(line: TubeLine, station: TubeStop){
        tubeManager.getTimetableService(line, station)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ timetable ->
                view?.showTimetable(timetable)
            }, { e ->
                view?.showError()
                Log.e("error", "something went wrong getting stops for $line", e) })
    }
}