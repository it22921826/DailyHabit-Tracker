package com.dailyhabbittracker.tracker.model

import java.time.LocalDate

/** Basic habit model */
data class Habit(
    val id: Long = System.currentTimeMillis(),
    var name: String,
    val completedDays: MutableSet<Long> = mutableSetOf() // epochDay values
) {
    fun isCompletedToday(): Boolean = completedDays.contains(LocalDate.now().toEpochDay())
    fun toggleToday() {
        val today = LocalDate.now().toEpochDay()
        if (completedDays.contains(today)) completedDays.remove(today) else completedDays.add(today)
    }
}

/** Mood entry with emoji and optional note */
data class MoodEntry(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val emoji: String,
    val note: String? = null
)

/** Hydration daily record */
data class HydrationState(
    val dateEpoch: Long = LocalDate.now().toEpochDay(),
    var goalMl: Int = 2000,
    var consumedMl: Int = 0
)

