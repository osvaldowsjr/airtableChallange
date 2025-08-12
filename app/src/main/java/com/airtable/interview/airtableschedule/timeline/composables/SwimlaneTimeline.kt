package com.airtable.interview.airtableschedule.timeline.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airtable.interview.airtableschedule.timeline.DateAxis
import com.airtable.interview.airtableschedule.timeline.EventUiModel

@Composable
fun SwimlaneTimeline(
    lanes: List<List<EventUiModel>>,
    minDate: Long,
    totalDays: Int,
    openEditDialog: (EventUiModel) -> Unit,
) {
    if (lanes.isEmpty()) return

    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()
    val dayWidth = 80.dp

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScrollState)
                .background(Color.LightGray)
        ) {
            DateAxis(minDate, totalDays, dayWidth)
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
        ) {
            Box(
                modifier = Modifier
                    .horizontalScroll(horizontalScrollState)
                    .fillMaxWidth()
            ) {
                val laneHeight = 40.dp
                val spacing = 8.dp
                val totalHeight = (laneHeight + spacing) * lanes.size

                DayDividers(
                    totalDays = totalDays,
                    dayWidth = dayWidth,
                    height = totalHeight
                )

                Column {
                    lanes.forEachIndexed { index, laneEvents ->
                        LaneRow(laneEvents, minDate, dayWidth, index, totalDays) {
                            openEditDialog(it)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DayDividers(
    totalDays: Int,
    dayWidth: Dp,
    height: Dp,
    lineColor: Color = Color.LightGray,
    lineWidth: Float = 1f,
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val pxDayWidth = dayWidth.toPx()
        for (dayIndex in 0..totalDays) {
            val x = dayIndex * pxDayWidth
            drawLine(
                color = lineColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = lineWidth
            )
        }
    }
}
