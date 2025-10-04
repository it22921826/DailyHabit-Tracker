package com.dailyhabbittracker.tracker.ui.shared

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailyhabbittracker.tracker.data.WellnessRepository
import com.dailyhabbittracker.tracker.model.Habit
import com.dailyhabbittracker.tracker.model.HydrationState
import com.dailyhabbittracker.tracker.model.MoodEntry

class MainViewModel(private val repo: WellnessRepository): ViewModel() {
    val habits: LiveData<List<Habit>> = repo.habits
    val moods: LiveData<List<MoodEntry>> = repo.moods
    val hydration: LiveData<HydrationState> = repo.hydration

    fun addHabit(name: String) = repo.addHabit(name)
    fun updateHabitName(id: Long, newName: String) = repo.updateHabitName(id, newName)
    fun deleteHabit(id: Long) = repo.deleteHabit(id)
    fun toggleHabitToday(id: Long) = repo.toggleHabitToday(id)
    fun habitsCompletedToday(): Int = repo.habitsCompletedToday() // added

    fun addMood(emoji: String, note: String?) = repo.addMood(emoji, note)
    fun deleteMood(id: Long) = repo.deleteMood(id) // added
    fun weeklyMoodCounts() = repo.weeklyMoodCounts()
    fun updateMoodNote(id: Long, note: String?) = repo.updateMoodNote(id, note)

    fun addWater(amount: Int) = repo.addWater(amount)
    fun setHydrationGoal(goal: Int) = repo.setHydrationGoal(goal)
}

class MainViewModelFactory(private val repo: WellnessRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
