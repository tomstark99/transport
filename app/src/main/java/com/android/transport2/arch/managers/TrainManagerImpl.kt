package com.android.transport2.arch.managers

import com.android.transport2.BuildConfig
import com.android.transport2.arch.api.TransportService
import com.android.transport2.arch.models.Train
import io.reactivex.Single
import kotlin.random.Random

class TrainManagerImpl (private val service: TransportService) : TrainManager {

    override fun getTrainService(station: TrainManager.TrainStation, dest: TrainManager.TrainStation, operator: TrainManager.Operator, keySize: Int): Single<List<Train>> {
        val random = Random.nextInt(BuildConfig.TRANSPORT_APP_IDS.size)
        return service.getTrain(station.id, dest.id, operator.id, BuildConfig.TRANSPORT_APP_IDS[random], BuildConfig.TRANSPORT_KEYS[random]).map { response ->
            val trains = ArrayList<Train>()
            for((i, train) in response.departures.all.withIndex()){
                if(i < 3 || train.status == "CANCELLED"){
                    trains.add(Train.fromTemplate(response, i)!!)
                }
            }

            trains
        }
    }
}