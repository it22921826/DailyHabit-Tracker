package com.dailyhabbittracker.tracker.ui.mood

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dailyhabbittracker.HabitrixApp
import com.dailyhabbittracker.databinding.FragmentMoodTrendBinding
import com.dailyhabbittracker.tracker.ui.shared.MainViewModel
import com.dailyhabbittracker.tracker.ui.shared.MainViewModelFactory
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class MoodTrendFragment: Fragment() {
    private var _binding: FragmentMoodTrendBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((requireActivity().application as HabitrixApp).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoodTrendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.moods.observe(viewLifecycleOwner) {
            val counts = viewModel.weeklyMoodCounts()
            if (counts.isEmpty()) {
                binding.textEmpty.visibility = View.VISIBLE
                binding.barChart.visibility = View.GONE
            } else {
                binding.textEmpty.visibility = View.GONE
                binding.barChart.visibility = View.VISIBLE
                val entries = counts.entries.mapIndexed { index, entry -> BarEntry(index.toFloat(), entry.value.toFloat()) }
                val dataSet = BarDataSet(entries, "Weekly Mood Count").apply {
                    color = Color.parseColor("#4CAF50")
                    valueTextColor = Color.BLACK
                    valueTextSize = 12f
                }
                binding.barChart.data = BarData(dataSet)
                binding.barChart.xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(counts.keys.toList())
                binding.barChart.xAxis.granularity = 1f
                binding.barChart.axisLeft.axisMinimum = 0f
                binding.barChart.axisRight.isEnabled = false
                binding.barChart.description = Description().apply { text = "" }
                binding.barChart.invalidate()
            }
        }
    }

    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}

