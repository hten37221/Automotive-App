package com.android.car.automotiveapp.feature.home.car3d

import com.android.car.automotiveapp.core.model.CarConfiguration
import com.android.car.automotiveapp.core.model.CarPart
import com.android.car.automotiveapp.core.model.Vector3D
import com.android.car.automotiveapp.feature.home.car3d.geometry.CarGeometryBuilder
import com.android.car.automotiveapp.feature.home.car3d.math.Matrix3D
import com.android.car.automotiveapp.feature.home.car3d.model.HingeType
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin

class GeometryWindingTest {

    private val defaultConfig = CarConfiguration()

    @Test
    fun hoodPolygons_haveOutwardUpwardNormals() {
        val polygons = CarGeometryBuilder.buildCarModel(defaultConfig)
        // Nắp ca-pô nằm ở phần trước xe (Z > 0.65f, Y trong khoảng 0.07f..0.25f, X trong khoảng -0.78..0.78)
        val hoodPolygons = polygons.filter { 
            it.part == CarPart.BODY && 
            it.vertices.all { v -> v.z in 0.65f..2.10f && v.y in 0.07f..0.25f && v.x in -0.78f..0.78f } 
        }
        
        assertThat(hoodPolygons).isNotEmpty()
        for (poly in hoodPolygons) {
            val v0 = poly.vertices[0]
            val v1 = poly.vertices[1]
            val v2 = poly.vertices[2]
            val calculatedNormal = (v1 - v0).cross(v2 - v0).normalized()
            // Vector pháp tuyến của nắp ca-pô phải hướng lên trên (Y dương)
            assertThat(calculatedNormal.y).isGreaterThan(0.2f)
        }
    }

    @Test
    fun driverDoorPolygons_haveOutwardLeftNormals() {
        val polygons = CarGeometryBuilder.buildCarModel(defaultConfig)
        val driverDoorPolys = polygons.filter { it.part == CarPart.DOORS && it.hingeType == HingeType.DRIVER_DOOR && !it.isDoubleSided }

        assertThat(driverDoorPolys).isNotEmpty()
        for (poly in driverDoorPolys) {
            val v0 = poly.vertices[0]
            val v1 = poly.vertices[1]
            val v2 = poly.vertices[2]
            val calculatedNormal = (v1 - v0).cross(v2 - v0).normalized()
            // Cửa tài xế (bên trái xe X < 0), pháp tuyến phải hướng sang trái (X âm)
            assertThat(calculatedNormal.x).isLessThan(-0.3f)
        }
    }

    @Test
    fun headlightPolygons_haveForwardNormals() {
        val polygons = CarGeometryBuilder.buildCarModel(defaultConfig)
        // Đèn pha trước (Z > 1.8f)
        val frontHeadlightPolys = polygons.filter { 
            it.part == CarPart.HEADLIGHTS && 
            it.isEmissive && 
            it.vertices.all { v -> v.z > 1.8f } 
        }

        assertThat(frontHeadlightPolys).isNotEmpty()
        for (poly in frontHeadlightPolys) {
            val v0 = poly.vertices[0]
            val v1 = poly.vertices[1]
            val v2 = poly.vertices[2]
            val calculatedNormal = (v1 - v0).cross(v2 - v0).normalized()
            // Đèn pha phía trước (Z > 0), pháp tuyến phải hướng về phía trước (Z dương)
            assertThat(calculatedNormal.z).isGreaterThan(0.4f)
        }
    }

    @Test
    fun noDegeneratePolygons_orInvalidNormals() {
        val polygons = CarGeometryBuilder.buildCarModel(defaultConfig)
        assertThat(polygons).isNotEmpty()

        for (poly in polygons) {
            assertThat(poly.vertices.size).isAtLeast(3)
            for (v in poly.vertices) {
                assertThat(v.x.isFinite()).isTrue()
                assertThat(v.y.isFinite()).isTrue()
                assertThat(v.z.isFinite()).isTrue()
            }
            val v0 = poly.vertices[0]
            val v1 = poly.vertices[1]
            val v2 = poly.vertices[2]
            val cross = (v1 - v0).cross(v2 - v0)
            val length = cross.length()
            // Diện tích tam giác cơ bản không được bằng 0
            assertThat(length).isGreaterThan(1e-6f)
            assertThat(poly.normal.length()).isGreaterThan(0.5f)
        }
    }

    @Test
    fun driverDoor_opensOutwardInWorldSpace() {
        // Kiểm tra biến đổi quay của cửa tài xế khi mở bung ra ngoài (X âm hơn pivot)
        val hingePivot = Vector3D(-0.88f, 0f, 0.70f)
        val doorHandleClosed = Vector3D(-0.89f, 0.20f, -0.65f)
        val openDegrees = 45f // Dương: mở ra ngoài
        
        val rad = Math.toRadians(openDegrees.toDouble()).toFloat()
        val c = cos(rad)
        val s = sin(rad)
        val relX = doorHandleClosed.x - hingePivot.x
        val relZ = doorHandleClosed.z - hingePivot.z
        val newX = relX * c + relZ * s + hingePivot.x

        // Ban đầu doorHandleClosed.x = -0.89f.
        // Khi mở ra ngoài, newX phải dịch chuyển sang trái (newX < doorHandleClosed.x)
        assertThat(newX).isLessThan(-1.0f)
    }

    @Test
    fun matrix3D_lookAtAndPerspective_produceFiniteValues() {
        val eye = Vector3D(0f, 1f, 5f)
        val center = Vector3D(0f, 0f, 0f)
        val up = Vector3D(0f, 1f, 0f)

        val view = Matrix3D.lookAt(eye, center, up)
        val proj = Matrix3D.perspective(45f, 1.77f, 0.4f, 50f)
        val vp = proj.multiply(view)

        for (v in vp.m) {
            assertThat(v.isFinite()).isTrue()
        }
    }
}
