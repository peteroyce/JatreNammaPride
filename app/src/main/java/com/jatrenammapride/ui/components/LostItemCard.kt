package com.jatrenammapride.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jatrenammapride.data.model.LostItem
import com.jatrenammapride.ui.theme.*

@Composable
fun LostItemCard(
    item: LostItem,
    onResolve: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Type Badge and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TypeBadge(type = item.type)
                    if (item.isResolved) {
                        Badge(
                            containerColor = StatusCompleted,
                            contentColor = White
                        ) {
                            Text(
                                text = "Resolved",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Photo
                if (item.photoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = item.photoUrl,
                        contentDescription = "Item photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Description
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Last Seen Location
                Text(
                    text = "Last seen: ${item.lastSeenLocation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Contact Info
                Text(
                    text = "Contact: ${item.contactInfo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Mark Resolved Button
                if (!item.isResolved) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onResolve,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusOngoing
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Mark Resolved")
                    }
                }
            }

            // Resolved Overlay
            if (item.isResolved) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            Color.White.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RESOLVED",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = StatusOngoing.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TypeBadge(
    type: String,
    modifier: Modifier = Modifier
) {
    val color = when (type.lowercase()) {
        "lost" -> DeepRed
        "found" -> StatusOngoing
        else -> Saffron
    }

    Badge(
        modifier = modifier,
        containerColor = color,
        contentColor = White
    ) {
        Text(
            text = type.uppercase(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LostItemCardPreview() {
    JatreNammaPrideTheme(dynamicColor = false) {
        LostItemCard(
            item = LostItem(
                id = 1,
                type = "Lost",
                description = "Blue wallet with ID cards and some cash",
                photoUrl = "",
                contactInfo = "9876543210",
                lastSeenLocation = "Near temple entrance",
                isResolved = false
            ),
            onResolve = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
