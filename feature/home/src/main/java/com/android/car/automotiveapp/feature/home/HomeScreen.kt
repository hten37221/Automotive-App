package com.android.car.automotiveapp.feature.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TireRepair
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.car.automotiveapp.core.designsystem.theme.AutomotiveAccent
import com.android.car.automotiveapp.core.designsystem.theme.AutomotiveDarkCard
import com.android.car.automotiveapp.core.designsystem.theme.AutomotiveDarkSurface
import com.android.car.automotiveapp.core.designsystem.theme.AutomotiveSuccess
import com.android.car.automotiveapp.core.model.CameraAnglePreset
import com.android.car.automotiveapp.core.model.CarConfiguration
import com.android.car.automotiveapp.core.model.CarPart
import com.android.car.automotiveapp.core.model.GreetingData
import com.android.car.automotiveapp.feature.home.car3d.ui.Car3DStudioScreen
import com.android.car.automotiveapp.feature.home.car3d.ui.Car3DViewport

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val carConfig by viewModel.carConfiguration.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        carConfig = carConfig,
        selectedTabIndex = viewModel.selectedTabIndex,
        studioSelectedPart = viewModel.studioSelectedPart,
        onTabSelected = viewModel::selectTab,
        onNavigateToStudio = viewModel::navigateToStudio,
        onStudioPartSelected = viewModel::selectStudioPart,
        onConfigChange = viewModel::updateCarConfiguration,
        onResetConfig = viewModel::resetCarConfiguration,
        onRefresh = viewModel::loadGreeting,
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    carConfig: CarConfiguration,
    selectedTabIndex: Int,
    studioSelectedPart: CarPart?,
    onTabSelected: (Int) -> Unit,
    onNavigateToStudio: (CarPart?) -> Unit,
    onStudioPartSelected: (CarPart?) -> Unit,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit,
    onResetConfig: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AutomotiveDarkSurface,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AutomotiveDarkSurface)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = AutomotiveDarkSurface,
                    contentColor = AutomotiveAccent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = AutomotiveAccent,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { onTabSelected(0) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "TỔNG QUAN",
                                    fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { onTabSelected(1) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "MÔ HÌNH XE 3D",
                                    fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Crossfade(
            targetState = selectedTabIndex,
            label = "tab_crossfade",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { tabIndex ->
            when (tabIndex) {
                0 -> {
                    // TAB 0: TỔNG QUAN (Dashboard với Hero Card 3D Preview)
                    when (uiState) {
                        is HomeUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = AutomotiveAccent)
                            }
                        }
                        is HomeUiState.Error -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Lỗi: ${uiState.message}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = onRefresh) {
                                    Text("Thử lại")
                                }
                            }
                        }
                        is HomeUiState.Success -> {
                            DashboardContent(
                                greeting = uiState.data,
                                carConfig = carConfig,
                                onConfigChange = onConfigChange,
                                onNavigateToStudio = onNavigateToStudio
                            )
                        }
                    }
                }
                1 -> {
                    // TAB 1: STUDIO 3D TƯƠNG TÁC ĐẦY ĐỦ (F13, F15, F16)
                    Car3DStudioScreen(
                        config = carConfig,
                        onConfigChange = onConfigChange,
                        onResetConfig = onResetConfig,
                        selectedPart = studioSelectedPart,
                        onPartSelected = onStudioPartSelected,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    greeting: GreetingData,
    carConfig: CarConfiguration,
    onConfigChange: ((CarConfiguration) -> CarConfiguration) -> Unit,
    onNavigateToStudio: (CarPart?) -> Unit,
    modifier: Modifier = Modifier
) {
    var previewCameraPose by remember {
        mutableStateOf(CameraAnglePreset.ISOMETRIC.pose.copy(distance = 5.4f))
    }
    var previewSelectedPart by remember { mutableStateOf<CarPart?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Badge tiêu đề
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AutomotiveAccent.copy(alpha = 0.15f),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "AERO GT SPORT CONCEPT • SYSTEM READY",
                    color = AutomotiveAccent,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }

        // 1. HERO CARD: XEM TRƯỚC VÀ TƯƠNG TÁC 3D TRỰC TIẾP TRÊN DASHBOARD
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = AutomotiveDarkCard),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .border(
                        width = 1.dp,
                        color = AutomotiveAccent.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Khung 3D Viewport tương tác
                    Car3DViewport(
                        config = carConfig,
                        selectedPart = previewSelectedPart,
                        cameraPose = previewCameraPose,
                        onPartSelected = { part ->
                            previewSelectedPart = part
                            onNavigateToStudio(part)
                        },
                        onCameraPoseChanged = { previewCameraPose = it },
                        showHotspots = true,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay nhãn thông tin góc trên
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mô hình xe 3D tương tác",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Vuốt để xoay 360° • Zoom 2 ngón tay",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF14171A).copy(alpha = 0.85f),
                            modifier = Modifier
                                .size(38.dp)
                                .clickable { onNavigateToStudio(null) }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.OpenInFull,
                                    contentDescription = "Mở rộng toàn màn hình",
                                    tint = AutomotiveAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Nút bấm lớn mở toàn bộ Studio 3D
                    Button(
                        onClick = { onNavigateToStudio(null) },
                        colors = ButtonDefaults.buttonColors(containerColor = AutomotiveAccent),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = Color(0xFF0B0D0F),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MỞ STUDIO 3D & TINH CHỈNH CHI TIẾT",
                            color = Color(0xFF0B0D0F),
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        // 2. CARD TRẠNG THÁI XE THỜI GIAN THỰC (Realtime Telemetry - F11, F13)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusMiniCard(
                    icon = Icons.Default.BatteryChargingFull,
                    iconTint = AutomotiveSuccess,
                    title = "DUNG LƯỢNG PIN",
                    value = "${carConfig.telemetry.batteryPercent}%",
                    sub = "${carConfig.telemetry.estimatedRangeKm} km khả dụng",
                    onClick = { onNavigateToStudio(CarPart.BATTERY_CHASSIS) },
                    modifier = Modifier.weight(1f)
                )
                StatusMiniCard(
                    icon = Icons.Default.TireRepair,
                    iconTint = AutomotiveAccent,
                    title = "ÁP SUẤT LỐP",
                    value = "${carConfig.tirePressure.frontLeftPsi.toInt()} PSI",
                    sub = "Nhiệt độ ${carConfig.tirePressure.temperatureCelsius.toInt()}°C",
                    onClick = { onNavigateToStudio(CarPart.WHEELS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusMiniCard(
                    icon = Icons.Default.WbSunny,
                    iconTint = Color(0xFFFFB300),
                    title = "HỆ THỐNG ĐÈN",
                    value = carConfig.headlightMode.displayName.split(" ")[0],
                    sub = if (carConfig.drlEnabled) "DRL Đang bật" else "DRL Tắt",
                    onClick = { onNavigateToStudio(CarPart.HEADLIGHTS) },
                    modifier = Modifier.weight(1f)
                )
                StatusMiniCard(
                    icon = Icons.Default.Lock,
                    iconTint = if (carConfig.doorsLocked) AutomotiveSuccess else Color(0xFFFF5252),
                    title = "KHÓA TRUNG TÂM",
                    value = if (carConfig.doorsLocked) "ĐÃ KHÓA" else "MỞ KHÓA",
                    sub = if (carConfig.childSafetyLock) "Khóa trẻ em: Bật" else "Khóa trẻ em: Tắt",
                    onClick = { onNavigateToStudio(CarPart.DOORS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. ARCHITECTURE & HỆ THỐNG
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tính năng & Kiến trúc hệ thống xe",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(greeting.architectureHighlights) { highlight ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AutomotiveDarkCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "✓ $highlight",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatusMiniCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    sub: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AutomotiveDarkCard),
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.65f)
            )
        }
    }
}
