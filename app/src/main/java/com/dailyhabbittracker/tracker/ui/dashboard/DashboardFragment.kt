package com.dailyhabbittracker.tracker.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dailyhabbittracker.HabitrixApp
import com.dailyhabbittracker.databinding.FragmentDashboardBinding
import com.dailyhabbittracker.tracker.ui.shared.MainViewModel
import com.dailyhabbittracker.tracker.ui.shared.MainViewModelFactory

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((requireActivity().application as HabitrixApp).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.habits.observe(viewLifecycleOwner) {
            binding.textHabits.text = getString(
                com.dailyhabbittracker.R.string.habits_completed_today,
                viewModel.habitsCompletedToday(),
                it.size
            )
        }
        viewModel.hydration.observe(viewLifecycleOwner) { h ->
            binding.textHydration.text = getString(
                com.dailyhabbittracker.R.string.hydration_progress,
                h.consumedMl,
                h.goalMl
            )
        }
        viewModel.moods.observe(viewLifecycleOwner) { moods ->
            val last = moods.firstOrNull()?.emoji ?: "-"
            binding.textMood.text = getString(com.dailyhabbittracker.R.string.last_mood, last)
        }
        binding.buttonMoodTrend.setOnClickListener {
            findNavController().navigate(com.dailyhabbittracker.R.id.moodTrendFragment)
        }
    }

    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}
