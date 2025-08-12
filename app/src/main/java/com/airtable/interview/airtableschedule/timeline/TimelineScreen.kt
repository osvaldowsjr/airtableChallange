package com.airtable.interview.airtableschedule.timeline

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airtable.interview.airtableschedule.timeline.composables.EditEventDialog
import com.airtable.interview.airtableschedule.timeline.composables.SwimlaneTimeline
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun TimelineScreen(viewModel: TimelineViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedEvent = viewModel.selectedEventForEdit

    Box {
        SwimlaneTimeline(uiState.lanes, uiState.minDate, uiState.totalDaysExact) {
            viewModel.openEditDialog(it)
        }

        if (selectedEvent != null) {
            EditEventDialog(
                event = selectedEvent,
                minDate = uiState.minDate,
                onDismiss = { viewModel.closeEditDialog() },
                onSave = { viewModel.saveEditedEvent(it) }
            )
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun DateAxis(
    minDate: Long,
    totalDays: Int,
    dayWidth: Dp,
) {
    val dateFormat = SimpleDateFormat("dd/MM")
    Row {
        for (day in 0 until totalDays) {
            val currentDate = Date(minDate + day * (1000L * 60 * 60 * 24))
            Box(
                modifier = Modifier
                    .width(dayWidth)
                    .height(40.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(dateFormat.format(currentDate))
            }
        }
    }
}
