package com.rizqinh.bleproximity.core.ble

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observes system Bluetooth Adapter state (ON / OFF) in real-time via [BroadcastReceiver].
 */
@Singleton
class BluetoothStateReceiver @Inject constructor(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter?
) {

    /**
     * Emits true when Bluetooth is enabled, false otherwise.
     * Listens dynamically to [BluetoothAdapter.ACTION_STATE_CHANGED] broadcasts.
     */
    fun observeBluetoothState(): Flow<Boolean> = callbackFlow {
        // Emit initial state
        trySend(bluetoothAdapter?.isEnabled == true)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    val isEnabled = state == BluetoothAdapter.STATE_ON
                    trySend(isEnabled)
                }
            }
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(receiver, filter)

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {
                // Ignore unregister exceptions if receiver was not registered
            }
        }
    }

    /**
     * Returns instantaneous Bluetooth enabled status.
     */
    fun isEnabled(): Boolean = bluetoothAdapter?.isEnabled == true
}
