package com.rizqinh.bleproximity.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rizqinh.bleproximity.domain.model.SignalCategory

/**
 * Reusable Badge Component displaying signal category label and proximity distance.
 *
 * MENTOR NOTE FOR INTERVIEW:
 * In Compose, components are stateless functions. Notice how this Composable receives data ([category])
 * from its parent and renders the UI accordingly. This pattern is called State Hoisting.
 */
@Composable
fun SignalBadge(
    category: SignalCategory,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = category.color.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = "${category.label} (${category.estimatedDistanceRange})",
            color = category.color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
