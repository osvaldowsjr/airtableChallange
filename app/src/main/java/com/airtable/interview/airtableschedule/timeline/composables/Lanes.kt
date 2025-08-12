package com.airtable.interview.airtableschedule.timeline.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.airtable.interview.airtableschedule.timeline.EventUiModel
import com.airtable.interview.airtableschedule.timeline.daysBetween
import kotlin.math.max

@Composable
fun LaneRow(
    events: List<EventUiModel>,
    minDate: Long,
    dayWidth: Dp,
    laneIndex: Int,
    totalDays: Int,
    onClickEventUiModel: (EventUiModel) -> Unit,
) {
    var currentOffsetDays = 0
    Row {
        events.forEach { event ->
            val startOffsetDays = daysBetween(minDate, event.startDate.time)
            val eventDays = max(1, daysBetween(event.startDate.time, event.endDate.time) + 1)

            Spacer(modifier = Modifier.width((startOffsetDays - currentOffsetDays) * dayWidth))

            Box(
                modifier = Modifier
                    .width(eventDays * dayWidth)
                    .height(40.dp)
                    .background(colorForLane(laneIndex), RoundedCornerShape(8.dp))
                    .clickable { onClickEventUiModel(event) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = event.name,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            currentOffsetDays = startOffsetDays + eventDays
        }

        if (currentOffsetDays < totalDays) {
            Spacer(modifier = Modifier.width((totalDays - currentOffsetDays) * dayWidth))
        }
    }
}

fun colorForLane(index: Int): Color {
    val colors = listOf(
        Color(0xFF1E88E5),
        Color(0xFFD81B60),
        Color(0xFF43A047),
        Color(0xFFF4511E),
        Color(0xFF6A1B9A)
    )
    return colors[index % colors.size]
}