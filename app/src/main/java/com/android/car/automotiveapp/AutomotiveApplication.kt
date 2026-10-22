package com.android.car.automotiveapp

import android.app.Application
import android.content.pm.ApplicationInfo
import com.android.car.automotiveapp.core.common.AppLog
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AutomotiveApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val isDebug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        AppLog.init(isDebug = isDebug)
        AppLog.i("AutomotiveApplication", "Automotive application initialized")
    }
}
