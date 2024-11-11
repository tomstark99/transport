package com.android.transport2.arch.managers

import com.android.transport2.arch.api.TransportService
import com.android.transport2.arch.models.Tube
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime
import io.reactivex.Single

class TubeManagerImpl (private val service: TransportService) : TubeManager {

    override fun getLineService(line: TubeManager.TubeLine): Single<Tube>{
        return service.getTube(line.id).map { response ->
            Tube.fromTemplate(response.first())
        }
    }

    override fun getStopsService(line: TubeManager.TubeLine): Single<List<TubeStop>> {
        return service.getStops(line.id).map { response ->
            response.map { TubeStop.fromTemplate(line, it)!! }
        }
    }

    override fun getTimetableService(
        line: TubeManager.TubeLine,
        station: TubeStop
    ): Single<Map<TubeManager.TubeDirection, List<TubeTime>>> {
        return service.getTimetable(
//            listOf(line.id,*station.lines.map { it!!.id }.toTypedArray()).joinToString(","),
            line.id,
            station.id
        ).map { response ->
            response.map { TubeTime.fromTemplate(it)!! }.groupBy {

                TubeManager.TubeDirection.stringToTubeDirection(it.direction)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun List<TubeTime>.guessPlatform(): List<TubeTime> {
        if(this.any { it.destination?.id == TubeManager.TubeDirection.PLATFORM_UNKNOWN.id }) {
            val destinations = this.groupBy { it.destination?.id }
            // TODO: make this flatten into Map<String?, TubeTime.platform> so that each direction string has an associated platform
//            val platforms = destinations.flatMap { it? }
            return this
        } else { return this }
    }
}