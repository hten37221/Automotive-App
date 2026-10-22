package com.android.car.automotiveapp.core.model

import kotlin.math.sqrt

data class Vector3D(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f
) {
    operator fun plus(other: Vector3D) = Vector3D(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vector3D) = Vector3D(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Vector3D(x * scalar, y * scalar, z * scalar)
    operator fun div(scalar: Float) = Vector3D(x / scalar, y / scalar, z / scalar)

    fun dot(other: Vector3D): Float = x * other.x + y * other.y + z * other.z

    fun cross(other: Vector3D): Vector3D = Vector3D(
        x = y * other.z - z * other.y,
        y = z * other.x - x * other.z,
        z = x * other.y - y * other.x
    )

    fun length(): Float = sqrt(x * x + y * y + z * z)

    fun normalized(): Vector3D {
        val len = length()
        return if (len > 0.00001f) this / len else Vector3D(0f, 0f, 1f)
    }
}

data class CameraPose(
    val azimuth: Float = 45f,      // Horizontal orbit angle in degrees (0 = front, 90 = side, 180 = rear)
    val elevation: Float = 18f,    // Vertical pitch angle in degrees (-15 to 85)
    val distance: Float = 5.2f,     // Distance from target
    val targetX: Float = 0f,       // Orbit lookAt center X
    val targetY: Float = 0f,       // Orbit lookAt center Y
    val targetZ: Float = 0f        // Orbit lookAt center Z
)

enum class CameraAnglePreset(
    val title: String,
    val pose: CameraPose
) {
    ISOMETRIC(
        title = "3/4 Toàn cảnh",
        pose = CameraPose(azimuth = 45f, elevation = 20f, distance = 5.2f, targetX = 0f, targetY = 0f, targetZ = 0f)
    ),
    FRONT(
        title = "Đầu xe",
        pose = CameraPose(azimuth = 0f, elevation = 12f, distance = 4.6f, targetX = 0f, targetY = -0.1f, targetZ = 0.8f)
    ),
    SIDE(
        title = "Thân xe",
        pose = CameraPose(azimuth = 90f, elevation = 10f, distance = 5.2f, targetX = 0f, targetY = 0f, targetZ = 0f)
    ),
    REAR(
        title = "Đuôi xe",
        pose = CameraPose(azimuth = 180f, elevation = 15f, distance = 4.8f, targetX = 0f, targetY = 0f, targetZ = -0.6f)
    ),
    TOP(
        title = "Nóc xe",
        pose = CameraPose(azimuth = 45f, elevation = 75f, distance = 5.4f, targetX = 0f, targetY = 0.2f, targetZ = 0f)
    ),
    INTERIOR(
        title = "Khoang lái",
        pose = CameraPose(azimuth = 35f, elevation = 38f, distance = 2.8f, targetX = -0.3f, targetY = 0.15f, targetZ = 0.1f)
    )
}

data class Hotspot3D(
    val part: CarPart,
    val title: String,
    val worldPosition: Vector3D,
    val recommendedPose: CameraPose
)
