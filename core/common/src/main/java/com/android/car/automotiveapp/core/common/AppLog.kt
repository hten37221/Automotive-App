package com.android.car.automotiveapp.core.common

import timber.log.Timber

/**
 * Structured logging wrapper across Automotive modules.
 */
object AppLog {
    fun init(isDebug: Boolean) {
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        }
    }

    fun d(tag: String = "AutomotiveApp", message: String) {
        Timber.tag(tag).d(message)
    }

    fun i(tag: String = "AutomotiveApp", message: String) {
        Timber.tag(tag).i(message)
    }

    fun e(tag: String = "AutomotiveApp", message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Timber.tag(tag).e(throwable, message)
        } else {
            Timber.tag(tag).e(message)
        }
    }
}
