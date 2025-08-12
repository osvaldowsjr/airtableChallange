package com.airtable.interview.airtableschedule.timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airtable.interview.airtableschedule.models.Event
import com.airtable.interview.airtableschedule.repositories.EventDataRepository
import com.airtable.interview.airtableschedule.repositories.EventDataRepositoryImpl
import java.util.Date
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import kotlin.math.max

class TimelineViewModel : ViewModel() {
    private val eventDataRepository: EventDataRepository = EventDataRepositoryImpl()

    var selectedEventForEdit by mutableStateOf<EventUiModel?>(null)
        private set

    private var minDate: Long = Long.MAX_VALUE
    private var maxDate: Long = Long.MIN_VALUE

    private val _uiState: MutableStateFlow<TimelineUiState> = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> get() = _uiState

    init {
        viewModelScope.launch {
            eventDataRepository
                .getTimelineItems()
                .collect { events ->
                    val newState = calculateTimelineState(events)
                    minDate = newState.minDate
                    _uiState.value = newState
                }
        }
    }

    fun openEditDialog(event: EventUiModel) {
        selectedEventForEdit = event
    }

    fun closeEditDialog() {
        selectedEventForEdit = null
    }

    fun saveEditedEvent(editedEvent: EventUiModel) = viewModelScope.launch {
        updateMinDateIfNeeded(editedEvent.startDate.time)

        val currentEvents = eventDataRepository.getTimelineItems().lastOrNull()
        val updatedEvents = currentEvents?.map {
            if (it.id == editedEvent.id) {
                it.copy(
                    name = editedEvent.name,
                    startDate = editedEvent.startDate,
                    endDate = editedEvent.endDate
                )
            } else it
        } ?: emptyList()

        val newState = calculateTimelineState(updatedEvents)
        _uiState.value = newState

        selectedEventForEdit = null
    }

    private fun calculateTimelineState(events: List<Event>): TimelineUiState {
        if (events.isEmpty()) return TimelineUiState()

        val sortedEvents = events.sortedBy { it.startDate.time }
        minDate = sortedEvents.minOf { it.startDate.time }
        maxDate = sortedEvents.maxOf { it.endDate.time }
        val totalDaysExact = daysBetween(minDate, maxDate) + 1

        val lanes = mutableListOf<MutableList<EventUiModel>>()
        sortedEvents.forEach { event ->
            val offsetDays = daysBetween(minDate, event.startDate.time)
            val durationDays = max(1, daysBetween(event.startDate.time, event.endDate.time) + 1)
            val uiEvent = EventUiModel(
                id = event.id,
                name = event.name,
                startDate = event.startDate,
                endDate = event.endDate,
                offsetDays = offsetDays,
                durationDays = durationDays
            )

            val lane = lanes.firstOrNull { laneEvents ->
                laneEvents.lastOrNull()?.let { lastEvent ->
                    (lastEvent.offsetDays + lastEvent.durationDays - 1) < uiEvent.offsetDays
                } ?: true
            }
            if (lane != null) {
                lane.add(uiEvent)
            } else {
                lanes.add(mutableListOf(uiEvent))
            }
        }

        return TimelineUiState(
            lanes = lanes,
            minDate = minDate,
            maxDate = maxDate,
            totalDaysExact = totalDaysExact
        )
    }

    private fun offsetToDate(offset: Int): Date {
        return Date(minDate + offset * 24L * 60 * 60 * 1000)
    }

    fun updateMinDateIfNeeded(newDate: Long) {
        if (newDate < minDate) {
            minDate = newDate

            val currentEvents = uiState.value.lanes.flatten().map { uiEvent ->
                Event(
                    id = uiEvent.id,
                    name = uiEvent.name,
                    startDate = offsetToDate(uiEvent.offsetDays),
                    endDate = offsetToDate(uiEvent.offsetDays + uiEvent.durationDays - 1)
                )
            }

            val updatedState = calculateTimelineState(currentEvents)
            _uiState.value = updatedState
        }
    }
}