package com.android.transport2.arch

import com.android.transport2.arch.api.ApiModule
import com.android.transport2.arch.managers.TubeManagerImpl

object DataModule {

    val tubeManager by lazy {
        TubeManagerImpl(ApiModule.apiTFLService)
    }
}