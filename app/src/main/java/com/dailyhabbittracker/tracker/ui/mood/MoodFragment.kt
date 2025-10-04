package com.dailyhabbittracker.tracker.ui.mood

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dailyhabbittracker.HabitrixApp
import com.dailyhabbittracker.databinding.FragmentMoodBinding
import com.dailyhabbittracker.tracker.model.MoodEntry
import com.dailyhabbittracker.tracker.ui.shared.MainViewModel
import com.dailyhabbittracker.tracker.ui.shared.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MoodFragment: Fragment() {
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((requireActivity().application as HabitrixApp).repository)
    }

    private lateinit var adapter: MoodAdapter

    private val dateFmt = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault())

    private var selectedEmoji: String = "😀"
    private var selectedChip: android.widget.TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MoodAdapter(dateFmt) { showMoodOptions(it) }
        binding.recyclerMood.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMood.adapter = adapter
        val emojis = listOf("😀","🙂","😐","😔","😡","😴","🤒","🤩","😫","😎")
        emojis.forEach { e ->
            val chip = layoutInflater.inflate(android.R.layout.simple_list_item_1, binding.flexEmojis, false) as android.widget.TextView
            chip.text = e
            chip.textSize = 24f
            chip.setPadding(32,20,32,20)
            chip.setOnClickListener { selectEmoji(e, chip) }
            binding.flexEmojis.addView(chip)
        }
        // Preselect first
        (binding.flexEmojis.getChildAt(0) as? android.widget.TextView)?.let { selectEmoji(selectedEmoji, it) }

        binding.buttonAddMood.setOnClickListener { addMood(selectedEmoji) }
        binding.buttonShare.setOnClickListener { shareSummary() }
        viewModel.moods.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.textEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            updateWeeklySummary()
        }
    }

    private fun selectEmoji(emoji: String, chip: android.widget.TextView) {
        selectedEmoji = emoji
        selectedChip?.alpha = 1f
        chip.alpha = 0.5f
        selectedChip = chip
    }

    private fun updateWeeklySummary() {
        val counts = viewModel.weeklyMoodCounts()
        if (counts.isEmpty()) {
            binding.textSummary.visibility = View.GONE
            return
        }
        val summary = counts.entries.sortedByDescending { it.value }
            .joinToString(separator = "  ") { "${it.key}:${it.value}" }
        binding.textSummary.text = getString(com.dailyhabbittracker.R.string.mood_week_summary, summary)
        binding.textSummary.visibility = View.VISIBLE
    }

    private fun showMoodOptions(entry: MoodEntry) {
        val items = arrayOf(
            getString(com.dailyhabbittracker.R.string.edit_note),
            getString(com.dailyhabbittracker.R.string.delete_entry)
        )
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(com.dailyhabbittracker.R.string.mood_options)
            .setItems(items) { d, which ->
                when(which) {
                    0 -> editMoodNote(entry)
                    1 -> confirmDelete(entry)
                }
                d.dismiss()
            }
            .setNegativeButton(com.dailyhabbittracker.R.string.cancel, null)
            .show()
    }

    private fun editMoodNote(entry: MoodEntry) {
        val input = android.widget.EditText(requireContext())
        input.setText(entry.note ?: "")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(com.dailyhabbittracker.R.string.edit_note)
            .setView(input)
            .setPositiveButton(com.dailyhabbittracker.R.string.update) { d,_ ->
                val newNote = input.text.toString().trim().ifEmpty { null }
                viewModel.updateMoodNote(entry.id, newNote)
                d.dismiss()
            }
            .setNegativeButton(com.dailyhabbittracker.R.string.cancel, null)
            .show()
    }

    private fun confirmDelete(entry: MoodEntry) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setMessage(getString(com.dailyhabbittracker.R.string.delete_entry))
            .setPositiveButton(com.dailyhabbittracker.R.string.delete) { _, _ -> viewModel.deleteMood(entry.id) }
            .setNegativeButton(com.dailyhabbittracker.R.string.cancel, null)
            .show()
    }

    private fun addMood(emoji: String) {
        val note = binding.editNote.text.toString().trim().ifEmpty { null }
        viewModel.addMood(emoji, note)
        binding.editNote.setText("")
    }

    private fun shareSummary() {
        val moods: List<MoodEntry> = viewModel.moods.value ?: emptyList()
        if (moods.isEmpty()) return
        val counts = viewModel.weeklyMoodCounts()
        val countsLine = if (counts.isNotEmpty()) counts.entries.joinToString { "${it.key}:${it.value}" } else ""
        val summary = buildString {
            append("Mood Summary\n")
            if (countsLine.isNotEmpty()) append("Last 7 days: ").append(countsLine).append("\n\n")
            append("Recent entries:\n")
            moods.take(10).forEach { m ->
                append(dateFmt.format(Date(m.timestamp))).append(" - ").append(m.emoji)
                m.note?.let { append(" (").append(it).append(")") }
                append('\n')
            }
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, summary)
        }
        startActivity(Intent.createChooser(intent, "Share mood summary"))
    }

    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
