package com.rizqinh.bleproximity.presentation.scanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizqinh.bleproximity.domain.model.BleDevice
import com.rizqinh.bleproximity.presentation.components.DeviceCard
import com.rizqinh.bleproximity.presentation.components.PermissionWarningBanner

/**
 * Main Scanner Dashboard Screen (Screen 1).
 *
 * MENTOR EXPLANATION FOR INTERVIEW:
 * 1. [Scaffold]: A Material 3 layout structure that automatically handles slot APIs (topBar, bottomBar, floatingActionButton).
 * 2. [State Hoisting]: Notice how ScannerScreen takes UI state ([ScannerUiState]) and passes event callbacks upward.
 *    The screen itself stores no mutable state, making it previewable, testable, and reusable.
 * 3. Recomposition: Jetpack Compose automatically re-executes parts of the composable tree when values emitted
 *    by state ([collectAsState]) change.
 * 4. [LazyColumn]: Equivalent to RecyclerView in XML layouts. Unlike Column which renders all items at once,
 *    LazyColumn renders only visible items on screen for high performance.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel,
    onNavigateToRadar: (BleDevice) -> Unit,
    onNavigateToHistory: () -> Unit,
    onRequestPermissions: () -> Unit,
    onEnableBluetooth: () -> Unit,
) {
    // Collecting StateFlow as Compose State. Changes trigger Recomposition.
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "BLE Proximity Tracker",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Manual Start / Stop Scan Toggle Button
                    IconButton(onClick = { viewModel.toggleScanning() }) {
                        Icon(
                            imageVector = if (uiState.isScanning) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = if (uiState.isScanning) "Stop Scan" else "Start Scan",
                            tint = if (uiState.isScanning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    // Navigation to History Log Screen
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Riwayat Perangkat",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Permission or Bluetooth Disabled Warning Banner
            PermissionWarningBanner(
                isBluetoothEnabled = uiState.isBluetoothEnabled,
                hasPermissions = uiState.hasPermissions,
                onRequestPermissions = onRequestPermissions,
                onEnableBluetooth = onEnableBluetooth
            )

            // Search Bar & Minimum RSSI Slider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari Perangkat (Nama / MAC)...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Cari")
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Minimum RSSI Threshold Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter RSSI Min:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = ">= ${uiState.minRssiThreshold} dBm",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Slider(
                    value = uiState.minRssiThreshold.toFloat(),
                    onValueChange = { viewModel.onRssiThresholdChanged(it.toInt()) },
                    valueRange = -100f..-30f,
                    steps = 14
                )
            }

            // Status bar showing active count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ditemukan: ${uiState.devices.size} Perangkat",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                if (uiState.isScanning) {
                    Text(
                        text = "● Pemindaian Aktif",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // LazyColumn rendering sorted list of BLE device items
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = uiState.devices,
                    key = { device -> device.macAddress }
                ) { device ->
                    DeviceCard(
                        device = device,
                        onClick = {
                            viewModel.saveDeviceToHistory(device)
                            onNavigateToRadar(device)
                        }
                    )
                }
            }
        }
    }
}
