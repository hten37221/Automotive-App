package com.android.car.automotiveapp.feature.home.car3d.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.car.automotiveapp.core.model.CameraAnglePreset
import com.android.car.automotiveapp.core.model.CameraPose
import com.android.car.automotiveapp.core.model.CarConfiguration
import com.android.car.automotiveapp.core.model.CarPart

/**
 * Màn hình Studio mô hình 3D xe ô tô tương tác toàn diện
 * Cho phép xem trước, xoay 360°, phóng to thu nhỏ, chạm vào các chi tiết xe,
 * chuyển góc nhìn camera mượt mà và tinh chỉnh thông số trực tiếp.
 */
@Composable
fun Car3DStudioScreen(
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit,
    onResetConfig: () -> Unit = {},
    selectedPart: CarPart? = null,
    onPartSelected: (CarPart?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var internalSelectedPart by remember { mutableStateOf(selectedPart) }
    var currentCameraPose by remember {
        mutableStateOf(selectedPart?.toRecommendedCameraPose() ?: CameraAnglePreset.ISOMETRIC.pose)
    }
    var showHotspotPins by remember { mutableStateOf(true) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedPart) {
        internalSelectedPart = selectedPart
        if (selectedPart != null) {
            currentCameraPose = selectedPart.toRecommendedCameraPose()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C1014))
    ) {
        // 1. KHUNG NHÌN 3D VIEWPORT TƯƠNG TÁC
        Car3DViewport(
            config = config,
            selectedPart = internalSelectedPart,
            cameraPose = currentCameraPose,
            onPartSelected = { part ->
                internalSelectedPart = part
                onPartSelected(part)
                // Khi chạm vào bộ phận, góc nhìn camera tự động focus vào chi tiết đó (F09)
                currentCameraPose = part.toRecommendedCameraPose()
            },
            onCameraPoseChanged = { currentCameraPose = it },
            showHotspots = showHotspotPins,
            modifier = Modifier.fillMaxSize()
        )

        // 2. THANH TIÊU ĐỀ TRÊN CÙNG (Top Bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AERO GT CONCEPT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF00E5FF).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "3D INTERACTIVE",
                            color = Color(0xFF00E5FF),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Text(
                    text = "Chạm chi tiết xe hoặc vuốt xoay 360° • Zoom 2 ngón tay",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.65f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Nút Khôi phục cài đặt gốc xe (F16)
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E2328).copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.4f), CircleShape)
                ) {
                    IconButton(onClick = { showResetConfirmDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Restore,
                            contentDescription = "Khôi phục cấu hình xe gốc",
                            tint = Color(0xFFFF5252),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Nút Reset góc nhìn camera
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1E2328).copy(alpha = 0.85f),
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f), CircleShape)
                ) {
                    IconButton(
                        onClick = {
                            internalSelectedPart = null
                            onPartSelected(null)
                            currentCameraPose = CameraAnglePreset.ISOMETRIC.pose
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset góc nhìn",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Hộp thoại xác nhận Đặt lại xe về mặc định (F16)
        if (showResetConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showResetConfirmDialog = false },
                containerColor = Color(0xFF1E2328),
                titleContentColor = Color.White,
                textContentColor = Color.White.copy(alpha = 0.8f),
                title = {
                    Text("Khôi phục cài đặt gốc của xe", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("Bạn có chắc chắn muốn đặt lại toàn bộ màu sơn, kiểu mâm xe, tư thế ghế, góc gương và các thông số khác về cấu hình xuất xưởng ban đầu không?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showResetConfirmDialog = false
                            onResetConfig()
                            internalSelectedPart = null
                            onPartSelected(null)
                            currentCameraPose = CameraAnglePreset.ISOMETRIC.pose
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                    ) {
                        Text("ĐẶT LẠI", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showResetConfirmDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F))
                    ) {
                        Text("HỦY BỎ", color = Color.White)
                    }
                }
            )
        }

        // 3. CÁC NÚT ĐIỀU KHIỂN CHẾ ĐỘ STUDIO NỔI (Floating Mode Bar Bên Phải)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nút Bật/Tắt Chế độ Ban đêm (Night / Day Studio Mode)
            StudioActionButton(
                icon = if (config.nightMode) Icons.Default.Brightness4 else Icons.Default.Brightness7,
                title = if (config.nightMode) "Ban đêm" else "Ban ngày",
                isActive = config.nightMode,
                onClick = { onConfigChange { it.copy(nightMode = !it.nightMode) } }
            )

            // Nút Soi khung gầm X-Ray (X-Ray Chassis Mode)
            StudioActionButton(
                icon = Icons.Default.Power,
                title = "X-Ray",
                isActive = config.xrayMode,
                onClick = { onConfigChange { it.copy(xrayMode = !it.xrayMode) } }
            )

            // Nút Xoay tự động 360° (Auto Turntable)
            StudioActionButton(
                icon = Icons.Default.Cached,
                title = "Xoay 360°",
                isActive = config.autoRotate,
                onClick = { onConfigChange { it.copy(autoRotate = !it.autoRotate) } }
            )

            // Nút Mở/Đóng tất cả cửa & cốp xe (Open / Close All)
            val isAllOpen = config.trunkOpenPercent > 0.5f || config.driverDoorOpenPercent > 0.5f
            StudioActionButton(
                icon = Icons.Default.MeetingRoom,
                title = if (isAllOpen) "Đóng hết" else "Mở hết",
                isActive = isAllOpen,
                onClick = {
                    onConfigChange {
                        it.copy(
                            trunkOpenPercent = if (isAllOpen) 0f else 1f,
                            driverDoorOpenPercent = if (isAllOpen) 0f else 1f
                        )
                    }
                }
            )

            // Nút Ẩn/Hiện Hotspots
            StudioActionButton(
                icon = if (showHotspotPins) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                title = if (showHotspotPins) "Hiện ghim" else "Ẩn ghim",
                isActive = showHotspotPins,
                onClick = { showHotspotPins = !showHotspotPins }
            )
        }

        // 4. KHU VỰC ĐIỀU KHIỂN DƯỚI ĐÁY: GÓC NHÌN & DANH SÁCH BỘ PHẬN
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // Thanh chuyển nhanh góc nhìn Camera Presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CameraAnglePreset.values().forEach { preset ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1E2328).copy(alpha = 0.85f),
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = Color(0xFF00E5FF).copy(alpha = 0.25f),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                internalSelectedPart = null
                                onPartSelected(null)
                                currentCameraPose = preset.pose
                            }
                    ) {
                        Text(
                            text = preset.title,
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Thanh chọn danh sách các bộ phận xe dạng ngang (Horizontal Part Selector Carousel)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CarPart.values().forEach { part ->
                    val isSelected = internalSelectedPart == part
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF191E23).copy(alpha = 0.92f),
                        modifier = Modifier
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) Color.White else Color(0xFF00E5FF).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                internalSelectedPart = part
                                onPartSelected(part)
                                currentCameraPose = part.toRecommendedCameraPose()
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = getPartIcon(part),
                                contentDescription = part.displayName,
                                tint = if (isSelected) Color(0xFF0B0D0F) else Color(0xFF00E5FF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = part.displayName,
                                color = if (isSelected) Color(0xFF0B0D0F) else Color.White,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // 5. CỬA SỔ ĐIỀU CHỈNH THÔNG SỐ CHI TIẾT KHI CHỌN BỘ PHẬN
            CarPartDetailSheet(
                selectedPart = internalSelectedPart,
                config = config,
                onConfigChange = onConfigChange,
                onFocusPart = { part ->
                    currentCameraPose = part.toRecommendedCameraPose()
                },
                onDismiss = {
                    internalSelectedPart = null
                    onPartSelected(null)
                }
            )
        }
    }
}

@Composable
private fun StudioActionButton(
    icon: ImageVector,
    title: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = if (isActive) Color(0xFF00E5FF) else Color(0xFF1E2328).copy(alpha = 0.88f),
        modifier = Modifier
            .size(44.dp)
            .border(
                width = 1.dp,
                color = if (isActive) Color.White else Color(0xFF00E5FF).copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) Color(0xFF0B0D0F) else Color(0xFF00E5FF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
