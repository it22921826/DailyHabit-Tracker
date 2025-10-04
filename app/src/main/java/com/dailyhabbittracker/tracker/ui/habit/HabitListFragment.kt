package com.dailyhabbittracker.tracker.ui.habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dailyhabbittracker.R
import com.dailyhabbittracker.HabitrixApp
import com.dailyhabbittracker.databinding.FragmentHabitListBinding
import com.dailyhabbittracker.tracker.model.Habit
import com.dailyhabbittracker.tracker.ui.shared.MainViewModel
import com.dailyhabbittracker.tracker.ui.shared.MainViewModelFactory

class HabitListFragment: Fragment() {
    private var _binding: FragmentHabitListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory((requireActivity().application as HabitrixApp).repository)
    }
    private lateinit var adapter: HabitAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHabitListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = HabitAdapter(onToggle = { viewModel.toggleHabitToday(it.id) }, onDelete = { confirmDelete(it) }, onRename = { habit -> renameHabit(habit) })
        binding.recyclerHabits.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHabits.adapter = adapter
        binding.fabAddHabit.setOnClickListener { addHabitDialog() }
        viewModel.habits.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.textEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun addHabitDialog() {
        val input = EditText(requireContext())
        input.hint = getString(R.string.habit_name_hint)
        AlertDialog.Builder(requireContext()).setTitle(R.string.add_habit).setView(input)
            .setPositiveButton(R.string.save) { d,_ -> val name = input.text.toString().trim(); if(name.isNotEmpty()) viewModel.addHabit(name); d.dismiss() }
            .setNegativeButton(R.string.cancel,null).show()
    }

    private fun renameHabit(habit: Habit) {
        val input = EditText(requireContext())
        input.setText(habit.name)
        AlertDialog.Builder(requireContext()).setTitle(R.string.add_habit).setView(input)
            .setPositiveButton(R.string.save) { d,_ -> val name = input.text.toString().trim(); if(name.isNotEmpty()) viewModel.updateHabitName(habit.id, name); d.dismiss() }
            .setNegativeButton(R.string.cancel,null).show()
    }

    private fun confirmDelete(habit: Habit) {
        AlertDialog.Builder(requireContext()).setMessage("Delete ${habit.name}?")
            .setPositiveButton(R.string.delete) {_,_-> viewModel.deleteHabit(habit.id)}
            .setNegativeButton(R.string.cancel,null).show()
    }

    override fun onDestroyView() { _binding = null; super.onDestroyView() }
}

