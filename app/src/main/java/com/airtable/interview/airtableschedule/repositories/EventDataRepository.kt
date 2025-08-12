package com.airtable.interview.airtableschedule.repositories

import com.airtable.interview.airtableschedule.models.Event
import com.airtable.interview.airtableschedule.models.SampleTimelineItems
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * A store for data related to events. Currently, this just returns sample data.
 */
interface EventDataRepository {
    fun getTimelineItems(): Flow<List<Event>>
    suspend fun updateEvents(events: List<Event>)
}

class EventDataRepositoryImpl : EventDataRepository {

    var currentList = SampleTimelineItems.timelineItems

    override fun getTimelineItems(): Flow<List<Event>> {
        return flowOf(currentList)
    }

    override suspend fun updateEvents(events: List<Event>) {
        currentList = events
    }
}
