package com.jatrenammapride.ui.map

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jatrenammapride.data.model.MapMarker
import com.jatrenammapride.ui.theme.DeepRed
import com.jatrenammapride.ui.theme.Gold
import com.jatrenammapride.ui.theme.Saffron

// Marker type colors
private val ParkingColor = Color(0xFF1565C0)    // Blue
private val FirstAidColor = Color(0xFFC62828)   // Red
private val StallColor = Color(0xFFEF6C00)      // Orange
private val EntryColor = Color(0xFF2E7D32)      // Green
private val ExitColor = Color(0xFF616161)        // Gray

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel()
) {
    val markers by viewModel.markers.collectAsState()
    val allMarkersForMap by viewModel.allMarkersFlow.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filters = listOf("All", "Parking", "FirstAid", "Stall", "Entry", "Exit")
    val filterLabels = mapOf(
        "All" to "All",
        "Parking" to "Parking",
        "FirstAid" to "First Aid",
        "Stall" to "Stalls",
        "Entry" to "Entry",
        "Exit" to "Exit"
    )

    Column(modifier = modifier.fillMaxSize()) {
        // === Header Banner ===
        VenueGuideBanner()

        // === Filter chips ===
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { viewModel.selectFilter(filter) },
                    label = { Text(text = filterLabels[filter] ?: filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // === Scrollable content: schematic map + location cards ===
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Schematic map section
            item {
                SchematicMapSection(
                    allMarkers = allMarkersForMap,
                    filteredMarkers = markers
                )
            }

            // Grouped location cards
            val grouped = markers.groupBy { it.type }
            grouped.forEach { (type, typeMarkers) ->
                item {
                    Text(
                        text = when (type) {
                            "Parking" -> "\uD83C\uDD7F\uFE0F Parking Zones"
                            "FirstAid" -> "\u2695\uFE0F Medical & First Aid"
                            "Stall" -> "\uD83C\uDFAA Stalls & Stages"
                            "Entry" -> "\u27A1\uFE0F Entry Points"
                            "Exit" -> "\u2B05\uFE0F Exit Points"
                            else -> type
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = getColorForType(type),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                items(typeMarkers) { marker ->
                    LocationCard(marker = marker)
                }
            }
        }
    }
}

// ────────────────────────────────────────────────────
// Header Banner
// ────────────────────────────────────────────────────
@Composable
private fun VenueGuideBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Saffron, DeepRed)
                ),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Map,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Jatre Venue Guide",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Navigate the festival grounds",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────
// Schematic Map
// ────────────────────────────────────────────────────
@Composable
private fun SchematicMapSection(
    allMarkers: List<MapMarker>,
    filteredMarkers: List<MapMarker>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Schematic Layout",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val textMeasurer = rememberTextMeasurer()
            val filteredIds = filteredMarkers.map { it.id }.toSet()
            val labelColor = MaterialTheme.colorScheme.onSurface
            val bgColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                // Background
                drawRect(color = bgColor)

                // Draw a subtle grid
                val gridColor = Color.Gray.copy(alpha = 0.12f)
                val gridSpacing = 40f
                var x = gridSpacing
                while (x < size.width) {
                    drawLine(gridColor, Offset(x, 0f), Offset(x, size.height))
                    x += gridSpacing
                }
                var y = gridSpacing
                while (y < size.height) {
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y))
                    y += gridSpacing
                }

                // Compute bounds for mapping lat/lng to canvas
                if (allMarkers.isEmpty()) return@Canvas

                val minLat = allMarkers.minOf { it.latitude }
                val maxLat = allMarkers.maxOf { it.latitude }
                val minLng = allMarkers.minOf { it.longitude }
                val maxLng = allMarkers.maxOf { it.longitude }

                val latRange = (maxLat - minLat).coerceAtLeast(0.0001)
                val lngRange = (maxLng - minLng).coerceAtLeast(0.0001)

                val padding = 40f

                fun mapToCanvas(marker: MapMarker): Offset {
                    val cx = padding + ((marker.longitude - minLng) / lngRange).toFloat() * (size.width - 2 * padding)
                    // Invert Y so north is up
                    val cy = padding + (1f - ((marker.latitude - minLat) / latRange).toFloat()) * (size.height - 2 * padding)
                    return Offset(cx, cy)
                }

                // Draw dashed boundary path connecting Entry → ... → Exit
                val dashedEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                val boundaryColor = Color.Gray.copy(alpha = 0.30f)
                val sortedPositions = allMarkers.sortedBy { it.longitude }.map { mapToCanvas(it) }
                for (i in 0 until sortedPositions.size - 1) {
                    drawLine(
                        color = boundaryColor,
                        start = sortedPositions[i],
                        end = sortedPositions[i + 1],
                        strokeWidth = 1.5f,
                        pathEffect = dashedEffect
                    )
                }

                // Draw each marker
                allMarkers.forEach { marker ->
                    val pos = mapToCanvas(marker)
                    val color = getColorForType(marker.type)
                    val isHighlighted = filteredIds.contains(marker.id)
                    val alpha = if (isHighlighted) 1f else 0.3f

                    // Outer ring for highlighted markers
                    if (isHighlighted) {
                        drawCircle(
                            color = color.copy(alpha = 0.2f),
                            radius = 20f,
                            center = pos
                        )
                    }

                    // Main dot
                    drawCircle(
                        color = color.copy(alpha = alpha),
                        radius = 10f,
                        center = pos
                    )

                    // White inner dot
                    drawCircle(
                        color = Color.White.copy(alpha = alpha),
                        radius = 4f,
                        center = pos
                    )

                    // Label
                    if (isHighlighted) {
                        val shortLabel = marker.label.take(12)
                        val textLayout = textMeasurer.measure(
                            text = shortLabel,
                            style = TextStyle(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = labelColor
                            )
                        )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(
                                pos.x - textLayout.size.width / 2f,
                                pos.y + 14f
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend
            MapLegend()
        }
    }
}

@Composable
private fun MapLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = ParkingColor, label = "Parking")
        LegendItem(color = FirstAidColor, label = "First Aid")
        LegendItem(color = StallColor, label = "Stall")
        LegendItem(color = EntryColor, label = "Entry")
        LegendItem(color = ExitColor, label = "Exit")
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}

// ────────────────────────────────────────────────────
// Location Card (improved)
// ────────────────────────────────────────────────────
@Composable
fun LocationCard(marker: MapMarker) {
    val context = LocalContext.current
    val typeColor = getColorForType(marker.type)
    val typeIcon = getIconForType(marker.type)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type icon in colored circle
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(typeColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = marker.type,
                        tint = typeColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = marker.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    marker.description?.let { desc ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatCoordinates(marker.latitude, marker.longitude),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Get Directions button
            Button(
                onClick = {
                    val uri = Uri.parse(
                        "google.navigation:q=${marker.latitude},${marker.longitude}&mode=w"
                    )
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    // Fall back to browser if Maps not installed
                    val browserUri = Uri.parse(
                        "https://www.google.com/maps/dir/?api=1&destination=${marker.latitude},${marker.longitude}&travelmode=walking"
                    )
                    val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                    try {
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        context.startActivity(browserIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = typeColor.copy(alpha = 0.12f),
                    contentColor = typeColor
                ),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Map,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Get Directions",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ────────────────────────────────────────────────────
// Helpers
// ────────────────────────────────────────────────────

private fun getColorForType(type: String): Color = when (type) {
    "Parking" -> ParkingColor
    "FirstAid" -> FirstAidColor
    "Stall" -> StallColor
    "Entry" -> EntryColor
    "Exit" -> ExitColor
    else -> Color.Gray
}

private fun getIconForType(type: String): ImageVector = when (type) {
    "Parking" -> Icons.Filled.LocalParking
    "FirstAid" -> Icons.Filled.LocalHospital
    "Stall" -> Icons.Filled.Store
    "Entry" -> Icons.Filled.Login
    "Exit" -> Icons.Filled.Logout
    else -> Icons.Filled.Map
}

private fun formatCoordinates(lat: Double, lng: Double): String {
    val latDir = if (lat >= 0) "N" else "S"
    val lngDir = if (lng >= 0) "E" else "W"
    val latDeg = Math.abs(lat).toInt()
    val latMin = ((Math.abs(lat) - latDeg) * 60)
    val lngDeg = Math.abs(lng).toInt()
    val lngMin = ((Math.abs(lng) - lngDeg) * 60)
    return String.format(
        "%d\u00B0%05.2f\u2032%s  %d\u00B0%05.2f\u2032%s",
        latDeg, latMin, latDir,
        lngDeg, lngMin, lngDir
    )
}
