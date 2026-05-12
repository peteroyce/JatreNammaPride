package com.jatrenammapride.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jatrenammapride.ui.theme.DarkBrown
import com.jatrenammapride.ui.theme.Gold
import com.jatrenammapride.ui.theme.JatreNammaPrideTheme
import com.jatrenammapride.ui.theme.Saffron
import com.jatrenammapride.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun CountdownTimer(
    targetTimeMillis: Long,
    modifier: Modifier = Modifier
) {
    var remainingMillis by remember { mutableLongStateOf(targetTimeMillis - System.currentTimeMillis()) }

    LaunchedEffect(targetTimeMillis) {
        while (true) {
            remainingMillis = targetTimeMillis - System.currentTimeMillis()
            if (remainingMillis <= 0) {
                remainingMillis = 0
                break
            }
            delay(1000L)
        }
    }

    if (remainingMillis <= 0) {
        // Event has started
        Box(
            modifier = modifier
                .background(
                    color = Saffron,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Event Started!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }
    } else {
        val totalSeconds = remainingMillis / 1000
        val days = totalSeconds / 86400
        val hours = (totalSeconds % 86400) / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        Row(
            modifier = modifier
                .background(
                    color = Gold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimeUnit(value = days, label = "Days")
            TimeSeparator()
            TimeUnit(value = hours, label = "Hours")
            TimeSeparator()
            TimeUnit(value = minutes, label = "Min")
            TimeSeparator()
            TimeUnit(value = seconds, label = "Sec")
        }
    }
}

@Composable
private fun TimeUnit(
    value: Long,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Saffron,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format("%02d", value),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 24.sp
                ),
                fontWeight = FontWeight.Bold,
                color = White,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = DarkBrown,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TimeSeparator(modifier: Modifier = Modifier) {
    Text(
        text = ":",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = Saffron,
        modifier = modifier.padding(bottom = 16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun CountdownTimerPreview() {
    JatreNammaPrideTheme(dynamicColor = false) {
        CountdownTimer(
            targetTimeMillis = System.currentTimeMillis() + 90061000L,
            modifier = Modifier.padding(16.dp)
        )
    }
}
