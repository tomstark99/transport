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
    fun getLinesService(lines: List<TubeLine>): Single<List<Tube>>
    fun getStopsService(line: TubeLine): Single<List<TubeStop>>
    fun getTimetableService(line: TubeLine, station: TubeStop): Single<Map<TubeDirection, List<TubeTime>>>
    fun getTimetableServiceWithoutGrouping(line: TubeLine, stationId: String): Single<List<TubeTime>>

    enum class TubeDirection(var id: String) {
        INNER_RAIL("Inner Rail"),
        OUTER_RAIL("Outer Rail"),
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
        PLATFORM_1A("Platform 1A"),
        PLATFORM_2("Platform 2"),
        PLATFORM_2A("Platform 2A"),
        PLATFORM_3("Platform 3"),
        PLATFORM_3A("Platform 3A"),
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
        PLATFORM_UNKNOWN("Platform Unknown"),
        PLATFORM_ERROR("No Upcoming Departures");

        companion object {
            fun stringToTubeDirection(direction: String): TubeDirection {
                return values().first { it.id == direction }
            }
        }
    }

    enum class TubeLine(val id: String, @StringRes val commonName: Int, @ColorRes val color: Int, @ColorRes val colorSolid: Int) {
        BAKERLOO("bakerloo", R.string.line_bakerloo, R.color.colour_bakerloo, R.color.colour_bakerloo_solid),
        CENTRAL("central", R.string.line_central, R.color.colour_central, R.color.colour_central_solid),
        CIRCLE("circle", R.string.line_circle, R.color.colour_circle, R.color.colour_circle_solid),
        DISTRICT("district", R.string.line_district, R.color.colour_district, R.color.colour_district_solid),
        HAMMERSMITH("hammersmith-city", R.string.line_hammersmith, R.color.colour_hammersmith, R.color.colour_hammersmith_solid),
        JUBILEE("jubilee", R.string.line_jubilee, R.color.colour_jubilee, R.color.colour_jubilee_solid),
        METROPOLITAN("metropolitan", R.string.line_metropolitan, R.color.colour_metropolitan, R.color.colour_metropolitan_solid),
        NORTHERN("northern", R.string.line_northern, R.color.colour_northern, R.color.colour_northern_solid),
        PICCADILLY("piccadilly", R.string.line_piccadilly, R.color.colour_piccadilly, R.color.colour_piccadilly_solid),
        VICTORIA("victoria", R.string.line_victoria, R.color.colour_victoria, R.color.colour_victoria_solid),
        WATERLOO("waterloo-city", R.string.line_waterloo, R.color.colour_waterloo, R.color.colour_waterloo_solid),
        ELIZABETH("elizabeth", R.string.line_elizabeth, R.color.colour_elizabeth, R.color.colour_elizabeth_solid),
        DLR("dlr", R.string.line_dlr, R.color.colour_dlr, R.color.colour_dlr_solid),
//        LIBERTY("liberty", R.string.line_liberty, R.color.colour_liberty, R.color.colour_liberty_solid),
//        LIONESS("lioness", R.string.line_lioness, R.color.colour_lioness, R.color.colour_lioness_solid),
//        MILDMAY("mildmay", R.string.line_mildmay, R.color.colour_mildmay, R.color.colour_mildmay_solid),
//        SUFFRAGETTE("suffragette", R.string.line_suffragette, R.color.colour_suffragette, R.color.colour_suffragette_solid),
//        WEAVER("weaver", R.string.line_weaver, R.color.colour_weaver, R.color.colour_weaver_solid),
//        WINDRUSH("windrush", R.string.line_windrush, R.color.colour_windrush, R.color.colour_windrush_solid),
        LIBERTY("liberty", R.string.line_liberty, R.color.colour_overground, R.color.colour_overground_solid),
        LIONESS("lioness", R.string.line_lioness, R.color.colour_overground, R.color.colour_overground_solid),
        MILDMAY("mildmay", R.string.line_mildmay, R.color.colour_overground, R.color.colour_overground_solid),
        SUFFRAGETTE("suffragette", R.string.line_suffragette, R.color.colour_overground, R.color.colour_overground_solid),
        WEAVER("weaver", R.string.line_weaver, R.color.colour_overground, R.color.colour_overground_solid),
        WINDRUSH("windrush", R.string.line_windrush, R.color.colour_overground, R.color.colour_overground_solid),
        OVERGROUND("london-overground", R.string.line_overground, R.color.colour_overground, R.color.colour_overground_solid),
        UNKNOWN("unknown", R.string.line_unknown, R.color.md_theme_onPrimaryContainer, R.color.md_theme_onPrimaryContainer); // this colour is just a placeholder

        companion object{
            fun stringToTubeLine(line: String): TubeLine {
                return if (values().any { it.id == line }) {
                    values().first { it.id == line }
                } else {
                    UNKNOWN
                }
            }

            fun validTubeLines(): List<TubeLine> {
                return TubeLine.values().filterNot { listOf(OVERGROUND, UNKNOWN).contains(it) }
            }
        }
    }
}