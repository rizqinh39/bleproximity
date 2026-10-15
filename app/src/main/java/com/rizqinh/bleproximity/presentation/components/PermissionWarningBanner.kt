package com.rizqinh.bleproximity.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizqinh.bleproximity.presentation.theme.DangerRed

/**
 * Banner component notifying the user if Bluetooth is OFF or permissions are denied.
 */
@Composable
fun PermissionWarningBanner(
    isBluetoothEnabled: Boolean,
    hasPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    onEnableBluetooth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isBluetoothEnabled && hasPermissions) return

    val message = if (!hasPermissions) {
        "Izin Bluetooth Scan & Lokasi diperlukan untuk mendeteksi perangkat BLE."
    } else {
        "Bluetooth dalam keadaan MATI. Aktifkan Bluetooth untuk memulai pemindaian."
    }

    val buttonText = if (!hasPermissions) {
        "Izinkan Akses"
    } else {
        "Aktifkan Bluetooth"
    }

    val onActionClick: () -> Unit = if (!hasPermissions) {
        onRequestPermissions
    } else {
        onEnableBluetooth
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = DangerRed.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Peringatan",
                tint = DangerRed,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Peringatan Akses",
                color = DangerRed,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = Color.DarkGray,
            fontSize = 13.sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onActionClick,
            colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(text = buttonText, color = Color.White)
        }
    }
}
