package com.android.transport2.arch.api

import com.google.gson.Gson
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiModule {
    private const val API_URL_TFL = "https://api.tfl.gov.uk/"
    private const val API_URL_TRANSPORT_API = "https://transportapi.com/"

    val apiTFLService: TransportService by lazy {
        retrofitTFL.create(TransportService::class.java)
    }

    val apiTAPIService: TransportService by lazy {
        retrofitTransportApi.create(TransportService::class.java)
    }

    private val retrofitTFL by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL_TFL)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    private val retrofitTransportApi by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL_TRANSPORT_API)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private val okhttp by lazy {
        OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(
                HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    val gson by lazy {
        Gson()
    }
}