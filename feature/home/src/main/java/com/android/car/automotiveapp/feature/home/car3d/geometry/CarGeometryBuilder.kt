package com.android.car.automotiveapp.feature.home.car3d.geometry

import com.android.car.automotiveapp.core.model.CameraPose
import com.android.car.automotiveapp.core.model.CarColor
import com.android.car.automotiveapp.core.model.CarConfiguration
import com.android.car.automotiveapp.core.model.CarPart
import com.android.car.automotiveapp.core.model.Hotspot3D
import com.android.car.automotiveapp.core.model.Vector3D
import com.android.car.automotiveapp.core.model.WheelRimStyle
import com.android.car.automotiveapp.feature.home.car3d.model.HingeType
import com.android.car.automotiveapp.feature.home.car3d.model.Polygon3D
import kotlin.math.cos
import kotlin.math.sin

/**
 * Bộ tạo dựng mô hình hình học 3D của siêu xe điện thể thao (Sports EV Sedan)
 * Đã sửa toàn bộ thứ tự winding và chuẩn hóa pháp tuyến outward (F01, F02, F10)
 */
object CarGeometryBuilder {

    /**
     * Danh sách các điểm Hotspot 3D gắn trực tiếp lên xe
     * Đã chuẩn hóa toàn bộ Camera Focus Presets đúng góc nhìn trực quan của từng chi tiết (F09)
     */
    val hotspots: List<Hotspot3D> = listOf(
        Hotspot3D(
            part = CarPart.BODY,
            title = "Màu sơn & Thân vỏ",
            worldPosition = Vector3D(0f, 0.22f, 1.2f),
            recommendedPose = CameraPose(azimuth = 45f, elevation = 20f, distance = 5.2f, targetX = 0f, targetY = 0f, targetZ = 0f)
        ),
        Hotspot3D(
            part = CarPart.WHEELS,
            title = "Bánh & Mâm lốp",
            worldPosition = Vector3D(-0.98f, -0.22f, 1.25f),
            // Bánh trước trái ở X = -0.98f, góc azimuth 285 độ nhìn thẳng từ bên trái xe
            recommendedPose = CameraPose(azimuth = 285f, elevation = 12f, distance = 2.6f, targetX = -0.92f, targetY = -0.22f, targetZ = 1.25f)
        ),
        Hotspot3D(
            part = CarPart.HEADLIGHTS,
            title = "Đèn Matrix LED",
            worldPosition = Vector3D(-0.65f, 0.12f, 1.95f),
            // Nhìn từ góc 20 độ phía trước vào cụm đèn pha
            recommendedPose = CameraPose(azimuth = 20f, elevation = 12f, distance = 2.8f, targetX = -0.60f, targetY = 0.10f, targetZ = 1.95f)
        ),
        Hotspot3D(
            part = CarPart.MIRRORS,
            title = "Gương chiếu hậu",
            worldPosition = Vector3D(-0.98f, 0.22f, 0.45f),
            // Gương trái ở X = -0.98f, góc azimuth 295 độ nhìn từ bên ngoài sườn xe
            recommendedPose = CameraPose(azimuth = 295f, elevation = 16f, distance = 2.0f, targetX = -0.96f, targetY = 0.22f, targetZ = 0.45f)
        ),
        Hotspot3D(
            part = CarPart.TRUNK,
            title = "Cốp xe điện",
            worldPosition = Vector3D(0f, 0.35f, -1.82f),
            // Đứng phía sau đuôi nhìn vào khoang cốp
            recommendedPose = CameraPose(azimuth = 180f, elevation = 22f, distance = 3.2f, targetX = 0f, targetY = 0.25f, targetZ = -1.6f)
        ),
        Hotspot3D(
            part = CarPart.DOORS,
            title = "Cửa xe & Khóa",
            worldPosition = Vector3D(-0.96f, 0.05f, 0.1f),
            // Cửa tài xế ở bên trái, góc azimuth 270 độ nhìn ngang sườn xe
            recommendedPose = CameraPose(azimuth = 270f, elevation = 12f, distance = 3.2f, targetX = -0.88f, targetY = 0.0f, targetZ = 0.1f)
        ),
        Hotspot3D(
            part = CarPart.GLASS_WINDOWS,
            title = "Kính & Cửa sổ trời",
            worldPosition = Vector3D(0f, 0.52f, 0.2f),
            recommendedPose = CameraPose(azimuth = 45f, elevation = 38f, distance = 3.4f, targetX = 0f, targetY = 0.35f, targetZ = 0.1f)
        ),
        Hotspot3D(
            part = CarPart.SEATS,
            title = "Ghế ngồi & Cabin",
            worldPosition = Vector3D(-0.38f, 0.15f, 0.1f),
            // Ghế lái ở X = -0.38f, góc nhìn từ cửa lái azimuth 300 độ với góc dốc 35 độ nhìn xuống cabin
            recommendedPose = CameraPose(azimuth = 300f, elevation = 35f, distance = 2.4f, targetX = -0.38f, targetY = 0.10f, targetZ = 0.10f)
        ),
        Hotspot3D(
            part = CarPart.BATTERY_CHASSIS,
            title = "Khung gầm & Pin EV",
            worldPosition = Vector3D(0f, -0.32f, 0f),
            recommendedPose = CameraPose(azimuth = 45f, elevation = 16f, distance = 4.2f, targetX = 0f, targetY = -0.15f, targetZ = 0f)
        )
    )

    /**
     * Tạo toàn bộ danh sách đa giác 3D của xe dựa theo cấu hình hiện tại
     */
    fun buildCarModel(config: CarConfiguration): List<Polygon3D> {
        val polygons = mutableListOf<Polygon3D>()

        val bodyColorHex = config.bodyColor.hexColor
        val bodySecondaryHex = config.bodyColor.secondaryHex
        val isXray = config.xrayMode

        // 1. CHASSIS & THÂN VỎ CHÍNH (Front Hood, Fascia, Fenders, Roof pillars, Rear diffuser)
        buildBodyShell(polygons, bodyColorHex, bodySecondaryHex, isXray)

        // 2. KÍNH XE & CỬA SỔ TRỜI TOÀN CẢNH (Glass windows, windshield, sunroof)
        buildGlassWindows(polygons, config)

        // 3. CỐP XE ĐIỆN (Trunk Deck lid - có thể mở lật lên theo trục bản lề sau)
        buildTrunkLid(polygons, bodyColorHex, isXray)

        // 4. CỬA XE (Driver Door - có thể mở hé ra theo trục bản lề trước)
        buildDoors(polygons, bodyColorHex, isXray, config)

        // 5. GƯƠNG CHIẾU HẬU (Side Mirrors)
        buildMirrors(polygons, bodyColorHex, config)

        // 6. HỆ THỐNG ĐÈN XE (LED Headlights, DRLs, OLED rear lightbar)
        buildLighting(polygons, config)

        // 7. BÁNH XE & MÂM LỐP (4 Wheels with animated rims, tires and brake calipers)
        buildWheels(polygons, config)

        // 8. GHẾ NGỒI & KHOANG NỘI THẤT (Interior seats, steering wheel, dashboard)
        buildInterior(polygons, config)

        // 9. KHUNG GẦM & BỘ PIN ĐIỆN EV (100kWh Battery Skateboard & Electric Motors)
        buildChassisAndBattery(polygons, config)

        return polygons
    }

    private fun addQuad(
        list: MutableList<Polygon3D>,
        v1: Vector3D, v2: Vector3D, v3: Vector3D, v4: Vector3D,
        part: CarPart,
        color: Long,
        alpha: Float = 1.0f,
        isTransparent: Boolean = false,
        isEmissive: Boolean = false,
        emissiveColor: Long = color,
        isDoubleSided: Boolean = false,
        hingeType: HingeType = HingeType.NONE
    ) {
        val normal1 = (v2 - v1).cross(v3 - v1).normalized()
        list.add(
            Polygon3D(
                vertices = listOf(v1, v2, v3, v4),
                normal = normal1,
                part = part,
                baseColor = color,
                isTransparent = isTransparent,
                alpha = alpha,
                isEmissive = isEmissive,
                emissiveColor = emissiveColor,
                isDoubleSided = isDoubleSided,
                hingeType = hingeType
            )
        )
    }

    private fun addTriangle(
        list: MutableList<Polygon3D>,
        v1: Vector3D, v2: Vector3D, v3: Vector3D,
        part: CarPart,
        color: Long,
        alpha: Float = 1.0f,
        isTransparent: Boolean = false,
        isEmissive: Boolean = false,
        emissiveColor: Long = color,
        isDoubleSided: Boolean = false,
        hingeType: HingeType = HingeType.NONE
    ) {
        val normal = (v2 - v1).cross(v3 - v1).normalized()
        list.add(
            Polygon3D(
                vertices = listOf(v1, v2, v3),
                normal = normal,
                part = part,
                baseColor = color,
                isTransparent = isTransparent,
                alpha = alpha,
                isEmissive = isEmissive,
                emissiveColor = emissiveColor,
                isDoubleSided = isDoubleSided,
                hingeType = hingeType
            )
        )
    }

    // -------------------------------------------------------------
    // 1. THÂN VỎ XE (BODY SHELL) - Chuẩn hóa Outward Normals
    // -------------------------------------------------------------
    private fun buildBodyShell(
        polygons: MutableList<Polygon3D>,
        primaryColor: Long,
        secondaryColor: Long,
        isXray: Boolean
    ) {
        val bodyColor = if (isXray) 0xFF37474F else primaryColor
        val bodyAlpha = if (isXray) 0.35f else 1.0f
        val trimColor = 0xFF14171A

        // Nắp ca-pô (Front Hood) - Outward normal hướng lên trên (+Y)
        val hoodFL = Vector3D(-0.75f, 0.22f, 0.70f)
        val hoodFR = Vector3D(0.75f, 0.22f, 0.70f)
        val hoodCenterL = Vector3D(-0.68f, 0.18f, 1.45f)
        val hoodCenterR = Vector3D(0.68f, 0.18f, 1.45f)
        val noseL = Vector3D(-0.55f, 0.08f, 2.05f)
        val noseR = Vector3D(0.55f, 0.08f, 2.05f)

        // Sửa winding cho nắp ca-pô: (+Z) x (+X) = +Y (hướng lên trên)
        addQuad(polygons, hoodFL, hoodCenterL, hoodCenterR, hoodFR, CarPart.BODY, bodyColor, bodyAlpha, isTransparent = isXray)
        addQuad(polygons, hoodCenterL, noseL, noseR, hoodCenterR, CarPart.BODY, bodyColor, bodyAlpha, isTransparent = isXray)

        // Cản trước và lưới tản nhiệt khí động học (Front Bumper & Nose) - Hướng về phía trước (+Z)
        val bumperBottomL = Vector3D(-0.80f, -0.32f, 2.05f)
        val bumperBottomR = Vector3D(0.80f, -0.32f, 2.05f)
        val noseCenterL = Vector3D(-0.82f, -0.05f, 2.08f)
        val noseCenterR = Vector3D(0.82f, -0.05f, 2.08f)

        // Winding (+X) x (+Y) = +Z (hướng về phía trước)
        addQuad(polygons, noseCenterL, noseCenterR, noseR, noseL, CarPart.BODY, bodyColor, bodyAlpha, isTransparent = isXray)
        addQuad(polygons, bumperBottomL, bumperBottomR, noseCenterR, noseCenterL, CarPart.BODY, trimColor)

        // Hốc gió cản trước (Front Air Curtains)
        addQuad(polygons, Vector3D(-0.50f, -0.28f, 2.06f), Vector3D(0.50f, -0.28f, 2.06f), Vector3D(0.45f, -0.15f, 2.06f), Vector3D(-0.45f, -0.15f, 2.06f), CarPart.BODY, 0xFF0D0F11)

        // Vè chắn bùn trước (Front Fenders)
        // Vè trái: Hướng ra ngoài bên trái (-X)
        addQuad(polygons, Vector3D(-0.90f, 0.18f, 0.70f), Vector3D(-0.92f, 0.12f, 1.45f), hoodCenterL, hoodFL, CarPart.BODY, secondaryColor, bodyAlpha, isTransparent = isXray)
        addQuad(polygons, Vector3D(-0.92f, 0.12f, 1.45f), Vector3D(-0.82f, -0.05f, 2.08f), noseL, hoodCenterL, CarPart.BODY, secondaryColor, bodyAlpha, isTransparent = isXray)
        // Vè phải: Hướng ra ngoài bên phải (+X)
        addQuad(polygons, Vector3D(0.92f, 0.12f, 1.45f), Vector3D(0.90f, 0.18f, 0.70f), hoodFR, hoodCenterR, CarPart.BODY, secondaryColor, bodyAlpha, isTransparent = isXray)
        addQuad(polygons, Vector3D(0.82f, -0.05f, 2.08f), Vector3D(0.92f, 0.12f, 1.45f), hoodCenterR, noseR, CarPart.BODY, secondaryColor, bodyAlpha, isTransparent = isXray)

        // Cột A và Khung nóc xe (A-Pillars & Roof Rails)
        val roofFrontL = Vector3D(-0.62f, 0.65f, 0.05f)
        val roofFrontR = Vector3D(0.62f, 0.65f, 0.05f)
        val roofRearL = Vector3D(-0.60f, 0.62f, -0.75f)
        val roofRearR = Vector3D(0.60f, 0.62f, -0.75f)

        // Cột A trái & phải
        addQuad(polygons, Vector3D(-0.85f, 0.24f, 0.68f), Vector3D(-0.68f, 0.64f, 0.05f), roofFrontL, hoodFL, CarPart.BODY, trimColor)
        addQuad(polygons, hoodFR, roofFrontR, Vector3D(0.68f, 0.64f, 0.05f), Vector3D(0.85f, 0.24f, 0.68f), CarPart.BODY, trimColor)

        // Ray nóc xe (Roof rails)
        addQuad(polygons, Vector3D(-0.68f, 0.64f, 0.05f), Vector3D(-0.66f, 0.61f, -0.75f), roofRearL, roofFrontL, CarPart.BODY, trimColor)
        addQuad(polygons, roofFrontR, roofRearR, Vector3D(0.66f, 0.61f, -0.75f), Vector3D(0.68f, 0.64f, 0.05f), CarPart.BODY, trimColor)

        // Cột C dốc phong cách Fastback Coupe
        val trunkBaseL = Vector3D(-0.75f, 0.35f, -1.45f)
        val trunkBaseR = Vector3D(0.75f, 0.35f, -1.45f)
        addQuad(polygons, Vector3D(-0.66f, 0.61f, -0.75f), Vector3D(-0.85f, 0.32f, -1.45f), trunkBaseL, roofRearL, CarPart.BODY, trimColor)
        addQuad(polygons, roofRearR, trunkBaseR, Vector3D(0.85f, 0.32f, -1.45f), Vector3D(0.66f, 0.61f, -0.75f), CarPart.BODY, trimColor)

        // Hông sau xe & Vè bánh sau (Rear Quarter Panels & Haunches)
        val rearFenderL = Vector3D(-0.95f, 0.28f, -1.25f)
        val rearFenderR = Vector3D(0.95f, 0.28f, -1.25f)
        val rearBumperTopL = Vector3D(-0.82f, 0.20f, -1.98f)
        val rearBumperTopR = Vector3D(0.82f, 0.20f, -1.98f)
        val rearBumperBotL = Vector3D(-0.78f, -0.32f, -1.95f)
        val rearBumperBotR = Vector3D(0.78f, -0.32f, -1.95f)

        // Hông sau trái (-X)
        addQuad(polygons, Vector3D(-0.85f, 0.32f, -1.45f), Vector3D(-0.75f, 0.32f, -1.95f), rearBumperTopL, trunkBaseL, CarPart.BODY, bodyColor, bodyAlpha, isTransparent = isXray)
        addQuad(polygons, rearFenderL, Vector3D(-0.90f, 0.05f, -1.95f), rearBumperTopL, Vector3D(-0.85f, 0.32f, -1.45f), CarPart.BODY, secondaryColor, bodyAlpha, isTransparent = isXray)

        // Hông sau phải (+X)
        addQuad(polygons, trunkBaseR, rearBumperTopR, Vector3D(0.75f, 0.32f, -1.95f), Vector3D(0.85f, 0.32f, -1.45f), CarPart.BODY, bodyColor, bodyAlpha, isTransparent = isXray)
        addQuad(polygons, Vector3D(0.85f, 0.32f, -1.45f), rearBumperTopR, Vector3D(0.90f, 0.05f, -1.95f), rearFenderR, CarPart.BODY, secondaryColor, bodyAlpha, isTransparent = isXray)

        // Cản sau & Bộ khuếch tán gió gầm (Rear Diffuser & Fins) - Hướng về phía sau (-Z)
        addQuad(polygons, rearBumperTopL, rearBumperTopR, rearBumperBotR, rearBumperBotL, CarPart.BODY, trimColor)
        // Aerodynamic Diffuser fins (Vây khuếch tán gió)
        addQuad(polygons, Vector3D(-0.35f, -0.32f, -1.75f), Vector3D(-0.35f, -0.18f, -1.75f), Vector3D(-0.35f, -0.18f, -1.96f), Vector3D(-0.35f, -0.32f, -1.96f), CarPart.BODY, 0xFF00E5FF)
        addQuad(polygons, Vector3D(0.35f, -0.32f, -1.96f), Vector3D(0.35f, -0.18f, -1.96f), Vector3D(0.35f, -0.18f, -1.75f), Vector3D(0.35f, -0.32f, -1.75f), CarPart.BODY, 0xFF00E5FF)

        // Cửa phụ bên phải (Passenger side door - cố định, hướng ra ngoài bên phải +X)
        val passSillFront = Vector3D(0.88f, -0.28f, 0.70f)
        val passSillRear = Vector3D(0.88f, -0.28f, -0.70f)
        val passBeltFront = Vector3D(0.90f, 0.20f, 0.70f)
        val passBeltRear = Vector3D(0.90f, 0.22f, -0.70f)
        addQuad(polygons, passBeltFront, passSillFront, passSillRear, passBeltRear, CarPart.BODY, bodyColor, bodyAlpha, isTransparent = isXray)
    }

    // -------------------------------------------------------------
    // 2. KÍNH XE & CỬA SỔ TRỜI (GLASS & WINDOWS)
    // -------------------------------------------------------------
    private fun buildGlassWindows(polygons: MutableList<Polygon3D>, config: CarConfiguration) {
        val tintFactor = config.windowTintPercent / 100f
        val glassColor = 0xFF1A303A
        // Alpha tùy chỉnh theo độ tối màu kính (từ 0.40 đến 0.92)
        val glassAlpha = 0.45f + tintFactor * 0.45f

        val hoodFL = Vector3D(-0.75f, 0.22f, 0.70f)
        val hoodFR = Vector3D(0.75f, 0.22f, 0.70f)
        val roofFrontL = Vector3D(-0.62f, 0.65f, 0.05f)
        val roofFrontR = Vector3D(0.62f, 0.65f, 0.05f)
        val roofRearL = Vector3D(-0.60f, 0.62f, -0.75f)
        val roofRearR = Vector3D(0.60f, 0.62f, -0.75f)
        val trunkBaseL = Vector3D(-0.75f, 0.35f, -1.45f)
        val trunkBaseR = Vector3D(0.75f, 0.35f, -1.45f)

        // Kính chắn gió trước (Front Windshield)
        addQuad(polygons, hoodFL, hoodFR, roofFrontR, roofFrontL, CarPart.GLASS_WINDOWS, glassColor, alpha = glassAlpha, isTransparent = true, isDoubleSided = true)

        // Cửa sổ trời toàn cảnh (Panoramic Glass Roof)
        addQuad(polygons, roofFrontL, roofFrontR, roofRearR, roofRearL, CarPart.GLASS_WINDOWS, 0xFF14242E, alpha = 0.75f, isTransparent = true, isDoubleSided = true)

        // Kính chắn gió sau (Rear Window)
        addQuad(polygons, roofRearL, roofRearR, trunkBaseR, trunkBaseL, CarPart.GLASS_WINDOWS, glassColor, alpha = glassAlpha, isTransparent = true, isDoubleSided = true)

        // Kính hông bên phải (Passenger side window)
        val passWindowTopF = Vector3D(0.64f, 0.62f, 0.08f)
        val passWindowTopR = Vector3D(0.62f, 0.58f, -0.70f)
        val passWindowBotF = Vector3D(0.88f, 0.22f, 0.65f)
        val passWindowBotR = Vector3D(0.88f, 0.24f, -0.68f)
        addQuad(polygons, passWindowTopF, passWindowTopR, passWindowBotR, passWindowBotF, CarPart.GLASS_WINDOWS, glassColor, alpha = glassAlpha, isTransparent = true, isDoubleSided = true)

        // Sợi sưởi kính chắn gió trước (Front Defroster Heating Filaments - F10 visual feedback)
        if (config.frontDefroster) {
            val dY1 = 0.32f
            val dY2 = 0.44f
            addQuad(polygons, Vector3D(-0.65f, dY1, 0.55f), Vector3D(0.65f, dY1, 0.55f), Vector3D(0.65f, dY1 + 0.015f, 0.53f), Vector3D(-0.65f, dY1 + 0.015f, 0.53f), CarPart.GLASS_WINDOWS, 0xFFFF7043, isEmissive = true)
            addQuad(polygons, Vector3D(-0.60f, dY2, 0.35f), Vector3D(0.60f, dY2, 0.35f), Vector3D(0.60f, dY2 + 0.015f, 0.33f), Vector3D(-0.60f, dY2 + 0.015f, 0.33f), CarPart.GLASS_WINDOWS, 0xFFFF7043, isEmissive = true)
        }

        // Sợi sưởi kính sau (Rear Defroster Filaments - F10 visual feedback)
        if (config.rearDefroster) {
            val rY1 = 0.48f
            val rY2 = 0.40f
            addQuad(polygons, Vector3D(-0.55f, rY1, -1.05f), Vector3D(0.55f, rY1, -1.05f), Vector3D(0.55f, rY1 + 0.012f, -1.07f), Vector3D(-0.55f, rY1 + 0.012f, -1.07f), CarPart.GLASS_WINDOWS, 0xFFFF7043, isEmissive = true)
            addQuad(polygons, Vector3D(-0.60f, rY2, -1.25f), Vector3D(0.60f, rY2, -1.25f), Vector3D(0.60f, rY2 + 0.012f, -1.27f), Vector3D(-0.60f, rY2 + 0.012f, -1.27f), CarPart.GLASS_WINDOWS, 0xFFFF7043, isEmissive = true)
        }
    }

    // -------------------------------------------------------------
    // 3. CỐP XE ĐIỆN (TRUNK LID)
    // -------------------------------------------------------------
    private fun buildTrunkLid(polygons: MutableList<Polygon3D>, bodyColor: Long, isXray: Boolean) {
        val trunkBaseL = Vector3D(-0.75f, 0.35f, -1.45f)
        val trunkBaseR = Vector3D(0.75f, 0.35f, -1.45f)
        val trunkLipL = Vector3D(-0.75f, 0.33f, -1.95f)
        val trunkLipR = Vector3D(0.75f, 0.33f, -1.95f)
        val trunkBackL = Vector3D(-0.75f, 0.12f, -1.98f)
        val trunkBackR = Vector3D(0.75f, 0.12f, -1.98f)

        val alpha = if (isXray) 0.35f else 1.0f

        // Nắp trên của cốp (Trunk Top Deck)
        addQuad(polygons, trunkBaseL, trunkBaseR, trunkLipR, trunkLipL, CarPart.TRUNK, bodyColor, alpha, isTransparent = isXray, hingeType = HingeType.TRUNK_LID)

        // Đuôi dựng của cốp (Trunk Vertical Tailgate with Spoiler)
        addQuad(polygons, trunkLipL, trunkLipR, trunkBackR, trunkBackL, CarPart.TRUNK, bodyColor, alpha, isTransparent = isXray, hingeType = HingeType.TRUNK_LID)

        // Cánh lướt gió thể thao gắn trên mép cốp (Integrated Rear Lip Spoiler)
        val spoilerTopL = Vector3D(-0.76f, 0.37f, -1.97f)
        val spoilerTopR = Vector3D(0.76f, 0.37f, -1.97f)
        addQuad(polygons, trunkLipL, trunkLipR, spoilerTopR, spoilerTopL, CarPart.TRUNK, 0xFF0D0F11, 1.0f, hingeType = HingeType.TRUNK_LID)
    }

    // -------------------------------------------------------------
    // 4. CỬA XE (DRIVER DOOR) - Chuẩn hóa Winding & Window Roll Down
    // -------------------------------------------------------------
    private fun buildDoors(polygons: MutableList<Polygon3D>, bodyColor: Long, isXray: Boolean, config: CarConfiguration) {
        val alpha = if (isXray) 0.35f else 1.0f

        // Cửa người lái bên trái (Driver Side Door Panel) - Outward hướng sang trái (-X)
        val doorSillF = Vector3D(-0.88f, -0.28f, 0.70f)
        val doorSillR = Vector3D(-0.88f, -0.28f, -0.70f)
        val doorBeltF = Vector3D(-0.90f, 0.20f, 0.70f)
        val doorBeltR = Vector3D(-0.90f, 0.22f, -0.70f)

        // Winding: (-Z) x (-Y) = -X (hướng ra ngoài bên trái)
        addQuad(polygons, doorBeltF, doorBeltR, doorSillR, doorSillF, CarPart.DOORS, bodyColor, alpha, isTransparent = isXray, hingeType = HingeType.DRIVER_DOOR)

        // Tay nắm cửa chìm khí động học (Flush Door Handle)
        val handleF = Vector3D(-0.91f, 0.16f, -0.25f)
        val handleR = Vector3D(-0.91f, 0.16f, -0.45f)
        val handleBotR = Vector3D(-0.91f, 0.12f, -0.45f)
        val handleBotF = Vector3D(-0.91f, 0.12f, -0.25f)
        addQuad(polygons, handleF, handleR, handleBotR, handleBotF, CarPart.DOORS, 0xFF00E5FF, 1.0f, isEmissive = true, hingeType = HingeType.DRIVER_DOOR)

        // Khung kính cửa tài xế (Driver Door Glass) - Tích hợp hạ kính điện F10
        val rollDownOffset = config.windowRollDownPercent * 0.34f
        val driverWindowTopF = Vector3D(-0.64f, 0.62f - rollDownOffset, 0.08f)
        val driverWindowTopR = Vector3D(-0.62f, 0.58f - rollDownOffset, -0.70f)
        addQuad(polygons, driverWindowTopF, driverWindowTopR, doorBeltR, doorBeltF, CarPart.GLASS_WINDOWS, 0xFF1A303A, alpha = 0.55f, isTransparent = true, isDoubleSided = true, hingeType = HingeType.DRIVER_DOOR)
    }

    // -------------------------------------------------------------
    // 5. GƯƠNG CHIẾU HẬU (SIDE MIRRORS) - Tích hợp Mirror Tilt F10
    // -------------------------------------------------------------
    private fun buildMirrors(polygons: MutableList<Polygon3D>, bodyColor: Long, config: CarConfiguration) {
        val isFolded = config.mirrorsFolded
        val foldOffset = if (isFolded) 0.12f else 0f
        val mirrorZOffset = if (isFolded) -0.1f else 0f

        // Độ nghiêng gương chiếu hậu theo điều khiển D-Pad (F10)
        val pitchRad = Math.toRadians((config.mirrorPitch * 1.5).toDouble()).toFloat()
        val yawRad = Math.toRadians((config.mirrorYaw * 1.5).toDouble()).toFloat()
        val glassTiltX = sin(yawRad) * 0.02f
        val glassTiltY = sin(pitchRad) * 0.02f

        // Gương trái (Driver side mirror)
        val mLF = Vector3D(-0.98f + foldOffset, 0.24f, 0.50f + mirrorZOffset)
        val mLR = Vector3D(-0.98f + foldOffset, 0.24f, 0.38f + mirrorZOffset)
        val mLBotR = Vector3D(-0.95f + foldOffset, 0.16f, 0.38f + mirrorZOffset)
        val mLBotF = Vector3D(-0.95f + foldOffset, 0.16f, 0.50f + mirrorZOffset)

        // Thân vỏ gương
        addQuad(polygons, mLF, mLR, mLBotR, mLBotF, CarPart.MIRRORS, bodyColor)
        // Mặt kính gương phản chiếu (Mirror Glass surface) có tilt góc nhìn
        addQuad(
            polygons,
            Vector3D(-0.93f + foldOffset + glassTiltX, 0.23f + glassTiltY, 0.40f + mirrorZOffset),
            Vector3D(-0.93f + foldOffset - glassTiltX, 0.23f + glassTiltY, 0.48f + mirrorZOffset),
            Vector3D(-0.93f + foldOffset - glassTiltX, 0.17f - glassTiltY, 0.48f + mirrorZOffset),
            Vector3D(-0.93f + foldOffset + glassTiltX, 0.17f - glassTiltY, 0.40f + mirrorZOffset),
            CarPart.MIRRORS,
            0xFFCFD8DC,
            isDoubleSided = true
        )
        // Dải đèn xi nhan LED trên gương
        addQuad(polygons, Vector3D(-1.00f + foldOffset, 0.20f, 0.48f + mirrorZOffset), Vector3D(-1.00f + foldOffset, 0.20f, 0.40f + mirrorZOffset), Vector3D(-0.99f + foldOffset, 0.18f, 0.40f + mirrorZOffset), Vector3D(-0.99f + foldOffset, 0.18f, 0.48f + mirrorZOffset), CarPart.MIRRORS, 0xFFFFB300, isEmissive = true)

        // Gương phải (Passenger side mirror)
        val mRF = Vector3D(0.98f - foldOffset, 0.24f, 0.50f + mirrorZOffset)
        val mRR = Vector3D(0.98f - foldOffset, 0.24f, 0.38f + mirrorZOffset)
        val mRBotR = Vector3D(0.95f - foldOffset, 0.16f, 0.38f + mirrorZOffset)
        val mRBotF = Vector3D(0.95f - foldOffset, 0.16f, 0.50f + mirrorZOffset)

        addQuad(polygons, mRF, mRBotF, mRBotR, mRR, CarPart.MIRRORS, bodyColor)
        // Mặt kính gương phải
        addQuad(polygons, Vector3D(0.93f - foldOffset, 0.23f, 0.48f + mirrorZOffset), Vector3D(0.93f - foldOffset, 0.23f, 0.40f + mirrorZOffset), Vector3D(0.93f - foldOffset, 0.17f, 0.40f + mirrorZOffset), Vector3D(0.93f - foldOffset, 0.17f, 0.48f + mirrorZOffset), CarPart.MIRRORS, 0xFFCFD8DC, isDoubleSided = true)
        // Xi nhan gương phải
        addQuad(polygons, Vector3D(1.00f - foldOffset, 0.20f, 0.40f + mirrorZOffset), Vector3D(1.00f - foldOffset, 0.20f, 0.48f + mirrorZOffset), Vector3D(0.99f - foldOffset, 0.18f, 0.48f + mirrorZOffset), Vector3D(0.99f - foldOffset, 0.18f, 0.40f + mirrorZOffset), CarPart.MIRRORS, 0xFFFFB300, isEmissive = true)
    }

    // -------------------------------------------------------------
    // 6. HỆ THỐNG ĐÈN XE (LIGHTING) - Winding Hướng Forward (+Z)
    // -------------------------------------------------------------
    private fun buildLighting(polygons: MutableList<Polygon3D>, config: CarConfiguration) {
        val isHeadlightsOn = config.headlightMode != com.android.car.automotiveapp.core.model.HeadlightMode.OFF
        val lightColor = if (isHeadlightsOn) 0xFFE0F7FA else 0xFF37474F
        val lightEmissive = isHeadlightsOn

        // Cụm đèn pha LED bên trái (Front Left Matrix LED) - Normal hướng +Z
        val hlL1 = Vector3D(-0.78f, 0.14f, 1.95f)
        val hlL2 = Vector3D(-0.54f, 0.11f, 2.05f)
        val hlL3 = Vector3D(-0.55f, 0.05f, 2.05f)
        val hlL4 = Vector3D(-0.76f, 0.07f, 1.96f)
        addQuad(polygons, hlL4, hlL3, hlL2, hlL1, CarPart.HEADLIGHTS, lightColor, isEmissive = lightEmissive)

        // Dải LED ban ngày DRL trái (Cyber Light Blade) - Normal hướng +Z
        val drlColor = if (config.drlEnabled) 0xFF00E5FF else 0xFF455A64
        addQuad(polygons, Vector3D(-0.79f, 0.14f, 1.94f), Vector3D(-0.53f, 0.11f, 2.06f), Vector3D(-0.52f, 0.13f, 2.06f), Vector3D(-0.80f, 0.16f, 1.93f), CarPart.HEADLIGHTS, drlColor, isEmissive = config.drlEnabled)

        // Cụm đèn pha LED bên phải (Front Right Matrix LED) - Normal hướng +Z
        val hlR1 = Vector3D(0.54f, 0.11f, 2.05f)
        val hlR2 = Vector3D(0.78f, 0.14f, 1.95f)
        val hlR3 = Vector3D(0.76f, 0.07f, 1.96f)
        val hlR4 = Vector3D(0.55f, 0.05f, 2.05f)
        addQuad(polygons, hlR4, hlR3, hlR2, hlR1, CarPart.HEADLIGHTS, lightColor, isEmissive = lightEmissive)

        // Dải LED ban ngày DRL phải - Normal hướng +Z
        addQuad(polygons, Vector3D(0.53f, 0.11f, 2.06f), Vector3D(0.79f, 0.14f, 1.94f), Vector3D(0.80f, 0.16f, 1.93f), Vector3D(0.52f, 0.13f, 2.06f), CarPart.HEADLIGHTS, drlColor, isEmissive = config.drlEnabled)

        // Đèn sương mù dưới (Fog lights) - Normal hướng +Z
        val fogColor = if (config.fogLightsEnabled) 0xFFFFEB3B else 0xFF263238
        addQuad(polygons, Vector3D(-0.75f, -0.26f, 2.04f), Vector3D(-0.62f, -0.26f, 2.04f), Vector3D(-0.62f, -0.22f, 2.04f), Vector3D(-0.75f, -0.22f, 2.04f), CarPart.HEADLIGHTS, fogColor, isEmissive = config.fogLightsEnabled)
        addQuad(polygons, Vector3D(0.62f, -0.26f, 2.04f), Vector3D(0.75f, -0.26f, 2.04f), Vector3D(0.75f, -0.22f, 2.04f), Vector3D(0.62f, -0.22f, 2.04f), CarPart.HEADLIGHTS, fogColor, isEmissive = config.fogLightsEnabled)

        // Dải đèn hậu OLED chạy dài toàn chiều ngang đuôi xe (Continuous Full-Width Rear Lightbar) - Normal hướng -Z
        val tailLightColor = 0xFFFF1744
        val tailL1 = Vector3D(-0.80f, 0.22f, -1.97f)
        val tailR1 = Vector3D(0.80f, 0.22f, -1.97f)
        val tailR2 = Vector3D(0.80f, 0.17f, -1.97f)
        val tailL2 = Vector3D(-0.80f, 0.17f, -1.97f)
        addQuad(polygons, tailL1, tailR1, tailR2, tailL2, CarPart.HEADLIGHTS, tailLightColor, isEmissive = true, hingeType = HingeType.TRUNK_LID)
    }

    // -------------------------------------------------------------
    // 7. BÁNH XE & MÂM LỐP (WHEELS & TIRES)
    // -------------------------------------------------------------
    private fun buildWheels(polygons: MutableList<Polygon3D>, config: CarConfiguration) {
        val wheelPositions = listOf(
            Pair(Vector3D(-0.92f, -0.22f, 1.25f), true),   // Front Left
            Pair(Vector3D(0.92f, -0.22f, 1.25f), false),   // Front Right
            Pair(Vector3D(-0.92f, -0.22f, -1.25f), true),  // Rear Left
            Pair(Vector3D(0.92f, -0.22f, -1.25f), false)   // Rear Right
        )

        val tireRadius = 0.32f
        val rimRadius = 0.22f
        val tireWidth = 0.16f
        val tireColor = 0xFF1C1E20
        val rimColor = 0xFFECEFF1
        val rimSpokeColor = 0xFF37474F
        val caliperColor = config.caliperColor.hexColor

        wheelPositions.forEach { (center, isLeft) ->
            val sign = if (isLeft) -1f else 1f
            val segments = 12

            // Vòng lốp xe đa giác (Tire Tread & Outer Face)
            for (i in 0 until segments) {
                val theta1 = Math.toRadians((i * (360.0 / segments))).toFloat()
                val theta2 = Math.toRadians(((i + 1) * (360.0 / segments))).toFloat()

                val y1 = center.y + tireRadius * sin(theta1)
                val z1 = center.z + tireRadius * cos(theta1)
                val y2 = center.y + tireRadius * sin(theta2)
                val z2 = center.z + tireRadius * cos(theta2)

                val xOuter = center.x + sign * (tireWidth / 2)
                val xInner = center.x - sign * (tireWidth / 2)

                // Bề mặt gai lốp tiếp đất (Tire tread outer band)
                val t1 = Vector3D(xOuter, y1, z1)
                val t2 = Vector3D(xInner, y1, z1)
                val t3 = Vector3D(xInner, y2, z2)
                val t4 = Vector3D(xOuter, y2, z2)
                if (isLeft) {
                    addQuad(polygons, t1, t4, t3, t2, CarPart.WHEELS, tireColor)
                } else {
                    addQuad(polygons, t1, t2, t3, t4, CarPart.WHEELS, tireColor)
                }

                // Mặt ngoài của lốp (Tire sidewall)
                val ry1 = center.y + rimRadius * sin(theta1)
                val rz1 = center.z + rimRadius * cos(theta1)
                val ry2 = center.y + rimRadius * sin(theta2)
                val rz2 = center.z + rimRadius * cos(theta2)

                val s1 = Vector3D(xOuter, y1, z1)
                val s2 = Vector3D(xOuter, y2, z2)
                val s3 = Vector3D(xOuter, ry2, rz2)
                val s4 = Vector3D(xOuter, ry1, rz1)
                if (isLeft) {
                    addQuad(polygons, s1, s2, s3, s4, CarPart.WHEELS, 0xFF24272A)
                } else {
                    addQuad(polygons, s1, s4, s3, s2, CarPart.WHEELS, 0xFF24272A)
                }
            }

            // Đĩa phanh & Cùm phanh thể thao bên trong mâm (Brake Rotor & Caliper)
            val discX = center.x + sign * 0.02f
            val discRadius = 0.18f
            for (i in 0 until 6) {
                val a1 = Math.toRadians((i * 60.0)).toFloat()
                val a2 = Math.toRadians(((i + 1) * 60.0)).toFloat()
                val vDiscCenter = Vector3D(discX, center.y, center.z)
                val vD1 = Vector3D(discX, center.y + discRadius * sin(a1), center.z + discRadius * cos(a1))
                val vD2 = Vector3D(discX, center.y + discRadius * sin(a2), center.z + discRadius * cos(a2))
                if (isLeft) {
                    addTriangle(polygons, vDiscCenter, vD1, vD2, CarPart.WHEELS, 0xFF78909C)
                } else {
                    addTriangle(polygons, vDiscCenter, vD2, vD1, CarPart.WHEELS, 0xFF78909C)
                }
            }

            // Kẹp phanh Brembo Caliper (Top-left quadrant of brake disc)
            val calX = center.x + sign * 0.05f
            val cal1 = Vector3D(calX, center.y + 0.12f, center.z + 0.08f)
            val cal2 = Vector3D(calX, center.y + 0.19f, center.z + 0.02f)
            val cal3 = Vector3D(calX, center.y + 0.14f, center.z - 0.09f)
            val cal4 = Vector3D(calX, center.y + 0.07f, center.z - 0.04f)
            if (isLeft) {
                addQuad(polygons, cal1, cal2, cal3, cal4, CarPart.WHEELS, caliperColor, isEmissive = true)
            } else {
                addQuad(polygons, cal1, cal4, cal3, cal2, CarPart.WHEELS, caliperColor, isEmissive = true)
            }

            // Nan hoa mâm xe (Wheel Rim Spokes)
            val rimFaceX = center.x + sign * (tireWidth / 2 + 0.01f)
            val numSpokes = config.wheelRimStyle.spokes
            for (s in 0 until numSpokes) {
                val angle = Math.toRadians((s * (360.0 / numSpokes))).toFloat()
                val spokeWidthAngle = Math.toRadians(8.0).toFloat()

                val spY1 = center.y + rimRadius * sin(angle - spokeWidthAngle)
                val spZ1 = center.z + rimRadius * cos(angle - spokeWidthAngle)
                val spY2 = center.y + rimRadius * sin(angle + spokeWidthAngle)
                val spZ2 = center.z + rimRadius * cos(angle + spokeWidthAngle)

                val hubRadius = 0.05f
                val hubY1 = center.y + hubRadius * sin(angle - spokeWidthAngle)
                val hubZ1 = center.z + hubRadius * cos(angle - spokeWidthAngle)
                val hubY2 = center.y + hubRadius * sin(angle + spokeWidthAngle)
                val hubZ2 = center.z + hubRadius * cos(angle + spokeWidthAngle)

                val sp1 = Vector3D(rimFaceX, spY1, spZ1)
                val sp2 = Vector3D(rimFaceX, spY2, spZ2)
                val sp3 = Vector3D(rimFaceX, hubY2, hubZ2)
                val sp4 = Vector3D(rimFaceX, hubY1, hubZ1)

                val spokeColor = if (s % 2 == 0) rimColor else rimSpokeColor
                if (isLeft) {
                    addQuad(polygons, sp1, sp2, sp3, sp4, CarPart.WHEELS, spokeColor)
                } else {
                    addQuad(polygons, sp1, sp4, sp3, sp2, CarPart.WHEELS, spokeColor)
                }
            }

            // Nắp chụp logo trục bánh xe (Hub Center Cap)
            val cap1 = Vector3D(rimFaceX + sign * 0.01f, center.y + 0.04f, center.z + 0.04f)
            val cap2 = Vector3D(rimFaceX + sign * 0.01f, center.y + 0.04f, center.z - 0.04f)
            val cap3 = Vector3D(rimFaceX + sign * 0.01f, center.y - 0.04f, center.z - 0.04f)
            val cap4 = Vector3D(rimFaceX + sign * 0.01f, center.y - 0.04f, center.z + 0.04f)
            if (isLeft) {
                addQuad(polygons, cap1, cap2, cap3, cap4, CarPart.WHEELS, 0xFF00E5FF, isEmissive = true)
            } else {
                addQuad(polygons, cap1, cap4, cap3, cap2, CarPart.WHEELS, 0xFF00E5FF, isEmissive = true)
            }
        }
    }

    // -------------------------------------------------------------
    // 8. GHẾ NGỒI & KHOANG NỘI THẤT (INTERIOR CABIN)
    // -------------------------------------------------------------
    private fun buildInterior(polygons: MutableList<Polygon3D>, config: CarConfiguration) {
        val ambientHex = config.ambientColor.hexColor
        val seatColor = 0xFF212529
        val bolsterColor = 0xFF2C3034

        // Ghế tài xế (Driver Sports Seat)
        val dSeatX = -0.38f
        // Đệm ngồi ghế lái (Driver Seat Bottom Cushion)
        val scF1 = Vector3D(dSeatX - 0.18f, -0.05f, 0.35f)
        val scF2 = Vector3D(dSeatX + 0.18f, -0.05f, 0.35f)
        val scR2 = Vector3D(dSeatX + 0.18f, -0.08f, -0.10f)
        val scR1 = Vector3D(dSeatX - 0.18f, -0.08f, -0.10f)
        addQuad(polygons, scF1, scF2, scR2, scR1, CarPart.SEATS, seatColor)

        // Lưng ghế lái thể thao ngả lưng (Driver Seat Backrest - Dynamic reclineDegrees)
        val reclineRad = Math.toRadians(config.driverSeat.reclineDegrees.toDouble()).toFloat()
        val backHeight = 0.45f
        val backTopZ = -0.10f - backHeight * sin(reclineRad)
        val backTopY = -0.08f + backHeight * cos(reclineRad)

        val sbB1 = scR1
        val sbB2 = scR2
        val sbT2 = Vector3D(dSeatX + 0.16f, backTopY, backTopZ)
        val sbT1 = Vector3D(dSeatX - 0.16f, backTopY, backTopZ)
        addQuad(polygons, sbB1, sbB2, sbT2, sbT1, CarPart.SEATS, bolsterColor)

        // Tựa đầu ghế lái (Headrest)
        val hrB1 = sbT1
        val hrB2 = sbT2
        val hrT2 = Vector3D(dSeatX + 0.12f, backTopY + 0.12f, backTopZ - 0.04f)
        val hrT1 = Vector3D(dSeatX - 0.12f, backTopY + 0.12f, backTopZ - 0.04f)
        addQuad(polygons, hrB1, hrB2, hrT2, hrT1, CarPart.SEATS, seatColor)

        // Ghế phụ bên phải (Passenger Seat)
        val pSeatX = 0.38f
        val pscF1 = Vector3D(pSeatX - 0.18f, -0.05f, 0.35f)
        val pscF2 = Vector3D(pSeatX + 0.18f, -0.05f, 0.35f)
        val pscR2 = Vector3D(pSeatX + 0.18f, -0.08f, -0.10f)
        val pscR1 = Vector3D(pSeatX - 0.18f, -0.08f, -0.10f)
        addQuad(polygons, pscF1, pscF2, pscR2, pscR1, CarPart.SEATS, seatColor)

        val psbT2 = Vector3D(pSeatX + 0.16f, 0.32f, -0.22f)
        val psbT1 = Vector3D(pSeatX - 0.16f, 0.32f, -0.22f)
        addQuad(polygons, pscR1, pscR2, psbT2, psbT1, CarPart.SEATS, bolsterColor)

        // Bảng táp-lô hiển thị màn hình cong kỹ thuật số (Curved Digital Dashboard Screen)
        val dashL = Vector3D(-0.65f, 0.22f, 0.58f)
        val dashR = Vector3D(0.65f, 0.22f, 0.58f)
        val dashBotR = Vector3D(0.65f, 0.08f, 0.52f)
        val dashBotL = Vector3D(-0.65f, 0.08f, 0.52f)
        addQuad(polygons, dashL, dashR, dashBotR, dashBotL, CarPart.SEATS, 0xFF101316)

        // Dải đèn viền nội thất Ambient Light viền táp-lô
        addQuad(polygons, Vector3D(-0.65f, 0.23f, 0.58f), Vector3D(0.65f, 0.23f, 0.58f), Vector3D(0.65f, 0.21f, 0.58f), Vector3D(-0.65f, 0.21f, 0.58f), CarPart.SEATS, ambientHex, isEmissive = true)

        // Vô lăng thể thao vát đáy (Flat-Bottom Sports Steering Wheel)
        val stCenter = Vector3D(-0.38f, 0.18f, 0.42f)
        val stRadius = 0.12f
        for (i in 0 until 8) {
            val a1 = Math.toRadians((i * 45.0)).toFloat()
            val a2 = Math.toRadians(((i + 1) * 45.0)).toFloat()
            val st1 = Vector3D(stCenter.x + stRadius * sin(a1), stCenter.y + stRadius * cos(a1), stCenter.z)
            val st2 = Vector3D(stCenter.x + stRadius * sin(a2), stCenter.y + stRadius * cos(a2), stCenter.z)
            addQuad(polygons, st1, st2, Vector3D(st2.x, st2.y, st2.z + 0.02f), Vector3D(st1.x, st1.y, st1.z + 0.02f), CarPart.SEATS, 0xFF2A2E33)
        }
    }

    // -------------------------------------------------------------
    // 9. KHUNG GẦM & BỘ PIN ĐIỆN EV (CHASSIS & BATTERY PACK)
    // -------------------------------------------------------------
    private fun buildChassisAndBattery(polygons: MutableList<Polygon3D>, config: CarConfiguration) {
        val batY = -0.32f
        val batThickness = 0.08f
        val batHalfW = 0.72f
        val batFrontZ = 1.15f
        val batRearZ = -1.15f

        val batteryBaseColor = if (config.xrayMode) 0xFF00E5FF else 0xFF212529
        val batteryEmissive = config.xrayMode

        // Mặt trên bộ pin 100kWh Skateboard - Outward normal hướng lên (+Y)
        val bT1 = Vector3D(-batHalfW, batY + batThickness, batFrontZ)
        val bT2 = Vector3D(batHalfW, batY + batThickness, batFrontZ)
        val bT3 = Vector3D(batHalfW, batY + batThickness, batRearZ)
        val bT4 = Vector3D(-batHalfW, batY + batThickness, batRearZ)
        addQuad(polygons, bT1, bT2, bT3, bT4, CarPart.BATTERY_CHASSIS, batteryBaseColor, alpha = 0.9f, isEmissive = batteryEmissive)

        // Các cell pin ma trận bên trong (Cell matrix partition lines)
        for (i in -4..4 step 2) {
            val zLine = (i / 5f)
            val l1 = Vector3D(-batHalfW + 0.05f, batY + batThickness + 0.005f, zLine)
            val l2 = Vector3D(batHalfW - 0.05f, batY + batThickness + 0.005f, zLine)
            val l3 = Vector3D(batHalfW - 0.05f, batY + batThickness + 0.005f, zLine + 0.04f)
            val l4 = Vector3D(-batHalfW + 0.05f, batY + batThickness + 0.005f, zLine + 0.04f)
            addQuad(polygons, l1, l2, l3, l4, CarPart.BATTERY_CHASSIS, 0xFF00B0FF, isEmissive = true)
        }

        // Động cơ điện cầu trước (Front Electric Motor)
        val mF1 = Vector3D(-0.25f, batY + 0.12f, 1.45f)
        val mF2 = Vector3D(0.25f, batY + 0.12f, 1.45f)
        val mF3 = Vector3D(0.25f, batY - 0.02f, 1.15f)
        val mF4 = Vector3D(-0.25f, batY - 0.02f, 1.15f)
        addQuad(polygons, mF1, mF2, mF3, mF4, CarPart.BATTERY_CHASSIS, 0xFFFF6F00, isEmissive = config.xrayMode)

        // Động cơ điện cầu sau công suất lớn (Rear High-Power Electric Motor)
        val mR1 = Vector3D(-0.30f, batY + 0.15f, -1.15f)
        val mR2 = Vector3D(0.30f, batY + 0.15f, -1.15f)
        val mR3 = Vector3D(0.30f, batY - 0.02f, -1.48f)
        val mR4 = Vector3D(-0.30f, batY - 0.02f, -1.48f)
        addQuad(polygons, mR1, mR2, mR3, mR4, CarPart.BATTERY_CHASSIS, 0xFFFF3D00, isEmissive = config.xrayMode)
    }
}
