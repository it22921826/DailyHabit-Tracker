package com.dailyhabbittracker.tracker.ui.hydration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dailyhabbittracker.R
import com.dailyhabbittracker.HabitrixApp
import com.dailyhabbittracker.databinding.FragmentHydrationBinding
import com.dailyhabbittracker.tracker.ui.shared.MainViewModel
import com.dailyhabbittracker.tracker.ui.shared.MainViewModelFactory
import com.dailyhabbittracker.tracker.work.HydrationReminderWorker
import java.util.concurrent.TimeUnit

class HydrationFragment: Fragment() {
    private var _binding: FragmentHydrationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((requireActivity().application as HabitrixApp).repository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHydrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonAdd250.setOnClickListener { viewModel.addWater(250) }
        binding.buttonGoal.setOnClickListener { promptGoal() }
        binding.buttonInterval.setOnClickListener { promptInterval() }
        binding.buttonSchedule.setOnClickListener { scheduleWork() }
        viewModel.hydration.observe(viewLifecycleOwner) { st ->
            binding.textStatus.text = getString(R.string.hydration_progress, st.consumedMl, st.goalMl)
        }
    }

    private var intervalMinutes: Long = 60 // default

    private fun promptGoal() {
        val input = EditText(requireContext())
        input.hint = getString(R.string.enter_goal_ml)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.set_goal)
            .setView(input)
            .setPositiveButton(R.string.save) { d,_ -> input.text.toString().toIntOrNull()?.let { viewModel.setHydrationGoal(it) }; d.dismiss() }
            .setNegativeButton(R.string.cancel,null)
            .show()
    }

    private fun promptInterval() {
        val input = EditText(requireContext())
        input.hint = getString(R.string.enter_interval_min)
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.set_interval)
            .setView(input)
            .setPositiveButton(R.string.save) { d,_ ->
                val v = input.text.toString().toLongOrNull()
                if (v!=null && v>=15) intervalMinutes = v else Toast.makeText(requireContext(), R.string.interval_too_low, Toast.LENGTH_SHORT).show()
                d.dismiss()
            }
            .setNegativeButton(R.string.cancel,null)
            .show()
    }

    private fun scheduleWork() {
        if (intervalMinutes < 15) {
            Toast.makeText(requireContext(), R.string.interval_too_low, Toast.LENGTH_SHORT).show(); return
        }
        val req = PeriodicWorkRequestBuilder<HydrationReminderWorker>(intervalMinutes, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            req
        )
        Toast.makeText(requireContext(), getString(R.string.schedule_reminders), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() { _binding = null; super.onDestroyView() }

    companion object { private const val WORK_NAME = "hydration_reminders" }
}

