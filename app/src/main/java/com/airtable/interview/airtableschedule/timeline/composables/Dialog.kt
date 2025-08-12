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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.airtable.interview.airtableschedule.timeline.EventUiModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max

fun offsetToDate(baseDate: Date, offset: Int): Date = Date(baseDate.time + offset * 24L * 60 * 60 * 1000)
fun dateToOffset(baseDate: Date, date: Date): Int = ((date.time - baseDate.time) / (24L * 60 * 60 * 1000)).toInt()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventDialog(
    event: EventUiModel,
    minDate: Long,
    onDismiss: () -> Unit,
    onSave: (EventUiModel) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    val baseDate = remember { Date(minDate) }

    var name by rememberSaveable { mutableStateOf(event.name) }
    var startOffset by rememberSaveable { mutableIntStateOf(event.offsetDays) }
    var duration by rememberSaveable { mutableIntStateOf(event.durationDays) }

    val startDate = remember(startOffset) { offsetToDate(baseDate, startOffset) }
    val endDate = remember(startOffset, duration) { offsetToDate(baseDate, startOffset + duration - 1) }

    val isNameValid = name.isNotBlank()
    val isDurationValid = duration > 0
    val isSaveEnabled = isNameValid && isDurationValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Event") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    isError = !isNameValid
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text("Start Date: ${dateFormat.format(startDate)}")
                DatePickerButton(
                    initialDate = startDate,
                    onDateSelected = { newDate ->
                        val newOffset = max(0, dateToOffset(baseDate, newDate))
                        if (newOffset <= startOffset + duration - 1) {
                            startOffset = newOffset
                        }
                    },
                    contentDescription = "Pick Start Date"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("End Date: ${dateFormat.format(endDate)}")
                DatePickerButton(
                    initialDate = endDate,
                    onDateSelected = { newDate ->
                        val newOffset = max(startOffset, dateToOffset(baseDate, newDate))
                        duration = newOffset - startOffset + 1
                    },
                    contentDescription = "Pick End Date"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(event.copy(
                        name = name,
                        offsetDays = startOffset,
                        durationDays = duration,
                        startDate = startDate,
                        endDate = endDate
                    ))
                },
                enabled = isSaveEnabled
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DatePickerButton(
    initialDate: Date,
    onDateSelected: (Date) -> Unit,
    contentDescription: String
) {
    val context = LocalContext.current
    var showPicker by remember { mutableStateOf(false) }

    LaunchedEffect(showPicker) {
        if (showPicker) {
            val cal = Calendar.getInstance().apply { time = initialDate }
            DatePickerDialog(
                context,
                { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                    val selectedCal = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    onDateSelected(selectedCal.time)
                    showPicker = false
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    Button(
        onClick = { showPicker = true },
        modifier = Modifier.semantics { this.contentDescription = contentDescription }
    ) {
        Text("Pick Date")
    }
}