package com.android.car.automotiveapp.feature.home.car3d.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.android.car.automotiveapp.core.model.CameraPose
import com.android.car.automotiveapp.core.model.CarConfiguration
import com.android.car.automotiveapp.core.model.CarPart
import com.android.car.automotiveapp.core.model.Hotspot3D
import com.android.car.automotiveapp.core.model.Vector3D
import com.android.car.automotiveapp.feature.home.car3d.geometry.CarGeometryBuilder
import com.android.car.automotiveapp.feature.home.car3d.math.Matrix3D
import com.android.car.automotiveapp.feature.home.car3d.model.HingeType
import com.android.car.automotiveapp.feature.home.car3d.model.Polygon3D
import com.android.car.automotiveapp.feature.home.car3d.model.ProjectedPolygon
import com.android.car.automotiveapp.feature.home.car3d.model.ScreenPoint
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

data class ProjectedHotspot(
    val hotspot: Hotspot3D,
    val screenX: Float,
    val screenY: Float,
    val depth: Float,
    val isVisible: Boolean
)

object Car3DRenderer {

    private val keyLightDir = Vector3D(-0.4f, 1.2f, 0.7f).normalized()
    private val fillLightDir = Vector3D(0.8f, 0.5f, -0.6f).normalized()

    private data class ClipVertex(val x: Float, val y: Float, val z: Float, val w: Float)

    /**
     * Biến đổi và chiếu hình học 3D của xe lên mặt phẳng 2D
     */
    fun projectAndRender(
        drawScope: DrawScope,
        polygons: List<Polygon3D>,
        cameraPose: CameraPose,
        config: CarConfiguration,
        selectedPart: CarPart?,
        viewWidth: Float,
        viewHeight: Float
    ): List<ProjectedHotspot> {
        val aspect = viewWidth / max(1f, viewHeight)

        // 1. Tính toán tọa độ vị trí Camera (Eye Position)
        val azRad = Math.toRadians(cameraPose.azimuth.toDouble())
        val elRad = Math.toRadians(cameraPose.elevation.toDouble())
        val eyeX = (cameraPose.distance * cos(elRad) * sin(azRad)).toFloat() + cameraPose.targetX
        val eyeY = (cameraPose.distance * sin(elRad)).toFloat() + cameraPose.targetY
        val eyeZ = (cameraPose.distance * cos(elRad) * cos(azRad)).toFloat() + cameraPose.targetZ

        val eye = Vector3D(eyeX, eyeY, eyeZ)
        val center = Vector3D(cameraPose.targetX, cameraPose.targetY, cameraPose.targetZ)
        val up = Vector3D(0f, 1f, 0f)

        // Ma trận View & Projection
        val viewMatrix = Matrix3D.lookAt(eye, center, up)
        val projMatrix = Matrix3D.perspective(fovYDegrees = 42f, aspect = aspect, near = 0.4f, far = 50f)
        val vpMatrix = projMatrix.multiply(viewMatrix)

        // 2. Vẽ bối cảnh Studio: Nền sàn phản chiếu, bóng xe và chùm sáng đèn pha
        renderStudioFloor(drawScope, vpMatrix, eye, config, viewWidth, viewHeight)

        // 3. Biến đổi các mặt đa giác với Hinge Rotation (Cốp mở, Cửa mở)
        val projectedPolys = mutableListOf<ProjectedPolygon>()

        val trunkHingePivot = Vector3D(0f, 0.35f, -1.45f)
        val maxTrunkAngle = 55f * (config.trunkMaxHeightPercent / 100f).coerceIn(0.5f, 1.0f)
        val trunkOpenAngle = config.trunkOpenPercent * maxTrunkAngle

        val doorHingePivot = Vector3D(-0.88f, 0f, 0.70f)
        // Cửa tài xế (bên trái x < 0) mở bung ra ngoài (hướng dương theta)
        val doorOpenAngle = config.driverDoorOpenPercent * 45f

        for (poly in polygons) {
            val transformedVertices = poly.vertices.map { v ->
                when (poly.hingeType) {
                    HingeType.TRUNK_LID -> rotateAroundAxisX(v, trunkHingePivot, trunkOpenAngle)
                    HingeType.DRIVER_DOOR -> rotateAroundAxisY(v, doorHingePivot, doorOpenAngle)
                    else -> v
                }
            }

            // Tính pháp tuyến bề mặt sau biến đổi
            val v0 = transformedVertices[0]
            val v1 = transformedVertices[1]
            val v2 = transformedVertices[2]
            val normal = (v1 - v0).cross(v2 - v0).normalized()

            // Vector từ bề mặt tới camera
            val viewDir = (eye - v0).normalized()
            val dotView = normal.dot(viewDir)

            // Backface culling: nếu không phải 2 mặt và quay lưng về camera thì bỏ qua
            if (!poly.isDoubleSided && dotView <= -0.01f) {
                continue
            }

            // Cắt đa giác với near-plane (Sutherland-Hodgman) để chống vỡ hình học khi zoom sát
            val clippedVertices = clipPolygonNearPlane(transformedVertices, vpMatrix, nearW = 0.15f)
            if (clippedVertices.size < 3) continue

            val screenPoints = mutableListOf<ScreenPoint>()
            var totalDepth = 0f
            var valid = true

            for (cv in clippedVertices) {
                val invW = 1f / cv.w
                val ndcX = cv.x * invW
                val ndcY = cv.y * invW
                val ndcZ = cv.z * invW

                if (!ndcX.isFinite() || !ndcY.isFinite() || !ndcZ.isFinite()) {
                    valid = false
                    break
                }

                val screenX = (ndcX + 1f) * 0.5f * viewWidth
                val screenY = (1f - (ndcY + 1f) * 0.5f) * viewHeight
                screenPoints.add(ScreenPoint(screenX, screenY, ndcZ))
                totalDepth += ndcZ
            }

            if (!valid || screenPoints.size < 3) continue

            val avgDepth = totalDepth / screenPoints.size

            // Tính toán màu sắc với ánh sáng Directional + Specular
            val litColor = if (poly.isEmissive) {
                poly.emissiveColor
            } else {
                computeLighting(poly, normal, viewDir, config, selectedPart == poly.part)
            }

            projectedPolys.add(
                ProjectedPolygon(
                    screenPoints = screenPoints,
                    averageDepth = avgDepth,
                    color = litColor,
                    alpha = poly.alpha,
                    part = poly.part,
                    isEmissive = poly.isEmissive,
                    isSelected = (selectedPart == poly.part)
                )
            )
        }

        // 4. Sắp xếp đa giác theo chiều sâu (Painter's Algorithm Z-sorting)
        projectedPolys.sortByDescending { it.averageDepth }

        // 5. Vẽ tất cả các đa giác lên màn hình
        for (poly in projectedPolys) {
            drawPolygon(drawScope, poly)
        }

        // 6. Tính toán vị trí các điểm Hotspot 3D trên màn hình đồng bộ cùng chuyển động bản lề
        return computeProjectedHotspots(
            hotspots = CarGeometryBuilder.hotspots,
            vpMatrix = vpMatrix,
            doorHingePivot = doorHingePivot,
            doorOpenAngle = doorOpenAngle,
            trunkHingePivot = trunkHingePivot,
            trunkOpenAngle = trunkOpenAngle,
            width = viewWidth,
            height = viewHeight
        )
    }

    private fun renderStudioFloor(
        drawScope: DrawScope,
        vpMatrix: Matrix3D,
        eye: Vector3D,
        config: CarConfiguration,
        width: Float,
        height: Float
    ) {
        val isNight = config.nightMode
        val floorY = -0.38f

        // Vẽ bóng tiếp xúc xe (Radial Ground Shadow)
        val shadowCenter = projectPoint(Vector3D(0f, floorY, 0f), vpMatrix, width, height)
        val shadowFront = projectPoint(Vector3D(0f, floorY, 1.8f), vpMatrix, width, height)
        val shadowRear = projectPoint(Vector3D(0f, floorY, -1.8f), vpMatrix, width, height)
        val shadowLeft = projectPoint(Vector3D(-0.95f, floorY, 0f), vpMatrix, width, height)
        val shadowRight = projectPoint(Vector3D(0.95f, floorY, 0f), vpMatrix, width, height)

        if (shadowCenter != null && shadowFront != null && shadowRear != null && shadowLeft != null && shadowRight != null) {
            val shadowPath = Path().apply {
                moveTo(shadowLeft.x, shadowLeft.y)
                quadraticTo(shadowCenter.x - 20, shadowFront.y, shadowFront.x, shadowFront.y)
                quadraticTo(shadowCenter.x + 20, shadowFront.y, shadowRight.x, shadowRight.y)
                quadraticTo(shadowCenter.x + 20, shadowRear.y, shadowRear.x, shadowRear.y)
                quadraticTo(shadowCenter.x - 20, shadowRear.y, shadowLeft.x, shadowLeft.y)
                close()
            }
            drawScope.drawPath(
                path = shadowPath,
                color = Color.Black.copy(alpha = if (isNight) 0.65f else 0.40f)
            )
        }

        // Vẽ các vòng tròn showroom công nghệ trên sàn
        val ringColor = if (isNight) Color(0xFF00E5FF).copy(alpha = 0.12f) else Color(0xFF90A4AE).copy(alpha = 0.15f)
        val radii = listOf(1.5f, 2.5f, 3.6f)
        for (radius in radii) {
            val ringPath = Path()
            var first = true
            for (i in 0..36) {
                val rad = Math.toRadians((i * 10.0)).toFloat()
                val rx = radius * sin(rad)
                val rz = radius * cos(rad)
                val sp = projectPoint(Vector3D(rx, floorY, rz), vpMatrix, width, height)
                if (sp != null) {
                    if (first) {
                        ringPath.moveTo(sp.x, sp.y)
                        first = false
                    } else {
                        ringPath.lineTo(sp.x, sp.y)
                    }
                }
            }
            drawScope.drawPath(ringPath, ringColor, style = Stroke(width = 1.5f))
        }

        // Vẽ chùm sáng đèn pha rọi xuống sàn ở chế độ đèn bật (Headlight Cones Projection)
        if (config.headlightMode != com.android.car.automotiveapp.core.model.HeadlightMode.OFF) {
            val beamOriginL = projectPoint(Vector3D(-0.65f, 0.1f, 2.05f), vpMatrix, width, height)
            val beamOriginR = projectPoint(Vector3D(0.65f, 0.1f, 2.05f), vpMatrix, width, height)
            val beamTargetL1 = projectPoint(Vector3D(-1.6f, floorY, 6.5f), vpMatrix, width, height)
            val beamTargetL2 = projectPoint(Vector3D(-0.2f, floorY, 6.5f), vpMatrix, width, height)
            val beamTargetR1 = projectPoint(Vector3D(0.2f, floorY, 6.5f), vpMatrix, width, height)
            val beamTargetR2 = projectPoint(Vector3D(1.6f, floorY, 6.5f), vpMatrix, width, height)

            if (beamOriginL != null && beamTargetL1 != null && beamTargetL2 != null) {
                val beamPathL = Path().apply {
                    moveTo(beamOriginL.x, beamOriginL.y)
                    lineTo(beamTargetL1.x, beamTargetL1.y)
                    lineTo(beamTargetL2.x, beamTargetL2.y)
                    close()
                }
                drawScope.drawPath(
                    path = beamPathL,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE0F7FA).copy(alpha = if (isNight) 0.35f else 0.18f),
                            Color(0xFF00E5FF).copy(alpha = 0f)
                        ),
                        startY = beamOriginL.y,
                        endY = beamTargetL1.y
                    )
                )
            }

            if (beamOriginR != null && beamTargetR1 != null && beamTargetR2 != null) {
                val beamPathR = Path().apply {
                    moveTo(beamOriginR.x, beamOriginR.y)
                    lineTo(beamTargetR1.x, beamTargetR1.y)
                    lineTo(beamTargetR2.x, beamTargetR2.y)
                    close()
                }
                drawScope.drawPath(
                    path = beamPathR,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE0F7FA).copy(alpha = if (isNight) 0.35f else 0.18f),
                            Color(0xFF00E5FF).copy(alpha = 0f)
                        ),
                        startY = beamOriginR.y,
                        endY = beamTargetR2.y
                    )
                )
            }
        }
    }

    private fun computeLighting(
        poly: Polygon3D,
        normal: Vector3D,
        viewDir: Vector3D,
        config: CarConfiguration,
        isSelected: Boolean
    ): Long {
        val ambientBase = if (config.nightMode) 0.28f else 0.45f

        val diffKey = max(0f, normal.dot(keyLightDir))
        val diffFill = max(0f, normal.dot(fillLightDir)) * 0.4f
        val diffuse = ambientBase + diffKey * 0.55f + diffFill

        // Specular highlight (Phản xạ ánh kim)
        val reflectKey = (normal * (2f * normal.dot(keyLightDir)) - keyLightDir).normalized()
        val specFactor = max(0f, reflectKey.dot(viewDir)).pow(config.paintFinish.specularShininess)
        val specular = specFactor * config.paintFinish.metallicFactor * 0.45f

        val baseColor = poly.baseColor
        val rBase = ((baseColor shr 16) and 0xFF) / 255f
        val gBase = ((baseColor shr 8) and 0xFF) / 255f
        val bBase = (baseColor and 0xFF) / 255f

        var r = min(1f, rBase * diffuse + specular)
        var g = min(1f, gBase * diffuse + specular)
        var b = min(1f, bBase * diffuse + specular)

        // Nếu bộ phận đang được chọn: tăng cường độ sáng và phủ ánh sắc cyan nổi bật
        if (isSelected) {
            r = min(1f, r * 1.15f + 0.1f)
            g = min(1f, g * 1.25f + 0.2f)
            b = min(1f, b * 1.30f + 0.3f)
        }

        val rInt = (r * 255).toInt().coerceIn(0, 255)
        val gInt = (g * 255).toInt().coerceIn(0, 255)
        val bInt = (b * 255).toInt().coerceIn(0, 255)

        return (0xFFL shl 24) or (rInt.toLong() shl 16) or (gInt.toLong() shl 8) or bInt.toLong()
    }

    private fun drawPolygon(drawScope: DrawScope, poly: ProjectedPolygon) {
        val path = Path()
        val pts = poly.screenPoints
        path.moveTo(pts[0].x, pts[0].y)
        for (i in 1 until pts.size) {
            path.lineTo(pts[i].x, pts[i].y)
        }
        path.close()

        val color = Color(poly.color).copy(alpha = poly.alpha)
        drawScope.drawPath(path = path, color = color)

        // Vẽ viền sáng khi được chọn
        if (poly.isSelected) {
            drawScope.drawPath(
                path = path,
                color = Color(0xFF00E5FF).copy(alpha = 0.85f),
                style = Stroke(width = 2.5f)
            )
        }
    }

    private fun computeProjectedHotspots(
        hotspots: List<Hotspot3D>,
        vpMatrix: Matrix3D,
        doorHingePivot: Vector3D,
        doorOpenAngle: Float,
        trunkHingePivot: Vector3D,
        trunkOpenAngle: Float,
        width: Float,
        height: Float
    ): List<ProjectedHotspot> {
        val result = mutableListOf<ProjectedHotspot>()

        for (hs in hotspots) {
            val transformedPos = when (hs.part) {
                CarPart.DOORS -> rotateAroundAxisY(hs.worldPosition, doorHingePivot, doorOpenAngle)
                CarPart.TRUNK -> rotateAroundAxisX(hs.worldPosition, trunkHingePivot, trunkOpenAngle)
                else -> hs.worldPosition
            }
            val sp = projectPoint(transformedPos, vpMatrix, width, height)
            if (sp != null) {
                // Kiểm tra điểm có nằm trong màn hình
                val isVisible = sp.x in 20f..(width - 20f) && sp.y in 20f..(height - 20f)
                result.add(
                    ProjectedHotspot(
                        hotspot = hs,
                        screenX = sp.x,
                        screenY = sp.y,
                        depth = sp.z,
                        isVisible = isVisible
                    )
                )
            }
        }

        return result
    }

    private fun clipPolygonNearPlane(
        vertices: List<Vector3D>,
        vpMatrix: Matrix3D,
        nearW: Float = 0.15f
    ): List<ClipVertex> {
        val m = vpMatrix.m
        val clipVertices = ArrayList<ClipVertex>(vertices.size)
        for (v in vertices) {
            val x = m[0] * v.x + m[1] * v.y + m[2] * v.z + m[3]
            val y = m[4] * v.x + m[5] * v.y + m[6] * v.z + m[7]
            val z = m[8] * v.x + m[9] * v.y + m[10] * v.z + m[11]
            val w = m[12] * v.x + m[13] * v.y + m[14] * v.z + m[15]

            if (!x.isFinite() || !y.isFinite() || !z.isFinite() || !w.isFinite()) {
                return emptyList()
            }
            clipVertices.add(ClipVertex(x, y, z, w))
        }

        if (clipVertices.isEmpty()) return emptyList()

        val outputList = ArrayList<ClipVertex>()
        var prevVertex = clipVertices.last()
        var prevInside = prevVertex.w >= nearW

        for (currVertex in clipVertices) {
            val currInside = currVertex.w >= nearW
            if (currInside) {
                if (!prevInside) {
                    val t = (nearW - prevVertex.w) / (currVertex.w - prevVertex.w)
                    outputList.add(
                        ClipVertex(
                            x = prevVertex.x + t * (currVertex.x - prevVertex.x),
                            y = prevVertex.y + t * (currVertex.y - prevVertex.y),
                            z = prevVertex.z + t * (currVertex.z - prevVertex.z),
                            w = nearW
                        )
                    )
                }
                outputList.add(currVertex)
            } else if (prevInside) {
                val t = (nearW - prevVertex.w) / (currVertex.w - prevVertex.w)
                outputList.add(
                    ClipVertex(
                        x = prevVertex.x + t * (currVertex.x - prevVertex.x),
                        y = prevVertex.y + t * (currVertex.y - prevVertex.y),
                        z = prevVertex.z + t * (currVertex.z - prevVertex.z),
                        w = nearW
                    )
                )
            }
            prevVertex = currVertex
            prevInside = currInside
        }

        return outputList
    }

    private fun projectPoint(v: Vector3D, vpMatrix: Matrix3D, width: Float, height: Float): ScreenPoint? {
        val m = vpMatrix.m
        val x = m[0] * v.x + m[1] * v.y + m[2] * v.z + m[3]
        val y = m[4] * v.x + m[5] * v.y + m[6] * v.z + m[7]
        val z = m[8] * v.x + m[9] * v.y + m[10] * v.z + m[11]
        val w = m[12] * v.x + m[13] * v.y + m[14] * v.z + m[15]

        if (w <= 0.15f || !x.isFinite() || !y.isFinite() || !z.isFinite() || !w.isFinite()) return null

        val ndcX = x / w
        val ndcY = y / w
        val ndcZ = z / w

        val screenX = (ndcX + 1f) * 0.5f * width
        val screenY = (1f - (ndcY + 1f) * 0.5f) * height

        return ScreenPoint(screenX, screenY, ndcZ)
    }

    private fun rotateAroundAxisX(v: Vector3D, pivot: Vector3D, degrees: Float): Vector3D {
        if (degrees == 0f) return v
        val rad = Math.toRadians(degrees.toDouble()).toFloat()
        val c = cos(rad)
        val s = sin(rad)
        val relY = v.y - pivot.y
        val relZ = v.z - pivot.z
        val newY = relY * c - relZ * s + pivot.y
        val newZ = relY * s + relZ * c + pivot.z
        return Vector3D(v.x, newY, newZ)
    }

    private fun rotateAroundAxisY(v: Vector3D, pivot: Vector3D, degrees: Float): Vector3D {
        if (degrees == 0f) return v
        val rad = Math.toRadians(degrees.toDouble()).toFloat()
        val c = cos(rad)
        val s = sin(rad)
        val relX = v.x - pivot.x
        val relZ = v.z - pivot.z
        val newX = relX * c + relZ * s + pivot.x
        val newZ = -relX * s + relZ * c + pivot.z
        return Vector3D(newX, v.y, newZ)
    }
}
