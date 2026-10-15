package com.rizqinh.bleproximity.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizqinh.bleproximity.core.util.DateFormatter
import com.rizqinh.bleproximity.domain.model.BleDevice
import com.rizqinh.bleproximity.presentation.theme.CardSurface
import com.rizqinh.bleproximity.presentation.theme.TextPrimary
import com.rizqinh.bleproximity.presentation.theme.TextSecondary

/**
 * Reusable Card Component for displaying a single detected BLE Device item in a LazyColumn.
 *
 * MENTOR NOTE FOR INTERVIEW:
 * Compose Cards re-render (recompose) efficiently when properties inside [device] change.
 * [Modifier.clickable] handles tap gestures to navigate to the Radar Detail view.
 */
@Composable
fun DeviceCard(
    device: BleDevice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = device.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = device.macAddress,
                        fontSize = 13.sp,
                        color = TextSecondary,
                    )
                }

                // RSSI Value Tag
                Text(
                    text = "${device.rssi} dBm",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = device.category.color,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SignalBadge(category = device.category)

                Text(
                    text = DateFormatter.formatTimeString(device.lastSeenTimestamp),
                    fontSize = 12.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}
