package com.jatrenammapride.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jatrenammapride.data.model.Event
import com.jatrenammapride.ui.theme.*
import com.jatrenammapride.ui.theme.JatreNammaPrideTheme
import com.jatrenammapride.util.DateUtils

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOngoing = event.status.equals("Ongoing", ignoreCase = true)
    val borderStroke = if (isOngoing) {
        BorderStroke(2.dp, StatusOngoing)
    } else {
        null
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = borderStroke
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title and Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                StatusBadge(status = event.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time Range
            Text(
                text = DateUtils.formatTimeRange(event.startTime, event.endTime),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Location
            Text(
                text = event.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chip
            CategoryChip(category = event.category)
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val color = when (status.lowercase()) {
        "ongoing" -> StatusOngoing
        "upcoming" -> StatusUpcoming
        "completed" -> StatusCompleted
        else -> StatusCompleted
    }

    Badge(
        modifier = modifier,
        containerColor = color,
        contentColor = White
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun CategoryChip(
    category: String,
    modifier: Modifier = Modifier
) {
    val color = when (category.lowercase()) {
        "religious" -> CategoryReligious
        "cultural" -> CategoryCultural
        "sports" -> CategorySports
        "food" -> CategoryFood
        else -> Saffron
    }

    AssistChip(
        onClick = { },
        label = {
            Text(
                text = category,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        },
        modifier = modifier,
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    )
}

@Preview(showBackground = true)
@Composable
fun EventCardPreview() {
    JatreNammaPrideTheme(dynamicColor = false) {
        EventCard(
            event = Event(
                id = 1,
                title = "Rathotsava Procession",
                description = "The grand chariot procession through main streets",
                startTime = System.currentTimeMillis(),
                endTime = System.currentTimeMillis() + 7200000,
                location = "Main Temple Road",
                category = "Religious",
                status = "Ongoing"
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
