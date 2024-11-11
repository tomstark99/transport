package com.android.transport2.arch.managers

import com.android.transport2.arch.models.Train
import io.reactivex.Single

interface TrainManager {
    fun getTrainService(station: TrainStation, dest: TrainStation, operator: Operator, keySize: Int): Single<List<Train>>

    enum class TrainStation(val id: String) {
        BRISTOL_TEMPLE_MEADS("BRI"),
        READING_STATION("RDG"),
        CHELTENHAM_SPA("CNM");

        companion object{
            fun stringToTrainStation(line: String): TrainStation?{
                return values().firstOrNull { it.id == line }
            }
        }
    }

    enum class Operator(val id: String) {
        GREAT_WESTERN_RAILWAY("GW"),
        CROSS_COUNTRY("XC");

        companion object{
            fun stringToOperator(line: String): Operator?{
                return values().firstOrNull { it.id == line }
            }
        }
    }
}