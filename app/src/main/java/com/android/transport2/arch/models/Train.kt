package com.android.transport2.arch.models

import java.io.Serializable

data class Train(
    val mode: String,
    val service: String,
    val platform: String,
    val stationName: String,
    val destinationName: String,
    val operator: String,
    val scheduledDeparture: String,
    val estimatedDeparture: String,
    val status: String,
    val minsToDepartures: String
):Serializable{
    companion object{
        fun fromTemplate(template: TrainTemplate, id: Int):Train?{
            val departures = template.departures.all[id]
            return Train(departures.mode,
                         departures.service,
                         departures.platform,
                         template.station_name,
                         departures.destination_name,
                         departures.operator,
                         departures.aimed_departure_time,
                         departures.expected_departure_time.orEmpty(),
                         departures.status,
                         departures.best_departure_estimate_mins.orEmpty())
        }
    }
}

data class TrainTemplate(
    val time_of_day: String,
    val station_name: String,
    val station_code: String,
    val departures: All
)

data class All(
    val all: List<Departures>
)
// add expected time
// add station name and code to train object
data class Departures(
    val mode: String,
    val service: String,
    val platform: String,
    val destination_name: String,
    val operator: String,
    val status: String,
    val aimed_departure_time: String,
    val expected_departure_time: String?,
    val best_departure_estimate_mins: String?
)
