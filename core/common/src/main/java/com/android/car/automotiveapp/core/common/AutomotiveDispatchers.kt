package com.android.car.automotiveapp.core.common

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: AutomotiveDispatchers)

enum class AutomotiveDispatchers {
    Default,
    IO,
    Main
}
