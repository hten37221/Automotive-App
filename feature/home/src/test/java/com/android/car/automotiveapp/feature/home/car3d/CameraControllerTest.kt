package com.android.car.automotiveapp.feature.home.car3d

import com.android.car.automotiveapp.core.model.CameraPose
import com.android.car.automotiveapp.core.model.CarPart
import com.android.car.automotiveapp.feature.home.car3d.camera.CameraInteractionMode
import com.android.car.automotiveapp.feature.home.car3d.camera.CarCameraController
import com.android.car.automotiveapp.feature.home.car3d.geometry.CarGeometryBuilder
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.math.sin

@OptIn(ExperimentalCoroutinesApi::class)
class CameraControllerTest {

    private val initialPose = CameraPose(
        azimuth = 45f,
        elevation = 20f,
        distance = 5.2f,
        targetX = 0f,
        targetY = 0f,
        targetZ = 0f
    )

    @Test
    fun onDrag_updatesAzimuthAndElevation() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)
        var lastEmittedPose: CameraPose? = null

        val controller = CarCameraController(
            initialPose = initialPose,
            scope = testScope,
            onPoseChanged = { lastEmittedPose = it }
        )

        controller.onDrag(dragAmountX = 10f, dragAmountY = 20f)
        testScope.advanceUntilIdle()

        assertThat(controller.mode).isEqualTo(CameraInteractionMode.USER_INTERACTING)
        // Azimuth ban đầu 45f - 10 * 0.45 = 40.5f
        assertThat(controller.currentPose.azimuth).isWithin(0.01f).of(40.5f)
        // Elevation ban đầu 20f + 20 * 0.35 = 27f
        assertThat(controller.currentPose.elevation).isWithin(0.01f).of(27f)
    }

    @Test
    fun onZoom_clampsWithinSafeBounds() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        val controller = CarCameraController(
            initialPose = initialPose,
            scope = testScope,
            onPoseChanged = {}
        )

        // Zoom in cực mạnh (tỉ lệ 10x)
        controller.onZoom(10f)
        testScope.advanceUntilIdle()
        assertThat(controller.currentPose.distance).isAtLeast(2.0f)

        // Zoom out cực xa (tỉ lệ 0.05x)
        controller.onZoom(0.05f)
        testScope.advanceUntilIdle()
        assertThat(controller.currentPose.distance).isAtMost(9.0f)
    }

    @Test
    fun onInteractionStart_cancelsActiveAnimation() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        val controller = CarCameraController(
            initialPose = initialPose,
            scope = testScope,
            onPoseChanged = {}
        )

        val targetPose = CameraPose(azimuth = 180f, elevation = 30f, distance = 4f)
        controller.animateTo(targetPose, durationMs = 1000)
        assertThat(controller.mode).isEqualTo(CameraInteractionMode.ANIMATING_FOCUS)

        // Khi người dùng chạm vào màn hình (start gesture)
        controller.onInteractionStart()
        assertThat(controller.mode).isEqualTo(CameraInteractionMode.USER_INTERACTING)
    }

    @Test
    fun stepAutoRotation_isDeltaTimeDependent() = runTest {
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val testScope = TestScope(testDispatcher)

        val controller = CarCameraController(
            initialPose = initialPose,
            scope = testScope,
            onPoseChanged = {}
        )

        // dt = 0.5 giây với tốc độ 20 độ/giây -> phải quay được đúng 10 độ
        controller.stepAutoRotation(deltaSeconds = 0.5f, speedDegreesPerSec = 20f)
        testScope.advanceUntilIdle()

        assertThat(controller.currentPose.azimuth).isWithin(0.01f).of(55f)
    }

    @Test
    fun cameraPresets_forLeftComponents_haveLeftEyePosition() {
        val wheelSpot = CarGeometryBuilder.hotspots.first { it.part == CarPart.WHEELS }
        val mirrorSpot = CarGeometryBuilder.hotspots.first { it.part == CarPart.MIRRORS }
        val doorSpot = CarGeometryBuilder.hotspots.first { it.part == CarPart.DOORS }
        val seatSpot = CarGeometryBuilder.hotspots.first { it.part == CarPart.SEATS }

        // Bánh trước trái ở X âm, camera phải đứng phía trái (sin(azimuth) < 0) để không bị thân xe che
        val wheelRad = Math.toRadians(wheelSpot.recommendedPose.azimuth.toDouble())
        assertThat(sin(wheelRad)).isLessThan(0.0)

        // Gương trái cũng ở X âm
        val mirrorRad = Math.toRadians(mirrorSpot.recommendedPose.azimuth.toDouble())
        assertThat(sin(mirrorRad)).isLessThan(0.0)

        // Cửa lái cũng ở X âm
        val doorRad = Math.toRadians(doorSpot.recommendedPose.azimuth.toDouble())
        assertThat(sin(doorRad)).isLessThan(0.0)

        // Ghế lái cũng ở cabin phía trái
        val seatRad = Math.toRadians(seatSpot.recommendedPose.azimuth.toDouble())
        assertThat(sin(seatRad)).isLessThan(0.0)
    }
}
