package com.rizqinh.bleproximity.core.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Helper utility for checking Android runtime BLE permissions across API levels.
 *
 * Android 12+ (API 31+) requires BLUETOOTH_SCAN and BLUETOOTH_CONNECT.
 * Android 11 and lower requires BLUETOOTH, BLUETOOTH_ADMIN, and Location permissions.
 */
object BlePermissionHelper {

    /**
     * Returns array of required runtime permissions based on the device's Android SDK level.
     */
    fun getRequiredPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        }
    }

    /**
     * Checks if all required BLE permissions have been granted by the user.
     *
     * @param context App context
     * @return true if all permissions in [getRequiredPermissions] are GRANTED.
     */
    fun hasBlePermissions(context: Context): Boolean {
        return getRequiredPermissions().all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
