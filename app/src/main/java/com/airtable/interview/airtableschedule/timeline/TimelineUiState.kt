package com.airtable.interview.airtableschedule.timeline

import java.util.Date

data class TimelineUiState(
    val lanes: List<List<EventUiModel>> = emptyList(),
    val minDate: Long = 0L,
    val maxDate: Long = 0L,
    val totalDaysExact: Int = 0,
    val selectedEventUiModel: EventUiModel? = null
)

data class EventUiModel(
    val id: Int,
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val offsetDays: Int = 0,
    val durationDays: Int = 1
)
