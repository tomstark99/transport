package com.android.transport2.arch.managers

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.android.transport2.R
import com.android.transport2.arch.models.Tube
import com.android.transport2.arch.models.TubeStop
import com.android.transport2.arch.models.TubeTime
import io.reactivex.Single


interface TubeManager {
    fun getLineService(line: TubeLine): Single<Tube>
    fun getStopsService(line: TubeLine): Single<List<TubeStop>>
    fun getTimetableService(line: TubeLine, station: TubeStop): Single<Map<TubeDirection, List<TubeTime>>>

    enum class TubeDirection(var id: String) {
        NORTHBOUND("Northbound"),
        EASTBOUND("Eastbound"),
        SOUTHBOUND("Southbound"),
        WESTBOUND("Westbound"),
        PLATFORM_A("A"),
        PLATFORM_B("B"),
        PLATFORM_C("C"),
        PLATFORM_D("D"),
        PLATFORM_E("E"),
        PLATFORM_F("F"),
        PLATFORM_G("G"),
        PLATFORM_H("H"),
        PLATFORM_I("I"),
        PLATFORM_J("J"),
        PLATFORM_1("Platform 1"),
        PLATFORM_2("Platform 2"),
        PLATFORM_3("Platform 3"),
        PLATFORM_4("Platform 4"),
        PLATFORM_5("Platform 5"),
        PLATFORM_6("Platform 6"),
        PLATFORM_7("Platform 7"),
        PLATFORM_8("Platform 8"),
        PLATFORM_9("Platform 9"),
        PLATFORM_10("Platform 10"),
        PLATFORM_11("Platform 11"),
        PLATFORM_12("Platform 12"),
        PLATFORM_13("Platform 13"),
        PLATFORM_14("Platform 14"),
        PLATFORM_15("Platform 15"),
        PLATFORM_UNKNOWN("Platform Unknown");

        companion object {
            fun stringToTubeDirection(direction: String): TubeDirection {
                return values().first { it.id.equals(direction) }
            }
        }
    }

    enum class TubeLine(val id: String, @StringRes val commonName: Int, @ColorRes val color: Int) {
        BAKERLOO("bakerloo", R.string.line_bakerloo, R.color.colour_bakerloo),
        CENTRAL("central", R.string.line_central, R.color.colour_central),
        CIRCLE("circle", R.string.line_circle, R.color.colour_circle),
        DISTRICT("district", R.string.line_district, R.color.colour_district),
        HAMMERSMITH("hammersmith-city", R.string.line_hammersmith, R.color.colour_hammersmith),
        JUBILEE("jubilee", R.string.line_jubilee, R.color.colour_jubilee),
        METROPOLITAN("metropolitan", R.string.line_metropolitan, R.color.colour_metropolitan),
        NORTHERN("northern", R.string.line_northern, R.color.colour_northern),
        PICCADILLY("piccadilly", R.string.line_piccadilly, R.color.colour_piccadilly),
        VICTORIA("victoria", R.string.line_victoria, R.color.colour_victoria),
        WATERLOO("waterloo-city", R.string.line_waterloo, R.color.colour_waterloo),
        ELIZABETH("elizabeth", R.string.line_elizabeth, R.color.colour_elizabeth),
        DLR("dlr", R.string.line_dlr, R.color.colour_dlr),
        OVERGROUND("london-overground", R.string.line_overground, R.color.colour_overground);

        companion object{
            fun stringToTubeLine(line: String): TubeLine?{
                return values().firstOrNull { it.id == line }
            }
        }
    }
}