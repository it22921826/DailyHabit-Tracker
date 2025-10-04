package com.dailyhabbittracker.tracker.ui.habit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dailyhabbittracker.databinding.ItemHabitBinding
import com.dailyhabbittracker.tracker.model.Habit
import java.time.LocalDate

class HabitAdapter(
    private val onToggle: (Habit) -> Unit,
    private val onDelete: (Habit) -> Unit,
    private val onRename: (Habit) -> Unit
): ListAdapter<Habit, HabitAdapter.HabitVH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitVH {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitVH(binding)
    }

    override fun onBindViewHolder(holder: HabitVH, position: Int) = holder.bind(getItem(position))

    inner class HabitVH(private val binding: ItemHabitBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(h: Habit) {
            binding.textName.text = h.name
            val today = LocalDate.now().toEpochDay()
            binding.checkbox.isChecked = h.completedDays.contains(today)
            binding.checkbox.setOnClickListener { onToggle(h) }
            binding.root.setOnLongClickListener { onRename(h); true }
            binding.buttonDelete.setOnClickListener { onDelete(h) }
        }
    }

    companion object {
        private val DIFF = object: DiffUtil.ItemCallback<Habit>() {
            override fun areItemsTheSame(oldItem: Habit, newItem: Habit) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Habit, newItem: Habit) = oldItem == newItem
        }
    }
}

