package com.android.car.automotiveapp.core.model

enum class CarColor(val displayName: String, val hexColor: Long, val secondaryHex: Long) {
    CYBER_CYAN("Cyber Cyan", 0xFF00E5FF, 0xFF00838F),
    MIDNIGHT_BLACK("Midnight Stealth", 0xFF181B1E, 0xFF0B0D0F),
    HYPER_SILVER("Hyper Silver", 0xFFCFD8DC, 0xFF90A4AE),
    RACING_RED("Racing Crimson", 0xFFE53935, 0xFFB71C1C),
    PEARL_WHITE("Pearl White", 0xFFF5F7FA, 0xFFCFD8DC),
    ELECTRIC_GOLD("Electric Amber", 0xFFFFB300, 0xFFFF6F00),
    EMERALD_GREEN("Emerald Matrix", 0xFF00E676, 0xFF00B0FF)
}

enum class PaintFinish(val displayName: String, val specularShininess: Float, val metallicFactor: Float) {
    METALLIC_GLOSS("Bóng Metallic", 40f, 0.7f),
    SATIN_MATTE("Mờ Satin", 8f, 0.2f),
    CARBON_DIAMOND("Sợi Carbon Diamond", 60f, 0.9f)
}

enum class WheelRimStyle(val displayName: String, val spokes: Int) {
    AERO_BLADE_19("19\" Aero Blade", 5),
    SPORT_MULTI_20("20\" Sport Multi-Spoke", 10),
    CYBER_TURBINE_21("21\" Cyber Turbine", 7)
}

enum class CaliperColor(val displayName: String, val hexColor: Long) {
    BREMBO_RED("Đỏ Brembo", 0xFFE53935),
    NEON_CYAN("Xanh Neon Cyan", 0xFF00E5FF),
    ACID_GOLD("Vàng Acid", 0xFFFFD600),
    STEALTH_DARK("Xám Đen Stealth", 0xFF37474F)
}

enum class HeadlightMode(val displayName: String) {
    OFF("Tắt"),
    AUTO("Tự động (Auto)"),
    LOW_BEAM("Chiếu gần (Cos)"),
    HIGH_BEAM("Pha xa (Matrix LED)")
}

enum class AmbientColor(val displayName: String, val hexColor: Long) {
    CYAN("Xanh Cyan", 0xFF00E5FF),
    NEON_PURPLE("Tím Cyberpunk", 0xFFD500F9),
    AMBER_GOLD("Vàng Hổ Phách", 0xFFFFAB00),
    CRIMSON_RED("Đỏ Dung Nham", 0xFFFF1744),
    ICE_BLUE("Xanh Băng Tuyết", 0xFF00B0FF),
    EMERALD("Xanh Ngọc Lục Bảo", 0xFF00E676)
}

enum class MassageMode(val displayName: String) {
    OFF("Tắt"),
    WAVE("Sóng lượn (Wave)"),
    KNEADING("Xoa bóp sâu (Kneading)"),
    PULSE("Rung ngắt quãng (Pulse)")
}

data class SeatComfortState(
    val heatingLevel: Int = 0, // 0..3
    val coolingLevel: Int = 0, // 0..3
    val reclineDegrees: Float = 18f, // 0..45
    val slidePositionPercent: Float = 40f, // 0..100
    val heightPercent: Float = 50f, // 0..100
    val lumbarSupportPercent: Float = 60f, // 0..100
    val massageMode: MassageMode = MassageMode.OFF
)

data class TirePressureState(
    val frontLeftPsi: Float = 35.0f,
    val frontRightPsi: Float = 35.0f,
    val rearLeftPsi: Float = 36.0f,
    val rearRightPsi: Float = 36.0f,
    val temperatureCelsius: Float = 32f
)

data class CarConfiguration(
    // Ngoại thất & Thân vỏ
    val bodyColor: CarColor = CarColor.CYBER_CYAN,
    val paintFinish: PaintFinish = PaintFinish.METALLIC_GLOSS,

    // Bánh xe & Mâm lốp
    val wheelRimStyle: WheelRimStyle = WheelRimStyle.SPORT_MULTI_20,
    val caliperColor: CaliperColor = CaliperColor.BREMBO_RED,
    val tirePressure: TirePressureState = TirePressureState(),

    // Kính xe & Cửa sổ
    val windowTintPercent: Int = 45, // 0..90%
    val windowRollDownPercent: Float = 0f, // 0..1 (0 = đóng hoàn toàn, 1 = hạ kính hoàn toàn)
    val frontDefroster: Boolean = false,
    val rearDefroster: Boolean = false,
    val sunroofOpenPercent: Float = 0f, // 0..1

    // Đèn xe
    val headlightMode: HeadlightMode = HeadlightMode.AUTO,
    val drlEnabled: Boolean = true,
    val fogLightsEnabled: Boolean = false,
    val hazardLightsEnabled: Boolean = false,
    val ambientColor: AmbientColor = AmbientColor.CYAN,
    val ambientBrightness: Float = 0.85f,

    // Gương chiếu hậu
    val mirrorsFolded: Boolean = false,
    val mirrorPitch: Float = 0f, // -10..+10
    val mirrorYaw: Float = 0f, // -10..+10
    val mirrorHeating: Boolean = false,
    val blindSpotMonitoring: Boolean = true,

    // Cốp xe điện
    val trunkOpenPercent: Float = 0f, // 0f = đóng, 1f = mở hoàn toàn
    val trunkMaxHeightPercent: Int = 90, // 70..100%
    val cargoLightEnabled: Boolean = true,
    val kickSensorEnabled: Boolean = true,

    // Cửa xe & Khóa
    val driverDoorOpenPercent: Float = 0f, // 0f = đóng, 1f = mở hoàn toàn
    val doorsLocked: Boolean = true,
    val childSafetyLock: Boolean = true,

    // Ghế ngồi & Khoang cabin
    val driverSeat: SeatComfortState = SeatComfortState(),

    // Chế độ xem & Hiển thị 3D
    val nightMode: Boolean = true,
    val xrayMode: Boolean = false,
    val autoRotate: Boolean = false,
    val rotationSpeed: Float = 0.4f,

    // Thông số vận hành kỹ thuật số (Telemetry F11)
    val telemetry: VehicleTelemetry = VehicleTelemetry()
)
