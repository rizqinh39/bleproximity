package com.rizqinh.bleproximity.presentation.radar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizqinh.bleproximity.core.util.DateFormatter
import com.rizqinh.bleproximity.domain.model.BleDevice
import com.rizqinh.bleproximity.domain.model.SignalCategory
import com.rizqinh.bleproximity.presentation.components.SignalBadge
import com.rizqinh.bleproximity.presentation.scanner.ScannerViewModel

/**
 * Intensive Real-time Tracking Screen (Screen 2 - Radar View).
 *
 * MENTOR EXPLANATION FOR INTERVIEW:
 * 1. Canvas: Compose low-level drawing API. Allows custom 2D graphics directly replacing custom View subclasses in XML.
 * 2. [animateFloatAsState]: Automatically interpolates values smoothly when RSSI changes, animating radar rings
 *    without manual ValueAnimator code.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadarDetailScreen(
    macAddress: String,
    viewModel: ScannerViewModel,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    // Find target device by MAC address in live stream
    val targetDevice = uiState.rawDevices.find { it.macAddress == macAddress }
        ?: BleDevice(
            macAddress = macAddress,
            name = "Perangkat Terpilih",
            rssi = -999,
            estimatedDistanceMeters = -1.0,
            category = SignalCategory.LOST,
        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pelacakan Radar",
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Target Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = targetDevice.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = targetDevice.macAddress,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SignalBadge(category = targetDevice.category)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Concentric Circles Radar View with Canvas
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center,
            ) {
                ConcentricRadarCanvas(category = targetDevice.category, rssi = targetDevice.rssi)

                // Central Pulse Dot
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .background(color = targetDevice.category.color, shape = CircleShape),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Connection Stability Metrics Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Metrik Stabilitas Sinyal",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        MetricItem(label = "Min RSSI", value = "${targetDevice.minRssi} dBm")
                        MetricItem(
                            label = "Rata-rata",
                            value = "${kotlin.math.round(targetDevice.avgRssi * 10) / 10} dBm",
                        )
                        MetricItem(label = "Max RSSI", value = "${targetDevice.maxRssi} dBm")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        MetricItem(label = "Total Paket", value = targetDevice.packetCount.toString())
                        MetricItem(
                            label = "Estimasi Jarak",
                            value = if (targetDevice.estimatedDistanceMeters > 0) {
                                "${targetDevice.estimatedDistanceMeters} m"
                            } else {
                                "-"
                            },
                        )
                        MetricItem(
                            label = "Terakhir Dilihat",
                            value = DateFormatter.formatTimeString(targetDevice.lastSeenTimestamp),
                        )
                    }
                }
            }
        }
    }
}

/**
 * Concentric Circles Radar drawing 6 proximity zones with animated ring pulses.
 */
@Composable
fun ConcentricRadarCanvas(
    category: SignalCategory,
    rssi: Int,
    modifier: Modifier = Modifier,
) {
    val animatedColor by animateColorAsState(
        targetValue = category.color,
        animationSpec = tween(durationMillis = 500),
        label = "RadarColorAnimation",
    )

    // Calculate active ring scale factor based on RSSI (-100 to -30 dBm)
    val normalizedRssi = ((rssi.coerceIn(-100, -30) + 100) / 70f).coerceIn(0.1f, 1.0f)

    val activeRadiusFraction by animateFloatAsState(
        targetValue = normalizedRssi,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "RadarRadiusAnimation",
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val maxRadius = size.minDimension / 2f

        // Draw 5 background concentric grid rings
        for (i in 1..5) {
            val radius = maxRadius * (i / 5f)
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.4f),
                radius = radius,
                style = Stroke(width = 1.5.dp.toPx()),
            )
        }

        // Draw active animated pulse ring
        drawCircle(
            color = animatedColor.copy(alpha = 0.25f),
            radius = maxRadius * activeRadiusFraction,
        )

        drawCircle(
            color = animatedColor,
            radius = maxRadius * activeRadiusFraction,
            style = Stroke(width = 3.dp.toPx()),
        )
    }
}

@Composable
fun MetricItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}
