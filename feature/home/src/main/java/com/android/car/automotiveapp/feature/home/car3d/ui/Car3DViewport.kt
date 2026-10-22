package com.android.car.automotiveapp.feature.home.car3d.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import com.android.car.automotiveapp.feature.home.car3d.camera.CameraInteractionMode
import com.android.car.automotiveapp.feature.home.car3d.camera.rememberCarCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.CarRepair
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Highlight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.car.automotiveapp.core.model.CameraPose
import com.android.car.automotiveapp.core.model.CarConfiguration
import com.android.car.automotiveapp.core.model.CarPart
import com.android.car.automotiveapp.feature.home.car3d.geometry.CarGeometryBuilder
import com.android.car.automotiveapp.feature.home.car3d.renderer.Car3DRenderer
import com.android.car.automotiveapp.feature.home.car3d.renderer.ProjectedHotspot
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Khung nhìn hiển thị và tương tác trực tiếp với mô hình 3D của xe ô tô
 * Hỗ trợ: Xoay (Orbit Drag), Phóng to/thu nhỏ (Pinch Zoom), Chạm chọn chi tiết xe và Focus camera mượt mà
 */
@Composable
fun Car3DViewport(
    config: CarConfiguration,
    selectedPart: CarPart?,
    cameraPose: CameraPose,
    onPartSelected: (CarPart) -> Unit,
    onCameraPoseChanged: (CameraPose) -> Unit,
    modifier: Modifier = Modifier,
    showHotspots: Boolean = true
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Bộ điều khiển Camera 3D thống nhất (F05, F06, F07, F08)
    val cameraController = rememberCarCameraController(
        cameraPose = cameraPose,
        onCameraPoseChanged = onCameraPoseChanged
    )

    // Đồng bộ camera khi cameraPose từ bên ngoài thay đổi (ví dụ: nhấn preset hoặc chọn part)
    LaunchedEffect(cameraPose) {
        if (cameraController.mode != CameraInteractionMode.USER_INTERACTING) {
            cameraController.animateTo(cameraPose)
        }
    }

    // Chế độ Showroom Turntable tự động quay tròn 360 độ xe (Frame-rate independent F08)
    LaunchedEffect(config.autoRotate) {
        if (config.autoRotate) {
            var lastFrameNanos = 0L
            while (true) {
                withFrameNanos { frameTimeNanos ->
                    if (lastFrameNanos != 0L) {
                        val dtSeconds = (frameTimeNanos - lastFrameNanos) / 1_000_000_000f
                        cameraController.stepAutoRotation(
                            deltaSeconds = dtSeconds,
                            speedDegreesPerSec = config.rotationSpeed * 50f
                        )
                    }
                    lastFrameNanos = frameTimeNanos
                }
            }
        }
    }

    // Hiệu ứng nhịp đập pulse cho các điểm Hotspot 3D
    val infiniteTransition = rememberInfiniteTransition(label = "hotspot_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Tạo dựng danh sách mặt đa giác hình học xe (sử dụng GeometryCacheKey đầy đủ F04)
    val geometryKey = remember(
        config.bodyColor,
        config.paintFinish,
        config.wheelRimStyle,
        config.caliperColor,
        config.windowTintPercent,
        config.windowRollDownPercent,
        config.headlightMode,
        config.drlEnabled,
        config.fogLightsEnabled,
        config.hazardLightsEnabled,
        config.ambientColor,
        config.mirrorsFolded,
        config.mirrorPitch,
        config.mirrorYaw,
        config.frontDefroster,
        config.rearDefroster,
        config.driverSeat,
        config.xrayMode
    ) {
        GeometryCacheKey(
            bodyColor = config.bodyColor,
            paintFinish = config.paintFinish,
            wheelRimStyle = config.wheelRimStyle,
            caliperColor = config.caliperColor,
            windowTintPercent = config.windowTintPercent,
            windowRollDownPercent = config.windowRollDownPercent,
            headlightMode = config.headlightMode,
            drlEnabled = config.drlEnabled,
            fogLightsEnabled = config.fogLightsEnabled,
            hazardLightsEnabled = config.hazardLightsEnabled,
            ambientColor = config.ambientColor,
            mirrorsFolded = config.mirrorsFolded,
            mirrorPitch = config.mirrorPitch,
            mirrorYaw = config.mirrorYaw,
            frontDefroster = config.frontDefroster,
            rearDefroster = config.rearDefroster,
            driverSeat = config.driverSeat,
            xrayMode = config.xrayMode
        )
    }

    val polygons = remember(geometryKey) {
        CarGeometryBuilder.buildCarModel(config)
    }

    var projectedHotspots by remember { mutableStateOf<List<ProjectedHotspot>>(emptyList()) }

    val currentRenderPose = cameraController.currentPose

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(if (config.nightMode) Color(0xFF0C1014) else Color(0xFF161A1E))
            .pointerInput(Unit) {
                // Hợp nhất cử chỉ Pinch Zoom và Orbit Drag (F05, F06, F07)
                detectTransformGestures { _, pan, zoom, _ ->
                    if (zoom != 1f) {
                        cameraController.onZoom(zoom)
                    }
                    if (pan.x != 0f || pan.y != 0f) {
                        cameraController.onDrag(pan.x, pan.y)
                    }
                }
            }
            .pointerInput(Unit) {
                // Quản lý vòng đời cử chỉ tương tác để giải phóng và cập nhật trạng thái khi nhấc tay (F06)
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    cameraController.onInteractionStart()
                    do {
                        val event = awaitPointerEvent()
                    } while (event.changes.any { it.pressed })
                    cameraController.onInteractionEnd()
                }
            }
            .pointerInput(Unit) {
                // Nhấn đúp chuột hoặc chạm 2 lần để reset góc nhìn về Isometric mặc định
                detectTapGestures(
                    onDoubleTap = {
                        cameraController.resetToDefault()
                    },
                    onTap = { tapOffset ->
                        // Tìm kiếm xem cú chạm có gần điểm Hotspot nào không
                        val tappedHotspot = projectedHotspots.firstOrNull { hs ->
                            hs.isVisible && sqrt(
                                (hs.screenX - tapOffset.x) * (hs.screenX - tapOffset.x) +
                                        (hs.screenY - tapOffset.y) * (hs.screenY - tapOffset.y)
                            ) < with(density) { 36.dp.toPx() }
                        }
                        if (tappedHotspot != null) {
                            onPartSelected(tappedHotspot.hotspot.part)
                        }
                    }
                )
            }
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()

        // 1. Vẽ Canvas 3D của Xe
        Canvas(modifier = Modifier.fillMaxSize()) {
            projectedHotspots = Car3DRenderer.projectAndRender(
                drawScope = this,
                polygons = polygons,
                cameraPose = currentRenderPose,
                config = config,
                selectedPart = selectedPart,
                viewWidth = widthPx,
                viewHeight = heightPx
            )
        }

        // 2. Lớp hiển thị các điểm ghim tương tác 3D (Interactive Hotspot Pins)
        if (showHotspots) {
            projectedHotspots.filter { it.isVisible }.forEach { hs ->
                val isCurrentSelected = selectedPart == hs.hotspot.part
                val xDp = with(density) { hs.screenX.toDp() }
                val yDp = with(density) { hs.screenY.toDp() }

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                hs.screenX.roundToInt() - with(density) { 20.dp.roundToPx() },
                                hs.screenY.roundToInt() - with(density) { 20.dp.roundToPx() }
                            )
                        }
                        .size(40.dp)
                        .clickable { onPartSelected(hs.hotspot.part) },
                    contentAlignment = Alignment.Center
                ) {
                    // Vòng tròn hào quang nhấp nháy (Pulsing glowing ring)
                    Box(
                        modifier = Modifier
                            .size(if (isCurrentSelected) 38.dp else 28.dp)
                            .scale(if (isCurrentSelected) 1.2f else pulseScale)
                            .clip(CircleShape)
                            .background(
                                if (isCurrentSelected) Color(0xFF00E5FF).copy(alpha = 0.35f)
                                else Color(0xFF00E5FF).copy(alpha = 0.20f)
                            )
                    )

                    // Nút ghim trọng tâm (Hotspot center pill)
                    Surface(
                        shape = CircleShape,
                        color = if (isCurrentSelected) Color(0xFF00E5FF) else Color(0xFF1E2328),
                        modifier = Modifier
                            .size(24.dp)
                            .border(
                                width = 1.5.dp,
                                color = if (isCurrentSelected) Color.White else Color(0xFF00E5FF),
                                shape = CircleShape
                            )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = getPartIcon(hs.hotspot.part),
                                contentDescription = hs.hotspot.title,
                                tint = if (isCurrentSelected) Color(0xFF0B0D0F) else Color(0xFF00E5FF),
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Biểu tượng tương ứng cho từng bộ phận của xe
 */
fun getPartIcon(part: CarPart): ImageVector {
    return when (part) {
        CarPart.BODY -> Icons.Default.DirectionsCar
        CarPart.WHEELS -> Icons.Default.Adjust
        CarPart.HEADLIGHTS -> Icons.Default.Lightbulb
        CarPart.MIRRORS -> Icons.Default.Visibility
        CarPart.TRUNK -> Icons.Default.CarRepair
        CarPart.DOORS -> Icons.Default.MeetingRoom
        CarPart.GLASS_WINDOWS -> Icons.Default.Highlight
        CarPart.SEATS -> Icons.Default.Tune
        CarPart.BATTERY_CHASSIS -> Icons.Default.Power
    }
}

/**
 * Cache key đảm bảo tất cả các thuộc tính ảnh hưởng đến hình học và màu sắc 3D
 * đều được theo dõi chính xác (F04).
 */
private data class GeometryCacheKey(
    val bodyColor: com.android.car.automotiveapp.core.model.CarColor,
    val paintFinish: com.android.car.automotiveapp.core.model.PaintFinish,
    val wheelRimStyle: com.android.car.automotiveapp.core.model.WheelRimStyle,
    val caliperColor: com.android.car.automotiveapp.core.model.CaliperColor,
    val windowTintPercent: Int,
    val windowRollDownPercent: Float,
    val headlightMode: com.android.car.automotiveapp.core.model.HeadlightMode,
    val drlEnabled: Boolean,
    val fogLightsEnabled: Boolean,
    val hazardLightsEnabled: Boolean,
    val ambientColor: com.android.car.automotiveapp.core.model.AmbientColor,
    val mirrorsFolded: Boolean,
    val mirrorPitch: Float,
    val mirrorYaw: Float,
    val frontDefroster: Boolean,
    val rearDefroster: Boolean,
    val driverSeat: com.android.car.automotiveapp.core.model.SeatComfortState,
    val xrayMode: Boolean
)
