package com.rizqinh.bleproximity

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.rizqinh.bleproximity.core.permission.BlePermissionHelper
import com.rizqinh.bleproximity.presentation.navigation.BleNavGraph
import com.rizqinh.bleproximity.presentation.scanner.ScannerViewModel
import com.rizqinh.bleproximity.presentation.theme.BLEProximityTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity entry point configured with Hilt [@AndroidEntryPoint].
 * Handles runtime BLE permission requests, Bluetooth enable intents, and App Lifecycle observer.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val scannerViewModel: ScannerViewModel by viewModels()

    // Permission launcher for BLE runtime permissions
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val hasPermissions = BlePermissionHelper.hasBlePermissions(this)
        scannerViewModel.updatePermissionStatus(hasPermissions)
    }

    // Bluetooth enable intent launcher
    private val bluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val hasPermissions = BlePermissionHelper.hasBlePermissions(this)
        scannerViewModel.updatePermissionStatus(hasPermissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initial permission check
        val initialPermissionsGranted = BlePermissionHelper.hasBlePermissions(this)
        scannerViewModel.updatePermissionStatus(initialPermissionsGranted)

        setContent {
            BLEProximityTheme {
                val navController = rememberNavController()

                // Lifecycle Observer: Pause scanning on ON_STOP, resume scanning on ON_START
                DisposableEffect(lifecycle) {
                    val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_START -> {
                                if (BlePermissionHelper.hasBlePermissions(this@MainActivity)) {
                                    scannerViewModel.startScanning()
                                }
                            }
                            Lifecycle.Event.ON_STOP -> {
                                scannerViewModel.stopScanning()
                            }
                            else -> {}
                        }
                    }
                    lifecycle.addObserver(observer)
                    onDispose {
                        lifecycle.removeObserver(observer)
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    BleNavGraph(
                        navController = navController,
                        scannerViewModel = scannerViewModel,
                        onRequestPermissions = {
                            permissionLauncher.launch(BlePermissionHelper.getRequiredPermissions())
                        },
                        onEnableBluetooth = {
                            bluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                        },
                    )
                }
            }
        }
    }
}
