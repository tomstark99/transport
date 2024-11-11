package com.android.transport2.arch.api

import com.android.transport2.arch.models.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TransportService {

    @GET("Line/{line}/Status?app_id=b6ad32e2&app_key=706a3106bc531ab6ba27366eed13d9af")
    fun getTube(@Path("line") line: String): Single<List<TubeTemplate>>

    @GET("Line/{line}/StopPoints?app_id=b6ad32e2&app_key=706a3106bc531ab6ba27366eed13d9af")
    fun getStops(@Path("line") line: String): Single<List<TubeStopTemplate>>

    @GET("Line/{lines}/Arrivals/{stationId}?tflOperatedNationalRailStationsOnly=true&app_id=b6ad32e2&app_key=706a3106bc531ab6ba27366eed13d9af")
    fun getTimetable(@Path("lines") line: String, @Path("stationId") stationId: String): Single<List<TubeTimeTemplate>>

    @GET("v3/uk/train/station/{st}/live.json?darwin=true&train_status=passenger&to_offset=PT04:00:00")
    fun getTrain(@Path("st") station: String, @Query("calling_at") calling: String, @Query("operator") operator: String, @Query("app_id") appID: String, @Query("app_key") key: String): Single<TrainTemplate>
    // @Query("destination") dest: String
}
