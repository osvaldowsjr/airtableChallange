package com.airtable.interview.airtableschedule.timeline

fun daysBetween(start: Long, end: Long): Int {
    val diff = end - start
    return (diff / (1000 * 60 * 60 * 24)).toInt()
}