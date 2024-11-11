package com.android.transport2.arch.android

import com.android.transport2.arch.managers.TubeManager.TubeLine
import com.android.transport2.arch.models.TubeStop

interface Load {
    fun onLineClicked(line: TubeLine)
    fun onStationClicked(line: TubeLine, station: TubeStop)
}