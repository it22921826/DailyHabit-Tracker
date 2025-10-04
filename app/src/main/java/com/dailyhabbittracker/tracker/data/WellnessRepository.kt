package com.dailyhabbittracker.tracker.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dailyhabbittracker.tracker.model.Habit
import com.dailyhabbittracker.tracker.model.HydrationState
import com.dailyhabbittracker.tracker.model.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

/** Repository handling in-memory state + SharedPreferences persistence (no database). */
class WellnessRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("zenroute_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _habits = MutableLiveData<List<Habit>>(emptyList())
    val habits: LiveData<List<Habit>> = _habits

    private val _moods = MutableLiveData<List<MoodEntry>>(emptyList())
    val moods: LiveData<List<MoodEntry>> = _moods

    private val _hydration = MutableLiveData(HydrationState())
    val hydration: LiveData<HydrationState> = _hydration

    init {
        loadAll()
        ensureHydrationDate()
    }

    // Habit operations
    fun addHabit(name: String) {
        val list = _habits.value!!.toMutableList()
        list.add(Habit(name = name))
        _habits.value = list
        persistHabits()
    }

    fun updateHabitName(id: Long, newName: String) {
        val list = _habits.value!!.map { if (it.id == id) it.copy(name = newName, completedDays = it.completedDays) else it }
        _habits.value = list
        persistHabits()
    }

    fun deleteHabit(id: Long) {
        _habits.value = _habits.value!!.filterNot { it.id == id }
        persistHabits()
    }

    fun toggleHabitToday(id: Long) {
        val list = _habits.value!!.map { h ->
            if (h.id == id) { h.toggleToday(); h } else h
        }
        _habits.value = list
        persistHabits()
    }

    fun habitsCompletedToday(): Int {
        val today = LocalDate.now().toEpochDay()
        return _habits.value!!.count { it.completedDays.contains(today) }
    }

    // Mood operations
    fun addMood(emoji: String, note: String?) {
        val list = _moods.value!!.toMutableList()
        list.add(0, MoodEntry(emoji = emoji, note = note)) // newest first
        _moods.value = list
        persistMoods()
    }

    fun deleteMood(id: Long) {
        _moods.value = _moods.value!!.filterNot { it.id == id }
        persistMoods()
    }

    fun updateMoodNote(id: Long, note: String?) {
        _moods.value = _moods.value!!.map { if (it.id == id) it.copy(note = note) else it }
        persistMoods()
    }

    fun lastMoodEmoji(): String? = _moods.value?.firstOrNull()?.emoji

    fun weeklyMoodCounts(): Map<String, Int> {
        val cutoff = System.currentTimeMillis() - 6 * 24 * 3600_000L
        return _moods.value!!.filter { it.timestamp >= cutoff }.groupingBy { it.emoji }.eachCount()
    }

    // Hydration operations
    fun addWater(amount: Int) {
        ensureHydrationDate()
        val state = _hydration.value!!.copy()
        state.consumedMl += amount
        _hydration.value = state
        persistHydration()
    }

    fun setHydrationGoal(goal: Int) {
        val state = _hydration.value!!.copy(goalMl = goal)
        _hydration.value = state
        persistHydration()
    }

    private fun ensureHydrationDate() {
        val today = LocalDate.now().toEpochDay()
        val state = _hydration.value!!
        if (state.dateEpoch != today) {
            _hydration.value = HydrationState(goalMl = state.goalMl)
            persistHydration()
        }
    }

    // Persistence
    private fun loadAll() {
        _habits.value = readJsonList(KEY_HABITS)
        _moods.value = readJsonList(KEY_MOODS)
        _hydration.value = readJson(KEY_HYDRATION) ?: HydrationState()
    }

    private inline fun <reified T> readJson(key: String): T? {
        val json = prefs.getString(key, null) ?: return null
        return gson.fromJson(json, object: TypeToken<T>(){}.type)
    }

    private inline fun <reified T> readJsonList(key: String): List<T> {
        return readJson<List<T>>(key) ?: emptyList()
    }

    private fun persistHabits() = prefs.edit().putString(KEY_HABITS, gson.toJson(_habits.value)).apply()
    private fun persistMoods() = prefs.edit().putString(KEY_MOODS, gson.toJson(_moods.value)).apply()
    private fun persistHydration() = prefs.edit().putString(KEY_HYDRATION, gson.toJson(_hydration.value)).apply()

    companion object {
        private const val KEY_HABITS = "habits_json"
        private const val KEY_MOODS = "moods_json"
        private const val KEY_HYDRATION = "hydration_json"
    }
}
