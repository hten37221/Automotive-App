package com.android.car.automotiveapp.feature.home.car3d.camera

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.android.car.automotiveapp.core.model.CameraPose
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Trạng thái tương tác của hệ thống camera 3D (F07)
 */
enum class CameraInteractionMode {
    IDLE,
    USER_INTERACTING,
    ANIMATING_FOCUS,
    AUTO_ROTATING
}

/**
 * Quản lý trạng thái và chu trình điều khiển Camera 3D (F05, F06, F07, F08)
 * - Đảm bảo camera pose luôn tươi mới không bị stale trong các pointer callbacks (F05)
 * - Quản lý xung đột giữa Animation và cử chỉ người dùng (F07)
 * - Tự động xoay độc lập với tốc độ khung hình (Frame-rate independent delta time) (F08)
 */
@Stable
class CarCameraController(
    initialPose: CameraPose,
    private val scope: CoroutineScope,
    private val onPoseChanged: (CameraPose) -> Unit
) {
    val animAzimuth = Animatable(initialPose.azimuth)
    val animElevation = Animatable(initialPose.elevation)
    val animDistance = Animatable(initialPose.distance)
    val animTargetX = Animatable(initialPose.targetX)
    val animTargetY = Animatable(initialPose.targetY)
    val animTargetZ = Animatable(initialPose.targetZ)

    var mode by mutableStateOf(CameraInteractionMode.IDLE)
        private set

    private var activeAnimationJob: Job? = null

    val currentPose: CameraPose
        get() = CameraPose(
            azimuth = normalizeAngle(animAzimuth.value),
            elevation = animElevation.value.coerceIn(-15f, 85f),
            distance = animDistance.value.coerceIn(2.0f, 9.0f),
            targetX = animTargetX.value,
            targetY = animTargetY.value,
            targetZ = animTargetZ.value
        )

    fun onInteractionStart() {
        // Huỷ ngay lập tức animation đang chạy khi người dùng bắt đầu chạm/drag (F07)
        cancelActiveAnimation()
        mode = CameraInteractionMode.USER_INTERACTING
    }

    fun onInteractionEnd() {
        if (mode == CameraInteractionMode.USER_INTERACTING) {
            mode = CameraInteractionMode.IDLE
            onPoseChanged(currentPose)
        }
    }

    fun onDrag(dragAmountX: Float, dragAmountY: Float) {
        onInteractionStart()
        val newAzimuth = normalizeAngle(animAzimuth.value - dragAmountX * 0.45f)
        val newElevation = (animElevation.value + dragAmountY * 0.35f).coerceIn(-15f, 85f)
        scope.launch {
            animAzimuth.snapTo(newAzimuth)
            animElevation.snapTo(newElevation)
            onPoseChanged(currentPose)
        }
    }

    fun onZoom(zoomRatio: Float) {
        onInteractionStart()
        if (zoomRatio <= 0f) return
        val newDist = (animDistance.value / zoomRatio).coerceIn(2.0f, 9.0f)
        scope.launch {
            animDistance.snapTo(newDist)
            onPoseChanged(currentPose)
        }
    }

    fun animateTo(targetPose: CameraPose, durationMs: Int = 650) {
        cancelActiveAnimation()
        mode = CameraInteractionMode.ANIMATING_FOCUS

        activeAnimationJob = scope.launch {
            try {
                // Chuẩn hóa góc quay ngắn nhất (shortest path rotation)
                val currentAz = normalizeAngle(animAzimuth.value)
                var targetAz = normalizeAngle(targetPose.azimuth)
                val diff = targetAz - currentAz
                if (diff > 180f) {
                    targetAz -= 360f
                } else if (diff < -180f) {
                    targetAz += 360f
                }

                coroutineScope {
                    launch { animAzimuth.animateTo(targetAz, tween(durationMs, easing = FastOutSlowInEasing)) }
                    launch { animElevation.animateTo(targetPose.elevation, tween(durationMs, easing = FastOutSlowInEasing)) }
                    launch { animDistance.animateTo(targetPose.distance, tween(durationMs, easing = FastOutSlowInEasing)) }
                    launch { animTargetX.animateTo(targetPose.targetX, tween(durationMs, easing = FastOutSlowInEasing)) }
                    launch { animTargetY.animateTo(targetPose.targetY, tween(durationMs, easing = FastOutSlowInEasing)) }
                    launch { animTargetZ.animateTo(targetPose.targetZ, tween(durationMs, easing = FastOutSlowInEasing)) }
                }
                animAzimuth.snapTo(normalizeAngle(targetPose.azimuth))
                onPoseChanged(currentPose)
            } finally {
                if (mode == CameraInteractionMode.ANIMATING_FOCUS) {
                    mode = CameraInteractionMode.IDLE
                }
            }
        }
    }

    fun resetToDefault() {
        val defaultPose = CameraPose(
            azimuth = 45f,
            elevation = 20f,
            distance = 5.2f,
            targetX = 0f,
            targetY = 0f,
            targetZ = 0f
        )
        animateTo(defaultPose, 500)
    }

    /**
     * Tự động xoay xe độc lập với tốc độ khung hình (F08).
     * @param deltaSeconds Thời gian trôi qua giữa 2 khung hình (giây)
     * @param speedDegreesPerSec Tốc độ xoay (độ / giây)
     */
    fun stepAutoRotation(deltaSeconds: Float, speedDegreesPerSec: Float = 24f) {
        if (mode == CameraInteractionMode.USER_INTERACTING || mode == CameraInteractionMode.ANIMATING_FOCUS) {
            return
        }
        mode = CameraInteractionMode.AUTO_ROTATING
        val nextAzimuth = normalizeAngle(animAzimuth.value + speedDegreesPerSec * deltaSeconds)
        scope.launch {
            animAzimuth.snapTo(nextAzimuth)
        }
    }

    private fun cancelActiveAnimation() {
        activeAnimationJob?.cancel()
        activeAnimationJob = null
    }

    private fun normalizeAngle(angle: Float): Float {
        var a = angle % 360f
        if (a < 0f) a += 360f
        return a
    }
}

@Composable
fun rememberCarCameraController(
    cameraPose: CameraPose,
    onCameraPoseChanged: (CameraPose) -> Unit
): CarCameraController {
    val scope = rememberCoroutineScope()
    return remember {
        CarCameraController(
            initialPose = cameraPose,
            scope = scope,
            onPoseChanged = onCameraPoseChanged
        )
    }
}
