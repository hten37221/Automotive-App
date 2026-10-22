package com.android.car.automotiveapp.feature.home.car3d.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HeatPump
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.car.automotiveapp.core.model.AmbientColor
import com.android.car.automotiveapp.core.model.CaliperColor
import com.android.car.automotiveapp.core.model.CarColor
import com.android.car.automotiveapp.core.model.CarConfiguration
import com.android.car.automotiveapp.core.model.CarPart
import com.android.car.automotiveapp.core.model.HeadlightMode
import com.android.car.automotiveapp.core.model.MassageMode
import com.android.car.automotiveapp.core.model.PaintFinish
import com.android.car.automotiveapp.core.model.WheelRimStyle

/**
 * Cửa sổ / Bảng điều khiển thông số chi tiết khi một bộ phận của xe được chọn
 */
@Composable
fun CarPartDetailSheet(
    selectedPart: CarPart?,
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit,
    onFocusPart: (CarPart) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = selectedPart != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        if (selectedPart == null) return@AnimatedVisibility

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Card(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF181D22).copy(alpha = 0.96f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF00E5FF).copy(alpha = 0.25f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                // Header: Thanh kéo & Tiêu đề bộ phận đang chọn
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.White.copy(alpha = 0.3f))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = getPartIcon(selectedPart),
                                    contentDescription = selectedPart.displayName,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedPart.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = selectedPart.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.65f),
                                maxLines = 1
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { onFocusPart(selectedPart) }) {
                            Icon(
                                imageVector = Icons.Default.CenterFocusStrong,
                                contentDescription = "Focus góc nhìn vào chi tiết",
                                tint = Color(0xFF00E5FF)
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Nội dung các tham số điều chỉnh chuyên biệt theo từng bộ phận
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedPart) {
                        CarPart.WHEELS -> WheelControlContent(config, onConfigChange)
                        CarPart.GLASS_WINDOWS -> GlassControlContent(config, onConfigChange)
                        CarPart.HEADLIGHTS -> HeadlightControlContent(config, onConfigChange)
                        CarPart.MIRRORS -> MirrorControlContent(config, onConfigChange)
                        CarPart.TRUNK -> TrunkControlContent(config, onConfigChange)
                        CarPart.DOORS -> DoorControlContent(config, onConfigChange)
                        CarPart.SEATS -> SeatControlContent(config, onConfigChange)
                        CarPart.BODY -> BodyControlContent(config, onConfigChange)
                        CarPart.BATTERY_CHASSIS -> ChassisControlContent(config, onConfigChange)
                    }
                }
            }
        }
    }
}
}

// -------------------------------------------------------------
// 1. ĐIỀU CHỈNH BÁNH XE & MÂM LỐP (WHEELS)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WheelControlContent(
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Kiểu vành mâm xe:",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WheelRimStyle.values().forEach { style ->
                FilterChip(
                    selected = config.wheelRimStyle == style,
                    onClick = { onConfigChange { it.copy(wheelRimStyle = style) } },
                    label = { Text(style.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00E5FF),
                        selectedLabelColor = Color(0xFF0B0D0F)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Màu kẹp phanh thể thao (Brake Calipers):",
            style = MaterialTheme.typography.labelMedium,
            color = Color.White.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CaliperColor.values().forEach { cal ->
                val isSelected = config.caliperColor == cal
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(cal.hexColor))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(onClickLabel = "Chọn kẹp phanh ${cal.displayName}") {
                            onConfigChange { it.copy(caliperColor = cal) }
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF222830)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Áp suất lốp (Cảm biến TPMS):",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Nhiệt độ: ${config.tirePressure.temperatureCelsius.toInt()}°C",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF00E676)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TireItem("Trước Trái", config.tirePressure.frontLeftPsi)
                    TireItem("Trước Phải", config.tirePressure.frontRightPsi)
                    TireItem("Sau Trái", config.tirePressure.rearLeftPsi)
                    TireItem("Sau Phải", config.tirePressure.rearRightPsi)
                }
            }
        }
    }
}

@Composable
private fun TireItem(label: String, psi: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
        Text(
            text = "${String.format("%.1f", psi)} PSI",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00E5FF)
        )
    }
}

// -------------------------------------------------------------
// 2. ĐIỀU CHỈNH KÍNH & CỬA SỔ (GLASS & WINDOWS)
// -------------------------------------------------------------
@Composable
private fun GlassControlContent(
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Độ tối màu kính cách nhiệt (Tint):", style = MaterialTheme.typography.labelMedium, color = Color.White)
            Text(text = "${config.windowTintPercent}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
        }
        Slider(
            value = config.windowTintPercent.toFloat(),
            onValueChange = { onConfigChange { cfg -> cfg.copy(windowTintPercent = it.toInt()) } },
            valueRange = 0f..90f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Hạ kính cửa xe điện:", style = MaterialTheme.typography.labelMedium, color = Color.White)
            Text(
                text = if (config.windowRollDownPercent == 0f) "Đóng kín" else "${(config.windowRollDownPercent * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00E5FF)
            )
        }
        Slider(
            value = config.windowRollDownPercent,
            onValueChange = { onConfigChange { cfg -> cfg.copy(windowRollDownPercent = it) } },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Sưởi kính lái trước chống mờ:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Switch(
                checked = config.frontDefroster,
                onCheckedChange = { onConfigChange { cfg -> cfg.copy(frontDefroster = it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF004D5A))
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Sưởi kính sau & sấy gương:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Switch(
                checked = config.rearDefroster,
                onCheckedChange = { onConfigChange { cfg -> cfg.copy(rearDefroster = it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF004D5A))
            )
        }
    }
}

// -------------------------------------------------------------
// 3. ĐIỀU CHỈNH HỆ THỐNG ĐÈN (HEADLIGHTS & LIGHTING)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HeadlightControlContent(
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Chế độ đèn chiếu sáng phía trước:", style = MaterialTheme.typography.labelMedium, color = Color.White)
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeadlightMode.values().forEach { mode ->
                FilterChip(
                    selected = config.headlightMode == mode,
                    onClick = { onConfigChange { it.copy(headlightMode = mode) } },
                    label = { Text(mode.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00E5FF),
                        selectedLabelColor = Color(0xFF0B0D0F)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Dải LED định vị ban ngày (DRL):", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Switch(
                checked = config.drlEnabled,
                onCheckedChange = { onConfigChange { cfg -> cfg.copy(drlEnabled = it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF004D5A))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Đèn sương mù dưới:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Switch(
                checked = config.fogLightsEnabled,
                onCheckedChange = { onConfigChange { cfg -> cfg.copy(fogLightsEnabled = it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF004D5A))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Đèn viền nội thất Ambient RGB:", style = MaterialTheme.typography.labelMedium, color = Color.White)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            AmbientColor.values().forEach { amb ->
                val isSelected = config.ambientColor == amb
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(amb.hexColor))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onConfigChange { it.copy(ambientColor = amb) } }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. ĐIỀU CHỈNH GƯƠNG CHIẾU HẬU (MIRRORS)
// -------------------------------------------------------------
@Composable
private fun MirrorControlContent(
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { onConfigChange { it.copy(mirrorsFolded = !it.mirrorsFolded) } },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (config.mirrorsFolded) Color(0xFFFFB300) else Color(0xFF00E5FF)
            )
        ) {
            Text(
                text = if (config.mirrorsFolded) "MỞ GƯƠNG CHIẾU HẬU" else "GẬP GƯƠNG CHIẾU HẬU ĐIỆN",
                color = Color(0xFF0B0D0F),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "Chỉnh góc nghiêng gương lái:", style = MaterialTheme.typography.labelMedium, color = Color.White)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { onConfigChange { it.copy(mirrorPitch = (it.mirrorPitch + 1f).coerceIn(-10f, 10f)) } }) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Lên", tint = Color(0xFF00E5FF))
                }
                Row {
                    IconButton(onClick = { onConfigChange { it.copy(mirrorYaw = (it.mirrorYaw - 1f).coerceIn(-10f, 10f)) } }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Trái", tint = Color(0xFF00E5FF))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(onClick = { onConfigChange { it.copy(mirrorYaw = (it.mirrorYaw + 1f).coerceIn(-10f, 10f)) } }) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Phải", tint = Color(0xFF00E5FF))
                    }
                }
                IconButton(onClick = { onConfigChange { it.copy(mirrorPitch = (it.mirrorPitch - 1f).coerceIn(-10f, 10f)) } }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Xuống", tint = Color(0xFF00E5FF))
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Cảnh báo điểm mù BSM:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Switch(
                checked = config.blindSpotMonitoring,
                onCheckedChange = { onConfigChange { cfg -> cfg.copy(blindSpotMonitoring = it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF004D5A))
            )
        }
    }
}

// -------------------------------------------------------------
// 5. ĐIỀU CHỈNH CỐP XE ĐIỆN (TRUNK)
// -------------------------------------------------------------
@Composable
private fun TrunkControlContent(
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit
) {
    val isTrunkOpen = config.trunkOpenPercent > 0.05f

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                onConfigChange {
                    it.copy(trunkOpenPercent = if (isTrunkOpen) 0f else 1f)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTrunkOpen) Color(0xFFFF5252) else Color(0xFF00E5FF)
            )
        ) {
            Text(
                text = if (isTrunkOpen) "ĐÓNG CỐP XE ĐIỆN" else "MỞ CỐP XE ĐIỆN THÔNG MINH",
                color = Color(0xFF0B0D0F),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Giới hạn chiều cao mở cốp:", style = MaterialTheme.typography.labelMedium, color = Color.White)
            Text(text = "${config.trunkMaxHeightPercent}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
        }
        Slider(
            value = config.trunkMaxHeightPercent.toFloat(),
            onValueChange = { onConfigChange { cfg -> cfg.copy(trunkMaxHeightPercent = it.toInt()) } },
            valueRange = 70f..100f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Cảm biến đá cốp rảnh tay:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Switch(
                checked = config.kickSensorEnabled,
                onCheckedChange = { onConfigChange { cfg -> cfg.copy(kickSensorEnabled = it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF004D5A))
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Đèn khoang hành lý cốp sau:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Switch(
                checked = config.cargoLightEnabled,
                onCheckedChange = { onConfigChange { cfg -> cfg.copy(cargoLightEnabled = it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF004D5A))
            )
        }
    }
}

// -------------------------------------------------------------
// 6. ĐIỀU CHỈNH CỬA XE & KHÓA (DOORS)
// -------------------------------------------------------------
@Composable
private fun DoorControlContent(
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit
) {
    val isDoorOpen = config.driverDoorOpenPercent > 0.05f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    onConfigChange {
                        it.copy(driverDoorOpenPercent = if (isDoorOpen) 0f else 1f)
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDoorOpen) Color(0xFFFF5252) else Color(0xFF00E5FF)
                )
            ) {
                Text(
                    text = if (isDoorOpen) "ĐÓNG CỬA" else "MỞ CỬA LÁI",
                    color = Color(0xFF0B0D0F),
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { onConfigChange { it.copy(doorsLocked = !it.doorsLocked) } },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (config.doorsLocked) Color(0xFF263238) else Color(0xFF00E676)
                )
            ) {
                Icon(
                    imageVector = if (config.doorsLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (config.doorsLocked) "ĐÃ KHÓA" else "MỞ KHÓA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Khóa an toàn trẻ em (Child Lock):", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Switch(
                checked = config.childSafetyLock,
                onCheckedChange = { onConfigChange { cfg -> cfg.copy(childSafetyLock = it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF004D5A))
            )
        }
    }
}

// -------------------------------------------------------------
// 7. ĐIỀU CHỈNH GHẾ NGỒI & KHOANG LÁI (SEATS)
// -------------------------------------------------------------
@Composable
private fun SeatControlContent(
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit
) {
    val seat = config.driverSeat

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Độ ngả lưng ghế lái:", style = MaterialTheme.typography.labelMedium, color = Color.White)
            Text(text = "${seat.reclineDegrees.toInt()}°", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
        }
        Slider(
            value = seat.reclineDegrees,
            onValueChange = { deg -> onConfigChange { it.copy(driverSeat = it.driverSeat.copy(reclineDegrees = deg)) } },
            valueRange = 0f..45f,
            colors = SliderDefaults.colors(thumbColor = Color(0xFF00E5FF), activeTrackColor = Color(0xFF00E5FF))
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HeatPump, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Sưởi ấm ghế lái:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(0, 1, 2, 3).forEach { lvl ->
                    FilterChip(
                        selected = seat.heatingLevel == lvl,
                        onClick = { onConfigChange { it.copy(driverSeat = it.driverSeat.copy(heatingLevel = lvl)) } },
                        label = { Text(if (lvl == 0) "Tắt" else "$lvl") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFF5252),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AcUnit, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Làm mát thông gió:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(0, 1, 2, 3).forEach { lvl ->
                    FilterChip(
                        selected = seat.coolingLevel == lvl,
                        onClick = { onConfigChange { it.copy(driverSeat = it.driverSeat.copy(coolingLevel = lvl)) } },
                        label = { Text(if (lvl == 0) "Tắt" else "$lvl") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF00E5FF),
                            selectedLabelColor = Color(0xFF0B0D0F)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Chế độ massage ghế:", style = MaterialTheme.typography.labelMedium, color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            MassageMode.values().forEach { mode ->
                FilterChip(
                    selected = seat.massageMode == mode,
                    onClick = { onConfigChange { it.copy(driverSeat = it.driverSeat.copy(massageMode = mode)) } },
                    label = { Text(mode.displayName.split(" ")[0]) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFD500F9),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 8. ĐIỀU CHỈNH THÂN VỎ & MÀU SƠN (BODY)
// -------------------------------------------------------------
@Composable
private fun BodyControlContent(
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Bảng màu sơn ngoại thất cao cấp:", style = MaterialTheme.typography.labelMedium, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CarColor.values().forEach { color ->
                val isSelected = config.bodyColor == color
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(color.hexColor))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) Color.White else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable(onClickLabel = "Chọn màu sơn ${color.displayName}") {
                            onConfigChange { it.copy(bodyColor = color) }
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(text = "Kiểu hoàn thiện bề mặt sơn:", style = MaterialTheme.typography.labelMedium, color = Color.White)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PaintFinish.values().forEach { finish ->
                FilterChip(
                    selected = config.paintFinish == finish,
                    onClick = { onConfigChange { it.copy(paintFinish = finish) } },
                    label = { Text(finish.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF00E5FF),
                        selectedLabelColor = Color(0xFF0B0D0F)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF222830)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Hệ số cản khí động học (Cd):", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                Text(text = "0.21 Cd (Siêu tối ưu)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
            }
        }
    }
}

// -------------------------------------------------------------
// 9. KHUNG GẦM & BỘ PIN EV (BATTERY & CHASSIS)
// -------------------------------------------------------------
@Composable
private fun ChassisControlContent(
    config: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Chế độ soi xuyên thấu khung gầm (X-Ray):", style = MaterialTheme.typography.bodyMedium, color = Color.White)
            Switch(
                checked = config.xrayMode,
                onCheckedChange = { onConfigChange { cfg -> cfg.copy(xrayMode = it) } },
                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00E5FF), checkedTrackColor = Color(0xFF004D5A))
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF222830)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Dung lượng pin 100 kWh:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    Text(text = "84% (540 km)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00E676))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Động cơ điện Dual Motor AWD:", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    Text(text = "650 HP / 850 Nm", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF00E5FF))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tình trạng sức khỏe pin (SOH):", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                    Text(text = "99% • 28°C", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
