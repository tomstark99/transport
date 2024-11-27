package com.android.transport2.arch.models

import android.location.Location
import com.android.transport2.arch.managers.TubeManager.*
import java.io.Serializable

data class TubeStop(
    val id: String,
    val name: String,
    val lines: List<TubeLine>,
    val additionalProperties: Map<String, String>?,
    val lat: Double?,
    val lon: Double?,
    val origin: TubeLine
): Serializable {
    companion object{
        fun fromTemplate(origin: TubeLine, template: TubeStopTemplate) : TubeStop {
            // Stop filtering out origin line to allow home screen to show all services from that station
//            val lines = template.lines.filter {
//                    line -> line.id in TubeLine.values().filterNot { it == origin }.map { it.id }
//            }.map { TubeLine.stringToTubeLine(it.id) }
            val lines = template.lines
                .filter { line -> line.id in TubeLine.values().map { it.id } }
                .mapNotNull { TubeLine.stringToTubeLine(it.id) }
            val additionalProperties = template.additionalProperties.associate { it.key to it.value }
            return TubeStop(
                template.id,
                template.commonName
                    .replace("Underground ", "", true)
                    .replace("DLR ", "", true),
                lines,
                additionalProperties,
                template.lat.toDouble(),
                template.lon.toDouble(),
                origin
            )
        }

        fun emptyTubeStop() : TubeStop {
            return TubeStop(
                id = "unknown",
                name = "Unknown",
                lines = emptyList(),
                additionalProperties = emptyMap(),
                lat = "0".toDouble(),
                lon = "0".toDouble(),
                origin = TubeLine.UNKNOWN
            )
        }
    }
}

data class TubeStopTemplate(
    val modes: List<String>,
    val lines: List<Line>,
    val id: String,
    val commonName: String,
    val additionalProperties: List<AdditionalProperties>,
    val lat: String,
    val lon: String

)

data class Wrapper<T>(
    val stops: List<T>
)

data class Line(
    val id: String,
    val name: String,
    val type: String,
)

data class AdditionalProperties(
    val key: String,
    val value: String
)