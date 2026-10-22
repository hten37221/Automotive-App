package com.android.car.automotiveapp.core.model

/**
 * Các bộ phận của xe có thể chạm vào để xem và điều chỉnh thông số
 */
enum class CarPart(
    val displayName: String,
    val description: String,
    val defaultAzimuth: Float,
    val defaultElevation: Float,
    val defaultDistance: Float,
    val targetOffsetX: Float = 0f,
    val targetOffsetY: Float = 0f,
    val targetOffsetZ: Float = 0f
) {
    BODY(
        displayName = "Thân vỏ xe",
        description = "Màu sơn ngoại thất, hoàn thiện bề mặt và khí động học",
        defaultAzimuth = 45f,
        defaultElevation = 20f,
        defaultDistance = 5.2f
    ),
    GLASS_WINDOWS(
        displayName = "Kính & Cửa sổ",
        description = "Kính chắn gió, cửa sổ trời toàn cảnh và độ tối màu kính",
        defaultAzimuth = 30f,
        defaultElevation = 35f,
        defaultDistance = 3.6f,
        targetOffsetY = 0.2f
    ),
    WHEELS(
        displayName = "Bánh xe & Mâm lốp",
        description = "Kích thước mâm, kiểu vành, kẹp phanh thể thao và áp suất lốp",
        defaultAzimuth = 285f,
        defaultElevation = 12f,
        defaultDistance = 2.6f,
        targetOffsetX = -0.92f,
        targetOffsetY = -0.22f,
        targetOffsetZ = 1.25f
    ),
    HEADLIGHTS(
        displayName = "Hệ thống Đèn xe",
        description = "Đèn pha Matrix LED, dải định vị DRL và đèn nội thất Ambient",
        defaultAzimuth = 20f,
        defaultElevation = 12f,
        defaultDistance = 2.8f,
        targetOffsetX = -0.60f,
        targetOffsetY = 0.10f,
        targetOffsetZ = 1.95f
    ),
    MIRRORS(
        displayName = "Gương chiếu hậu",
        description = "Gập/mở gương điện, chỉnh góc nhìn và cảnh báo điểm mù",
        defaultAzimuth = 295f,
        defaultElevation = 16f,
        defaultDistance = 2.0f,
        targetOffsetX = -0.96f,
        targetOffsetY = 0.22f,
        targetOffsetZ = 0.45f
    ),
    TRUNK(
        displayName = "Cốp xe điện",
        description = "Đóng/mở cốp sau thông minh, giới hạn độ cao và đèn hành lý",
        defaultAzimuth = 180f,
        defaultElevation = 22f,
        defaultDistance = 3.2f,
        targetOffsetX = 0f,
        targetOffsetY = 0.25f,
        targetOffsetZ = -1.6f
    ),
    DOORS(
        displayName = "Cửa xe & Khóa",
        description = "Đóng/mở cửa xe, khóa an toàn trẻ em và chốt cửa trung tâm",
        defaultAzimuth = 270f,
        defaultElevation = 12f,
        defaultDistance = 3.2f,
        targetOffsetX = -0.88f,
        targetOffsetY = 0.0f,
        targetOffsetZ = 0.1f
    ),
    SEATS(
        displayName = "Ghế ngồi & Nội thất",
        description = "Chỉnh điện tư thế ghế, sưởi ấm, làm mát thông gió và massage",
        defaultAzimuth = 300f,
        defaultElevation = 35f,
        defaultDistance = 2.4f,
        targetOffsetX = -0.38f,
        targetOffsetY = 0.10f,
        targetOffsetZ = 0.10f
    ),
    BATTERY_CHASSIS(
        displayName = "Khung gầm & Pin EV",
        description = "Kiểm tra kết cấu khung gầm, bộ pin 100kWh và động cơ kép AWD",
        defaultAzimuth = 45f,
        defaultElevation = 16f,
        defaultDistance = 4.2f,
        targetOffsetX = 0f,
        targetOffsetY = -0.15f,
        targetOffsetZ = 0f
    );

    fun toRecommendedCameraPose(): CameraPose = CameraPose(
        azimuth = defaultAzimuth,
        elevation = defaultElevation,
        distance = defaultDistance,
        targetX = targetOffsetX,
        targetY = targetOffsetY,
        targetZ = targetOffsetZ
    )
}
