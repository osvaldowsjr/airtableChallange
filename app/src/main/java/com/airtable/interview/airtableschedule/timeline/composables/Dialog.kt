package com.airtable.interview.airtableschedule.timeline.composables

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airtable.interview.airtableschedule.timeline.EventUiModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventDialog(
    event: EventUiModel,
    minDate: Long,
    onDismiss: () -> Unit,
    onSave: (EventUiModel) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val baseDate = Date(minDate)

    fun offsetToDate(offset: Int) = Date(baseDate.time + offset * 24L * 60 * 60 * 1000)
    fun dateToOffset(date: Date) = ((date.time - baseDate.time) / (24L * 60 * 60 * 1000)).toInt()

    var name by remember { mutableStateOf(event.name) }
    var startOffset by remember { mutableIntStateOf(event.offsetDays) }
    var duration by remember { mutableIntStateOf(event.durationDays) }

    val startDate = offsetToDate(startOffset)
    val endDate = offsetToDate(startOffset + duration - 1)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Event") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Start Date: ${dateFormat.format(startDate)}")
                DatePickerButton(
                    initialDate = startDate,
                    onDateSelected = { newDate ->
                        val newOffset = max(0, dateToOffset(newDate))
                        if (newOffset <= startOffset + duration - 1) {
                            startOffset = newOffset
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("End Date: ${dateFormat.format(endDate)}")
                DatePickerButton(
                    initialDate = endDate,
                    onDateSelected = { newDate ->
                        val newOffset = max(startOffset, dateToOffset(newDate))
                        duration = newOffset - startOffset + 1
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(event.copy(
                    name = name,
                    offsetDays = startOffset,
                    durationDays = duration,
                    startDate = startDate,
                    endDate = endDate
                ))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DatePickerButton(initialDate: Date, onDateSelected: (Date) -> Unit) {
    val context = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                onDateSelected(cal.time)
                showPicker = false
            },
            Calendar.getInstance().apply { time = initialDate }.get(Calendar.YEAR),
            Calendar.getInstance().apply { time = initialDate }.get(Calendar.MONTH),
            Calendar.getInstance().apply { time = initialDate }.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Button(onClick = { showPicker = true }) {
        Text("Pick Date")
    }
}