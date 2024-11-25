package com.android.transport2.arch.models

import com.android.transport2.arch.managers.TubeManager
import com.android.transport2.arch.managers.TubeManager.TubeLine.UNKNOWN
import com.android.transport2.arch.models.TubeStop.Companion.emptyTubeStop
import org.joda.time.DateTime
import java.io.Serializable

data class TubeTime(
    val line: TubeManager.TubeLine?,
    val station: TubeStop?,
    val direction: String,
    val platform: String,
    val towards: String,
    val timeToStation: Int,
    val expectedArrival: String,
    val destination: TubeStop?,
    val currentLocation: String,
    val vehicleId: String
): Serializable {
    companion object{
        fun fromTemplate(template: TubeTimeTemplate) : TubeTime {
            val line = TubeManager.TubeLine.stringToTubeLine(template.lineId)
            val platformName = if (template.platformName.contains("-")) {
                template.platformName
            } else {
                val words = template.platformName.split(" ")
                """${words[0]} - ${words.drop(1).joinToString(" ")}"""
            }
            val direction = platformName.split("-").first().dropLastWhile { it.isWhitespace() }
            val platform = platformName.split("-").last().dropWhile { it.isWhitespace() }
            val station = TubeStop(
                template.naptanId,
                template.stationName
                    .replace("Underground ", "", true)
                    .replace("DLR ", "", true)
                    .replace("Rail ", "", true),
                null,
                null,
                null,
                null,
                line!!) // hopefully this doesn't blow up
            val destination = if (template.destinationNaptanId.isNullOrEmpty()) null else TubeStop(
                template.destinationNaptanId,
                template.destinationName!!
                    .replace("Underground ", "", true)
                    .replace("DLR ", "", true)
                    .replace("Rail ", "", true),
                null,
                null,
                null,
                null,
                line)
            return TubeTime(
                line,
                station,
                if (direction == "Platform") """${direction} ${platform}""" else if (direction.contains(Regex("^(Inner|Outer) Rail$"))) platform else direction,
                platform,
                template.towards,
                template.timeToStation,
                template.expectedArrival,
                destination,
                template.currentLocation,
                template.vehicleId
            )
        }

        fun emptyTubeTime(): TubeTime {
            return TubeTime(
                line = UNKNOWN,
                station = emptyTubeStop(),
                direction = "No Upcoming Departures",
                platform = "Unknown",
                towards = "",
                timeToStation = -1,
                expectedArrival = DateTime.now().toString(),
                destination = emptyTubeStop(),
                currentLocation = "",
                vehicleId = "Unknown"
            )
        }
    }
}

data class TubeTimeTemplate(
    val vehicleId: String,
    val naptanId: String,
    val stationName: String,
    val lineId: String,
    val platformName: String,
    val timeToStation: Int,
    val destinationNaptanId: String?,
    val destinationName: String?,
    val currentLocation: String,
    val towards: String,
    val expectedArrival: String
)