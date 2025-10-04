package com.dailyhabbittracker.tracker.ui.mood

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dailyhabbittracker.databinding.ItemMoodBinding
import com.dailyhabbittracker.tracker.model.MoodEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodAdapter(
    private val dateFmt: SimpleDateFormat = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()),
    private val onLongPress: (MoodEntry) -> Unit = {}
): ListAdapter<MoodEntry, MoodAdapter.MoodVH>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodVH {
        val b = ItemMoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MoodVH(b)
    }
    override fun onBindViewHolder(holder: MoodVH, position: Int) = holder.bind(getItem(position))

    inner class MoodVH(private val b: ItemMoodBinding): RecyclerView.ViewHolder(b.root) {
        fun bind(m: MoodEntry) {
            b.textEmoji.text = m.emoji
            b.textTime.text = dateFmt.format(Date(m.timestamp))
            b.textNote.text = m.note ?: ""
            b.textNote.visibility = if (m.note.isNullOrEmpty()) android.view.View.GONE else android.view.View.VISIBLE
            b.root.setOnLongClickListener { onLongPress(m); true }
        }
    }

    companion object {
        private val DIFF = object: DiffUtil.ItemCallback<MoodEntry>() {
            override fun areItemsTheSame(oldItem: MoodEntry, newItem: MoodEntry) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: MoodEntry, newItem: MoodEntry) = oldItem == newItem
        }
    }
}
