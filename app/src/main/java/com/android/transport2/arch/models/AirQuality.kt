package com.android.transport2.arch.models

import java.io.Serializable

data class AirQuality(
    val current: Forecast?,
    val future: Forecast?
): Serializable {
    companion object{
        fun fromTemplate(template: AirQualityTemplate):AirQuality?{
            val current = template.currentForecast.find { it.forecastType == "Current" }
            val future = template.currentForecast.find { it.forecastType == "Future" }
            return AirQuality(current, future)
        }
    }
}
// disruptions currently has no use
data class AirQualityTemplate(
    val currentForecast: List<Forecast>
)

data class Forecast(
    val forecastType: String,
    val forecastBand: String,
    val forecastSummary: String,
    val nO2Band: String,
    val o3Band: String,
    val pM10Band: String,
    val pM25Band: String,
    val sO2Band: String
)
