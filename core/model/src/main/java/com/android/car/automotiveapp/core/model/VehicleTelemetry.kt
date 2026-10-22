package com.android.car.automotiveapp.core.model

/**
 * Dữ liệu vận hành và trạng thái kỹ thuật số mô phỏng của xe (F11)
 * Lưu ý: Ứng dụng là Automotive 3D Simulator, dữ liệu mô phỏng trực quan.
 */
data class VehicleTelemetry(
    val batteryPercent: Int = 84,
    val estimatedRangeKm: Int = 540,
    val speedKmh: Float = 0f,
    val powerOutputKw: Float = 0f,
    val batteryTemperatureCelsius: Float = 28f,
    val isCharging: Boolean = false,
    val odometerKm: Long = 12450L
)
